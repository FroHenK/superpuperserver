package mem.sirius.example.java;


import java.net.ServerSocket;
import java.net.Socket;

// Zdes bil ya
public class App {
    public static final String PORT = "PORT";
    public static int port = 0;
    public static final String myip = "http://memsproblems.000webhostapp.com/ac/";
    public static final String mem_dir = "memes";

    public static void main(String[] args) {
        port = Integer.parseInt(System.getenv(PORT) != null ? System.getenv(PORT) : "5000");
        System.out.println("Port is :" + port);
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
