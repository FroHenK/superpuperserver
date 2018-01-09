package mem.sirius.example.java;


import java.net.ServerSocket;
import java.net.Socket;

public class App {
    public static final int port = 6666;
    public static final String myip = "http://memsproblems.000webhostapp.com/ac/";
    public static final String mem_dir = "/home/azat/memes";

    public static void main(String[] args) {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
        } catch (Exception x) {
            x.printStackTrace();
        }
        while (true)
            try {
                System.out.println("Waiting for a client...");
                Socket socket = ss.accept();
                new Thread(new socket_connection(socket)).start();
            } catch (Exception x) {
                x.printStackTrace();
            }

    }
}
