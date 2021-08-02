package io.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class SelectorServer {
    public static void main(String[] args) throws Exception{
        ServerSocketChannel server = ServerSocketChannel.open();//建立服务通道
        server.bind(new InetSocketAddress(8080));
        server.configureBlocking(false);//设置为非阻塞

        Selector selector = Selector.open();//找内核要申请一个多路复用器

        SelectionKey sk = server.register(selector, SelectionKey.OP_ACCEPT);//最初是给一个服务器通道注册的事件都是accept

        sk.attach(new Acceptor(server,selector));//绑定接受事件的处理器

       /* ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

        writeBuffer.put("recive".getBytes());
        writeBuffer.flip();*/

        while (true){
            int select = selector.select();//就绪事件到达之前，阻塞
            Set<SelectionKey> selectionKeys = selector.selectedKeys();//拿到本次select获取的就绪事件
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();

                if(key.isAcceptable()){
                    SocketChannel accept = server.accept();
                    accept.configureBlocking(false);
                    accept.register(selector,SelectionKey.OP_READ);

                }else if(key.isReadable()){
                    SocketChannel channel =(SocketChannel) key.channel();

                    Selector selector1 = key.selector();//该通道对应的selector
                    key.attach(new Object());//添加附加
                    Object attachment = key.attachment();//取出附加对象

                }else if(key.isWritable()){

                }
            }
        }


    }
}
