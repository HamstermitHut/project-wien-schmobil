package fhtw.projectwienschmobil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.test(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void test(String args) throws IOException {
        String zuSendendeNachricht, ip = "127.0.0.1";
        int port = Integer.parseInt(args);
        this.socket = new Socket(ip, port);
        this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));



        //Sauberes schließen der Streams und des Sockets
        this.printWriter.close();
        this.bufferedReader.close();
        this.socket.close();
    }
    /**
     * Methode zum kommunizieren (schreiben) mit dem Server über Streams und Sockets
     * @param socket
     * @param nachricht
     * @throws IOException
     */
    void schreibeNachricht(Socket socket, String nachricht) throws IOException {
        this.printWriter.println(nachricht);
        this.printWriter.flush();
    }
    /**
     * Methode zum kommunizieren (lesen) mit dem Server über Streams und Sockets
     * @param socket
     * @throws IOException
     */
    String leseNachricht(Socket socket) throws IOException {
        String nachricht = this.bufferedReader.readLine();
        return nachricht;
    }
}
