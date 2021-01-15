package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class MainServer {

    private List<ServerController> clients;

    public MainServer() {
        ServerSocket server = null;
        Socket socket = null;
        final int Port = 8189;

        try {
            server = new ServerSocket(Port);
            System.out.println("Server is on");

            while (true){
                socket = server.accept();
                System.out.println("Client is connected, name " + socket.getRemoteSocketAddress());
                new ServerController(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }
}
