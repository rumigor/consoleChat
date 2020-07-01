package console_chat;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.util.Scanner;

public class ChatServer {
    ServerSocket server = null;
    Socket socket = null;
    DataInputStream in = null;
    DataOutputStream out = null;
    boolean clientIsOnline;
    static Scanner sc = new Scanner(System.in);

    final int PORT = 8189;
    public static void main(String[] args) throws IOException {
        ChatServer chatServer = new ChatServer();
        chatServer.startServer();

    }
    public void startServer() {


        try {
            server = new ServerSocket(PORT);
            System.out.println("Ожидаем подключения клиента");
            System.out.println("-----------------");
            socket = server.accept();
            System.out.println("Клиент подключился");
            System.out.println("---Чат готов для обмена сообщениями---");

            clientIsOnline = true;

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            Thread t1 = new Thread(() -> {

                    if (!clientIsOnline) Thread.currentThread().interrupt();
                    while(clientIsOnline) {
                        sendMessage();
                    }

            });
            t1.start();

            while (true) {

                String str = in.readUTF();

                if(str.equals("/end")){
                    out.writeUTF("/end");
                    System.out.println("Клиент отключился");
                    System.out.println("-------------------");
                    clientIsOnline = false;
                    socket.close();
                    server.close();
                    in.close();
                    out.close();
                    t1.interrupt();
                    startServer();
                }

                System.out.println("Клиент: " + str);

            }

        } catch (IOException e) {
            System.out.println("Завершение работы сервера...");
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
                server.close();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public void sendMessage() {
        try {
            String s = sc.nextLine();
            if (!equals("")) {
                try {
                    out.writeUTF(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (BufferOverflowException e){

        }
    }
}