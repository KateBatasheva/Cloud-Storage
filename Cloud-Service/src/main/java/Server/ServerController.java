package Server;

import java.io.*;
import java.net.Socket;

public class ServerController {

    MainServer server;
    Socket socket;

    private final int bufferSize = 1024;

    public ServerController(MainServer server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;

        try (InputStream in = new DataInputStream(socket.getInputStream())) {
            int read;
            byte[] buffer = new byte[bufferSize];
            while ((read = in.read(buffer)) != -1) {
                save(buffer, socket.getLocalAddress().toString(),read);
            }
        }
    }
    public void save (byte [] bytes, String clientDirectory, int bufferLenght) throws IOException {
       try (OutputStream out = new FileOutputStream(clientDirectory,true)){
           out.write(bytes,0,bufferLenght);
           }
    }
}
