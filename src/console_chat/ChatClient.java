package console_chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 8189;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    boolean serverIsOnline;
    Thread t1;
    Thread t2;

    public static void main(String[] args) throws IOException {
        ChatClient chat = new ChatClient();
        chat.openConnection();
        chat.t2 = new Thread(()-> {
            while (chat.serverIsOnline) {
                chat.sendMessage();
            }
        });
        chat.t2.start();
    }

    public void openConnection() throws IOException {
        socket = new Socket(SERVER_ADDR, SERVER_PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        serverIsOnline = true;
        t1 = new Thread(()-> {
                try {
                    while (!t1.isInterrupted()) {
                        String strFromServer = in.readUTF();
                        if (strFromServer.equalsIgnoreCase("/end")) {
                            serverIsOnline = false;
                            System.out.println("Потеряно соединение с сервером!");
                            System.exit(0);
                        }
                        System.out.println("Сервер: " + strFromServer);
                    }
                } catch (Exception e) {
                    System.out.println("Потеряно соединение с сервером!");
                    System.exit(0);
                }
        });
        t1.start();
    }

    public void sendMessage() {
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        if (!s.equals("")) {
            try {
                if (s.equals("/end")) {
                    serverIsOnline = false;
                    out.writeUTF(s);
                    System.out.println("Вы вышли из чата!");
                    System.exit(0);
                    return;
                }
                out.writeUTF(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
