package mem.sirius.example.java;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static mem.sirius.example.java.App.myip;
import static mem.sirius.example.java.App.serverport;

public class socket_connection extends Thread {
    private Socket socket;

    public socket_connection (ServerSocket ss) {
        try {
            socket = ss.accept();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Got a client :) ... Finally, someone saw me through all the cover!");
        System.out.println();
        try {
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();

            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);

            String line = null;
            while (true) {
                line = in.readUTF();
                System.out.println("The dumb client just sent me this line : " + line);
                System.out.println("I'm sending it back...");
                out.writeUTF(line);
                out.flush();
                parse_fol dir = new parse_fol();
                File memes = new File("/home/azat/memes");
                dir.processFilesFromFolder(memes);
                System.out.println(dir.getLinksAccepted(null, 5, "http://" + myip + ":" + serverport + "/"));
                System.out.println("Waiting for the next line...");
                System.out.println();
            }
        } catch (Exception x) {
            x.printStackTrace();
        }

    }
}
