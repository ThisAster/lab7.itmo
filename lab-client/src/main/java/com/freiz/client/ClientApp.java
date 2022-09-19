package com.freiz.client;

import com.freiz.common.commands.AbstractCommand;
import com.freiz.common.commands.AddCommand;
import com.freiz.common.commands.AddIfMaxCommand;
import com.freiz.common.commands.AddIfMinCommand;
import com.freiz.common.commands.ClearCommand;
import com.freiz.common.commands.CountGreaterThanHeartCountCommand;
import com.freiz.common.commands.CountLessThanMeleeWeaponMeleeWeaponCommand;
import com.freiz.common.commands.FilterByWeaponTypeCommand;
import com.freiz.common.commands.HelpCommand;
import com.freiz.common.commands.HistoryCommand;
import com.freiz.common.commands.InfoCommand;
import com.freiz.common.commands.LoginCommand;
import com.freiz.common.commands.RegisterCommand;
import com.freiz.common.commands.RemoveByIdCommand;
import com.freiz.common.commands.ShowCommand;
import com.freiz.common.commands.UpdateCommand;
import com.freiz.common.data.User;
import com.freiz.common.dto.CommandResultDto;
import com.freiz.common.exception.InvalidRequestException;
import com.freiz.common.network.Request;
import com.freiz.common.util.InputManager;
import com.freiz.common.util.OutputManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import static com.freiz.client.Crypto.encryptPassword;

public class ClientApp {
    private final DatagramSocket client;
    private final Map<String, AbstractCommand> commandsClientMap = new HashMap<>();
    private final Logger logger;

    public ClientApp(DatagramSocket client, InputManager inputManager, OutputManager outputManager) throws IOException {
        this.client = client;
        this.logger = Logger.getLogger("log");
        File lf = new File("client.log");
        FileHandler fh = new FileHandler(lf.getAbsolutePath(), true);
        logger.addHandler(fh);
        commandsClientMap.put("add", new AddCommand(outputManager, inputManager));
        commandsClientMap.put("add_if_max", new AddIfMaxCommand(inputManager, outputManager));
        commandsClientMap.put("add_if_min", new AddIfMinCommand(inputManager, outputManager));
        commandsClientMap.put("clear", new ClearCommand());
        commandsClientMap.put("count_greater_than_heart_count", new CountGreaterThanHeartCountCommand());
        commandsClientMap.put("help", new HelpCommand());
        commandsClientMap.put("history", new HistoryCommand());
        commandsClientMap.put("info", new InfoCommand());
        commandsClientMap.put("show", new ShowCommand());
        commandsClientMap.put("update", new UpdateCommand(inputManager, outputManager));
        commandsClientMap.put("count_less_than_melee_weapon", new CountLessThanMeleeWeaponMeleeWeaponCommand());
        commandsClientMap.put("filter_by_weapon_type", new FilterByWeaponTypeCommand());
        commandsClientMap.put("remove_by_id", new RemoveByIdCommand());
    }

    public boolean exeFile(String input, InputManager inputManager1) {
        if (input.startsWith("execute_script")) {
            String[] args = input.split(" ");
            if (args.length == 1 || args[1] == "") {
                logger.info("bad args");
                return true;
            }
            File fileName = new File(args[1]);
            if (fileName.canRead()) {
                try { // TODO: при исполнении скрипта нужно сделать проверку, если есть команда
                      // исполнение скрипта рекурсивно, то нужно сделать проверку, что я не открываю
                      // его заново
                    inputManager1.connectToFile(fileName);
                    logger.fine("successful executing");
                    return true;
                } catch (UnsupportedOperationException | IOException e) {
                    logger.severe("cant connect");
                    return true;
                }
            }
        } else if (input.startsWith("exit")) {
            logger.fine("successful exit");
            System.exit(-1);
        }
        return false;
    }

    public Object deserialize(byte[] data) {
        try {
            ByteArrayInputStream bos = new ByteArrayInputStream(data);
            ObjectInputStream objectOutputStream = new ObjectInputStream(bos);
            return objectOutputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendThenRecieve() {
        InputManager scanner = new InputManager(System.in);
        try {
            authUser();
        } catch (InvalidRequestException | IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                String messageToSend = scanner.nextLine();
                String[] args = messageToSend.split(" ");
                if (exeFile(messageToSend, scanner)) {
                    continue;
                }
                if (commandsClientMap.containsKey(args[0])) {
                    AbstractCommand someCommandSend = commandsClientMap.get(args[0]);
                    Request request = someCommandSend.packageRequest(args);
                    logger.info("created request, that in packed arguments by command");
                    request.setUser(LoginHandler.getUser());
                    sendMessage(getDatagramPacket(request));
                    Object data = getAnswerFromServer();
                    logger.info("deserialize data");
                    CommandResultDto commandResultDto = new CommandResultDto(data.toString());
                    System.out.println(commandResultDto);
                } else {
                    System.out.println("Class not found, please write a help to see look the available command");
                }
            } catch (IOException | InvalidRequestException e) {
                e.printStackTrace();
            }
        }
    }

    private Object getAnswerFromServer() throws IOException {
        final int constanta = 16;
        int maxPacket = 1 << constanta - 1;
        byte[] secondaryBuffer = new byte[maxPacket];
        DatagramPacket packetFromServer = new DatagramPacket(secondaryBuffer, maxPacket);
        logger.info("created packet, that will send to server");
        client.receive(packetFromServer);
        logger.info("By client was received data from server");
        secondaryBuffer = packetFromServer.getData();
        return deserialize(secondaryBuffer);
    }

    private DatagramPacket getDatagramPacket(Request request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(request);
        return new DatagramPacket(baos.toByteArray(), baos.size());
    }

    private void sendMessage(DatagramPacket datagramPacket) throws IOException {
        client.send(datagramPacket);
        logger.info("By client send data to server");
    }

    private void authUser() throws InvalidRequestException, IOException {
        Scanner consoleScanner = new Scanner(System.in);
        while (LoginHandler.getUser() == null) {
            System.out.println("Do you want to login? (else register) [y, any letter]");
            String yn = consoleScanner.nextLine();
            System.out.println("Enter your login:");
            String login = consoleScanner.nextLine();
            System.out.println("Enter your password:");
            String password = consoleScanner.nextLine();
            Request request;
            if ("y".equalsIgnoreCase(yn)) {
                request = new LoginCommand().packageRequest(new Object[] {});
            } else {
                request = new RegisterCommand().packageRequest(new Object[] {});
            }
            User user = new User(login, encryptPassword(password));
            sendAuthCommand(request, user);
        }
    }

    private void sendAuthCommand(Request request, User user) throws IOException {
        final int limitLengthAnswer = 3;
        request.setUser(user);
        DatagramPacket datagramPacket = getDatagramPacket(request);
        client.send(datagramPacket);
        Object answerFromServer = getAnswerFromServer();
        String[] answer = answerFromServer.toString().split("="); // {output = success your id = 5}
        if (answer.length == limitLengthAnswer) {
            try {
                user.setId(Integer.parseInt(answer[2].replaceAll("'}", "")));
            } catch (NumberFormatException e) {
                return;
            }
            LoginHandler.setUser(user);
            logger.info("success login!");
        }
    }
}
