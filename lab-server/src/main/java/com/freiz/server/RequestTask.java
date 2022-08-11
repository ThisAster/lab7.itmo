package com.freiz.server;

import com.freiz.common.commands.AbstractCommand;
import com.freiz.common.dto.CommandResultDto;
import com.freiz.common.network.Request;
import com.freiz.common.util.CollectionManager;
import com.freiz.common.util.HistoryManagerImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class RequestTask implements Callable<ByteBuffer> {

    private final Logger logger = Logger.getLogger("log");
    private final CollectionManager collectionManager;
    private final HistoryManagerImpl historyManagerImpl;
    private ByteBuffer buffer;

    public RequestTask(ByteBuffer byteBuffer, CollectionManager collectionManager, HistoryManagerImpl historyManager) throws IOException {
        File file = new File("server.log");
        FileHandler fh = new FileHandler(file.getAbsolutePath(), true);
        logger.addHandler(fh);
        this.buffer = byteBuffer;
        this.collectionManager = collectionManager;
        this.historyManagerImpl = historyManager;
    }

    @Override
    public ByteBuffer call() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(buffer.array());
        ObjectInputStream is = new ObjectInputStream(in);
        Request request = (Request) is.readObject();
        logger.info("receive request, deserialize request");
        String commandMessage = (String) request.getCommandName();
        CommandResolver commandResolver = CommandResolver.getInstance();
        AbstractCommand commandExe = commandResolver.resolveCommand(commandMessage);
        CommandResultDto commandResultDto = commandExe.execute(request, collectionManager, historyManagerImpl);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(commandResultDto);
        logger.info("serialize result");
        byte[] secondaryBuffer = baos.toByteArray();
        return ByteBuffer.wrap(secondaryBuffer);
    }
}
