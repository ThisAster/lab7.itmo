package com.freiz.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class SenderTask implements Runnable {

    private final ByteBuffer buffer;
    private final SocketAddress socketAddress;
    private final DatagramChannel server;

    public SenderTask(ByteBuffer buffer, SocketAddress socketAddress, DatagramChannel server) {
        this.buffer = buffer;
        this.socketAddress = socketAddress;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            server.send(buffer, socketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
