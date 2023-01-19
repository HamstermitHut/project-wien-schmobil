package fhtw.projectwienschmobil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
public class Server extends Thread{

    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    protected Socket socket;

    public Server(Socket paramSocket) throws IOException {
        this.socket = paramSocket;
        this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }
    public void run() {

    }

    public String leseNachricht(Socket paramSocket) throws IOException {
        String str = this.bufferedReader.readLine();
        //Schließen des Clients wenn EXIT eingetippt wurde
        if (str.equals("EXIT")) {
            this.socket.close();
            this.bufferedReader.close();
            this.printWriter.close();
        }
        //str = str.toLowerCase();
        System.out.println(str);
        return str;
    }


    public void schreibeNachricht(Socket paramSocket, String paramString) throws IOException {
        this.printWriter.println(paramString);
        this.printWriter.flush();
    }





}
