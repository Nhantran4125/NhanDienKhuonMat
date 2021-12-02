/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package facecompare;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainServer {

    ServerSocket server;
    Socket socket;

    public MainServer(int port) {
        try {
            server = new ServerSocket(port);
            int count = 0;
            ExecutorService executor = Executors.newCachedThreadPool();
            while (true) {
                count++;
                socket = server.accept();
                Runnable thread = new Server(socket, count);
                executor.execute(thread);
            }
        } catch (IOException ex) {
            System.out.println("Lỗi tại MainServer : " + ex);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                    server.close();
                } catch (IOException ex) {
                    System.out.println("Lỗi đống Socket hoặc đóng Server: " + ex);
                }
            }
        }
    }

    public static void main(String[] args) {
        MainServer server = new MainServer(5000);
    }
}
