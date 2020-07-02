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
    Thread t1;
    Thread t2;

    final int PORT = 8189;
    public static void main(String[] args) throws IOException, InterruptedException {
        ChatServer chatServer = new ChatServer();
        chatServer.startServer();
    }
    public void startServer() throws IOException {
        if (t1 != null) t1.interrupt();
        if (t2 != null) t2.interrupt();
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



        t1 = new Thread(() -> {

            while (!Thread.currentThread().isInterrupted()) {
                sendMessage();
            }

        });
        t1.start();
        t2 = new Thread(() -> {
            try {
                readMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t2.start();
        } catch (IOException e) {
            System.out.println("Завершение работы сервера...");
            socket.close();
            server.close();
            in.close();
            out.close();
            startServer();
        }
        }

    public void sendMessage() {
        try {
            String s = sc.nextLine();
            if (!Thread.currentThread().isInterrupted()) {
            if (s.equals("/end")) {
                socket.close();
                server.close();
                in.close();
                out.close();
                sc.close();
                System.exit(0);
            }
            if (!s.equals("")) {
                try {
                    out.writeUTF(s);
                } catch (IOException e) {
                    System.out.println("Клиент еще не подключился, повторите еще раз сообщение!");
                }
            }
            }
        } catch (BufferOverflowException | IndexOutOfBoundsException | IOException e){
            return;
        }
    }

    public void readMessage() throws IOException {
            try {
                while (true) {


                    String str = in.readUTF();


                    if (str.equalsIgnoreCase("/end")) {
                        System.out.println("Клиент отключился");
                        System.out.println("-------------------");
                        clientIsOnline = false;
                        socket.close();
                        server.close();
                        in.close();
                        out.close();
                    }
                    else System.out.println("Клиент: " + str);

                }

            } catch (IOException e) {
                System.out.println("Socket closed");
            } finally {
                startServer();
                Thread.currentThread().interrupt();
            }
        }
}