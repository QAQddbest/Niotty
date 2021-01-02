package niotty.nio.echo;

import niotty.nio.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class EchoServer extends Server {

    private static final int DEFAULT_PORT = 2333;
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final Charset charset = StandardCharsets.UTF_8;
    private int port;
    private int bufferSize;
    private Selector selector;

    public EchoServer(int port, int bufferSize) {
        this.port = port;
        this.bufferSize = bufferSize;
    }

    public EchoServer(int bufferSize) {
        this(DEFAULT_PORT, bufferSize);
    }

    public EchoServer() {
        this(DEFAULT_BUFFER_SIZE);
    }

    @Override
    public void run() {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        selector = Selector.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress(port));
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器初始化完成");
    }

    private void listen() throws IOException {
        System.out.println("服务器开始监听端口" + port);
        while (true) {
            selector.select(1000);
            Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
            while (ite.hasNext()) {
                SelectionKey key = ite.next();
                ite.remove();
                SocketChannel client;
                if (key.isAcceptable()) {
                    System.out.println("收到TCP连接");
                    ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                    client = serverChannel.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ,
                            ByteBuffer.allocate(bufferSize));
                } else if (key.isReadable()) {
                    client = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    buffer.clear();
                    int count = client.read(buffer);
                    if (count > 0) {
                        buffer.flip();
                        CharBuffer charBuffer = charset.decode(buffer);
                        System.out.println("收到数据: " + charBuffer.toString());
                        buffer.rewind();
                        client.write(buffer);
                    } else {
                        System.out.println("客户端异常关闭");
                        client.close();
                    }
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        selector.close();
    }
}
