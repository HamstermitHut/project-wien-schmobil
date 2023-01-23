package fhtw.projectwienschmobil;
import java.io.File;
import java.io.RandomAccessFile;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class is used to accept multiple incoming connections from clients and start a serverthread to setup a server-client connection
 *
 */
public class threadedServer {
    /**
     * Waits for incoming connections and starts a new server thread.
     * The client socket is passed as a parameter for the new server.
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
     * This function is used to measure how many users log on to this app. The respective sockets with port number and Ip address are written to a log file.
     * @param socket --> current Socketconnection
     * @throws IOException
     */
    public static void makeFileLog(Socket socket) throws IOException {
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
