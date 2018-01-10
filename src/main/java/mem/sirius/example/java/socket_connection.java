package mem.sirius.example.java;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class socket_connection extends Thread {
    private Socket socket;

    public socket_connection(Socket ss) {
        socket = ss;
    }

    public void run() {
        System.out.println("Got a client :) ... Finally, someone saw me through all the cover!");
        System.out.println();
        try {
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();
            ArrayList<Integer> data = new ArrayList<Integer>();
            while (true) {
                data.add(sin.read());
                if (data.size() >= 4 && data.get(data.size() - 1) == '\n' && data.get(data.size() - 2) == '\r' && data.get(data.size() - 3) == '\n' && data.get(data.size() - 4) == '\r')
                    break;
            }

            String defaultResponse = "HTTP/1.1 200 OK\r\n" +
                    "Server: Meme\r\n" +
                    "X-Powered-By: Some guys laptop\r\n" +
                    "Content-Language: ru\r\n" +
                    "Content-Type: text/html; charset=utf-8\r\n\r\n";
            sout.write(defaultResponse.getBytes());

            sout.write("kek".getBytes());
            sout.close();

//            DataInputStream in = new DataInputStream(sin);
//            DataOutputStream out = new DataOutputStream(sout);
//
//            String line = null;
//            while (true) {
//                try {
//                    line = in.readUTF();
//                } catch (Exception x) {
//                    socket.close();
//                    break;
//                }
//                Request request = new Request(line);
//                if (request.links.get("type").equals("get_list")) {
//                    get_list response = new get_list(request);
//                    String ans = response.getResponse().toString();
//                    if (ans != null) {
//                        System.out.println("New answer for getlist " + ans);
//                        System.out.println();
//                        out.writeUTF(ans);
//                        out.flush();
//                    }
//                    continue;
//                }
//                if (request.links.get("type").equals("close")) {
//                    System.out.println("User decide to close");
//                    socket.close();
//                    break;
//                }
//            }
        } catch (Exception x) {
            x.printStackTrace();
        }

    }
}
