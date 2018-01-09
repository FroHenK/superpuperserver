package mem.sirius.example.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class socket_connection extends Thread {
    private Socket socket;

    public socket_connection (Socket ss) {
        socket = ss;
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
                    System.out.println("New zap " + line);
                    get_list response = new get_list(request);
                    String ans = response.getResponse().toString();
                    if (ans != null) {
                        System.out.println("New answer for getlist " + ans);
                        System.out.println();
                        out.writeUTF(ans);
                        out.flush();
                    }
                    continue;
                }
                if (request.links.get("type").equals("close")) {
                    System.out.println("User decide to close");
                    socket.close();
                    break;
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }

    }
}
