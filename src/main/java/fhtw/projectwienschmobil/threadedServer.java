package fhtw.projectwienschmobil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class threadedServer {
    /**
     * Klasse threadedServer: Wartet auf eingehende Verbindungen und startet dabei einen neuen Server-Thread.
     * Dabei wird der Client-Socket als Parameter für den neuen Server übergeben.
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        ServerSocket serverSocket = null;
        Socket socket = null;

        int port=1234;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                socket = serverSocket.accept(); //Blockiert bis Client eine Verbindung herstellt.
                System.out.println(""+socket);
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // neuer thread für einen client
            new Server(socket).start();
        }
    }
}
