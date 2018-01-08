package mem.sirius.example.java;


import java.net.*;

public class App {
    public static final int port = 6666;
    public static final int serverport = 80;
    public static final String mem_dir = "/home/azat/memes";
    public static final String myip = "10.21.135.98";

    public static void main(String[] args) {
        while (true)
            try {
                ServerSocket ss = new ServerSocket(port);
                System.out.println("Waiting for a client...");
                Socket socket = ss.accept();
                new Thread(new socket_connection(socket)).start();
            } catch (Exception x) {
                x.printStackTrace();
            }

    }
}
