package com.freiz.server;

import com.freiz.common.network.Request;
import com.freiz.common.util.CollectionManager;
import com.freiz.common.util.HistoryManagerImpl;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.RecursiveTask;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class RequestTask extends RecursiveTask<ByteBuffer> {

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

    @SneakyThrows
    @Override
    protected ByteBuffer compute() {
        ByteArrayInputStream in = new ByteArrayInputStream(buffer.array());
        ObjectInputStream is = new ObjectInputStream(in);
        Request request = (Request) is.readObject();
        logger.info("receive request, deserialize request");
        Task task = new Task(buffer, request, collectionManager, historyManagerImpl);
        task.start();
        task.join();
        return ByteBuffer.wrap(task.getBuffer().array());
    }
}
