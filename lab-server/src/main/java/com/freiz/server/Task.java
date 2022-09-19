package com.freiz.server;

import com.freiz.common.commands.AbstractCommand;
import com.freiz.common.dto.CommandResultDto;
import com.freiz.common.network.Request;
import com.freiz.common.util.CollectionManager;
import com.freiz.common.util.HistoryManagerImpl;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class Task extends Thread {

    private final Logger logger = Logger.getLogger("log");

    private ByteBuffer buffer;
    private Request request;
    private final CollectionManager collectionManager;
    private final HistoryManagerImpl historyManagerImpl;

    public Task(ByteBuffer buffer, Request request, CollectionManager collectionManager, HistoryManagerImpl historyManager) {
        this.buffer = buffer;
        this.request = request;
        this.collectionManager = collectionManager;
        this.historyManagerImpl = historyManager;
    }

    public void run() {
        try {
            String commandMessage = request.getCommandName();
            CommandResolver commandResolver = CommandResolver.getInstance();
            AbstractCommand commandExe = commandResolver.resolveCommand(commandMessage);
            CommandResultDto commandResultDto = commandExe.execute(request, collectionManager, historyManagerImpl);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(commandResultDto);
            logger.info("serialize result");
            byte[] secondaryBuffer = baos.toByteArray();
            buffer = ByteBuffer.wrap(secondaryBuffer);
        } catch (Exception e) {
            buffer.clear();
        }
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
