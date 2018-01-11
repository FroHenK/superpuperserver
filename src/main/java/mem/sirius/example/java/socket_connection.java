package mem.sirius.example.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;


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
            String httpRequest = "";
            while (true) {
                int i = sin.read();
                data.add(i);
                if (data.size() >= 4 && data.get(data.size() - 1) == '\n' && data.get(data.size() - 2) == '\r' && data.get(data.size() - 3) == '\n' && data.get(data.size() - 4) == '\r')
                    break;
                httpRequest += (char) i;
            }

            System.out.println(Arrays.toString(httpRequest.split("\r\n")));

            System.out.println(httpRequest);

            String defaultResponse = "HTTP/1.1 101 Switching Protocols\r\n" +
                    "Upgrade: websocket\r\n" +
                    "Connection: Upgrade\r\n" +
                    "Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=\r\n" +
                    "Sec-WebSocket-Protocol: chat\r\n\r\n";
            sout.write(defaultResponse.getBytes());
            sout.flush();
            //sout.write("kek".getBytes());
            //sout.close();

            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);

            String line = null;
            while (true) {
                try {
                    line = in.readUTF();
                } catch (Exception x) {
                    x.printStackTrace();
                    socket.close();
                    break;
                }
                Request request = new Request(line);
                if (request.links.get("type").equals("get_list")) {
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
                if (request.links.get("type").equals("vk_register")) {
                    user_process response = new user_process(request);
                    String ans = response.vkLoginOrCreate().toString();
                    if (ans != null) {
                        System.out.printf("new answer for vk_register %s%n", ans);
                        System.out.println();
                        out.writeUTF(ans);
                        out.flush();
                    }
                }

                if (request.links.get("type").equals("logout_auth_token")) {
                    user_process response = new user_process(request);
                    String ans = response.killSession().toString();
                    if (ans != null) {
                        System.out.printf("new answer for logout_auth_token %s%n", ans);
                        System.out.println();
                        out.writeUTF(ans);
                        out.flush();
                    }
                }

                if (request.links.get("type").equals("validate_auth_token")) {
                    user_process response = new user_process(request);
                    String ans = response.validateSession().toString();
                    if (ans != null) {
                        System.out.printf("new answer for validate_auth_token %s%n", ans);
                        System.out.println();
                        out.writeUTF(ans);
                        out.flush();
                    }
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
