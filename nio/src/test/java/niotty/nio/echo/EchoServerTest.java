package niotty.nio.echo;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class EchoServerTest {

    @Test
    void run() {
        try (
                EchoServer server = new EchoServer();
        ) {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}