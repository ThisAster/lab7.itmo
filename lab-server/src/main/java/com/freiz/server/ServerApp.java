package com.freiz.server;

import com.freiz.common.db.Database;
import com.freiz.common.util.CollectionManager;
import com.freiz.common.util.FileManager;
import com.freiz.common.util.HistoryManagerImpl;
import com.freiz.common.util.JsonParser;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class ServerApp {
    final int constanta = 16;
    private final int maxPacket = 1 << constanta - 1;
    private final int port;
    private final CollectionManager collectionManager = new CollectionManager();
    private final HistoryManagerImpl historyManagerImpl = new HistoryManagerImpl();
    private final FileManager fileManager;
    private final Logger logger;
    private final CommandResolver commandResolver;

    public ServerApp(int port, FileManager fileManager) throws IOException {
        this.port = port;
        this.fileManager = fileManager;
        this.logger = Logger.getLogger("log");
        File file = new File("server.log");
        FileHandler fh = new FileHandler(file.getAbsolutePath(), true);
        logger.addHandler(fh);
        collectionManager.collectionManagerLoadRecordsFromDB();
        this.commandResolver = CommandResolver.getInstance();
        Database.getInstance();
    }

    public void start() throws ClassNotFoundException {
        try (DatagramChannel server = DatagramChannel.open()) {
            InetSocketAddress iAddress = new InetSocketAddress(port);
            server.bind(iAddress);
            server.configureBlocking(false);
            logger.info("start work");
            new Thread(() -> {
                try {
                    acceptConsoleInput();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            listen(server);
            logger.info("wait connection");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(port);
            System.out.println("DatagramChannel fall");
        } catch (Exception e) {
             throw new RuntimeException(e);
        }
    }

    private void listen(DatagramChannel server) {
        ForkJoinPool executor = new ForkJoinPool();
        ExecutorService sender = Executors.newFixedThreadPool(2);
        while (true) {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(maxPacket);
                SocketAddress address = server.receive(buffer);

                if (address != null) {
                    buffer.flip();
                    ByteBuffer result = executor.invoke(new RequestTask(buffer, collectionManager, historyManagerImpl));
                    sender.submit(new SenderTask(result, address, server));
                    logger.info("By server send data to client");
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.severe("cant connection");
                break;
            }
        }
    }

    public boolean acceptConsoleInput() throws IOException {
        if (System.in.available() > 0) {
            Scanner in = new Scanner(System.in);
            String command = in.nextLine();
            switch (command) {
                case "save":
                    fileManager.save(JsonParser.serialize(collectionManager.getAll()));
                    logger.fine("successful save");
                    break;
                case "exit":
                    System.out.println("Shutting down");
                    logger.info("server down");
                    System.exit(0);
                    return true; 
                default:
                    System.out.println("Unknown command. Available commands are: save, exit");
            }
        }
        return false;
    }
}
