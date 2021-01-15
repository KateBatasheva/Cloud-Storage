package ClientController;

import java.io.*;
import java.net.Socket;

public class ClientController {

    final String IP_ADDRESS = "localhost";
    final int Port = 8189;

    private final int bufferSize = 1024;


    public ClientController() throws IOException {
        try (Socket socket = new Socket(IP_ADDRESS, Port)) {
            try (OutputStream out = new DataOutputStream(socket.getOutputStream())) {
                try (InputStream in = new FileInputStream(getClass().getResource("testFile").getPath())) {
                    int read;
                    byte[] buffer = new byte[bufferSize];
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                }
            }
        }
    }
}
