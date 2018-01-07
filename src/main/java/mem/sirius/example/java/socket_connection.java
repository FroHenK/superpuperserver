package mem.sirius.example.java;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;


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
                Request request = new Request(line);
                if (request.links.get("type").equals("get_list")){
                    get_list response = new get_list(request);
                    String ans = response.toString();
                    if (ans != null) {
                        System.out.println("New answer for getlist " + ans);
                        System.out.println();
                        out.writeUTF(ans);
                        out.flush();
                    }
                    continue;
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }

    }
}
