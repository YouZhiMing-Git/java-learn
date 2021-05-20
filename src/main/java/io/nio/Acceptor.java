package io.nio;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class Acceptor {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public Acceptor(ServerSocketChannel serverSocketChannel, Selector selector) {
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
    }
}
