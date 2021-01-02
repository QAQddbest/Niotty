package niotty.nio;

import java.io.Closeable;
import java.io.IOException;

public abstract class Server implements Runnable, Closeable {

    @Override
    public abstract void close() throws IOException;

    @Override
    public abstract void run();

}