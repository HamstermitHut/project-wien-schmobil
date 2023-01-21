package fhtw.projectwienschmobil;
import java.io.File;
import java.io.RandomAccessFile;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Diese Klasse dient dazu mehrere eingehende Verbindungen von Clients anzunehmen und einen Server Thread mit einer Server-Client-Verbindung zu starten
 *
 */
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
                makeFileLog(socket);
                System.out.println(""+socket);
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // neuer thread für einen client
            new Server(socket).start();

        }

    }

    /**
     * Diese Funktion dient zum Messen, wie viele NutzerInnen sich auf diese App anmelden. Hierbei werden
     * die jeweiligen Sockets mit Portnummer und Ip-Adresse in ein Log-File geschrieben
     * @param socket --> aktuelle Socketverbindung
     * @throws IOException --> Fehler beim Dateiauslesen
     */
    private static void makeFileLog(Socket socket) throws IOException {
        File path = new File("log.txt");
        System.out.println(path.getAbsolutePath());
        path.createNewFile();
        RandomAccessFile randomAccessFile= new RandomAccessFile(path.getAbsolutePath(),"rw");
        randomAccessFile.seek(0L);
        do {
            randomAccessFile.readLine();
        } while (randomAccessFile.readLine() != null);
        randomAccessFile.writeBytes(""+socket+"\n");
        randomAccessFile.close();
    }
}
