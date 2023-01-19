package fhtw.projectwienschmobil;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class Server extends Thread{

    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private ObjectOutputStream outputStream;
    protected Socket socket;

    public Server(Socket paramSocket) throws IOException {
        this.socket = paramSocket;
        this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.outputStream=new ObjectOutputStream(this.socket.getOutputStream());
    }
    public void run() {
        try {
            System.out.println(leseNachricht(this.socket));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendStations() throws IOException {
        List<String> stations=ViennaSubwayStations.getSubwayStations();
        outputStream.writeObject(stations);


    }

    public String leseNachricht(Socket paramSocket) throws IOException {
        String str = this.bufferedReader.readLine();
        //Schließen des Clients wenn EXIT eingetippt wurde
        if (str.equals("EXIT")) {
            this.socket.close();
            this.bufferedReader.close();
            this.printWriter.close();
        }
        if(str.equals("stations")){
            sendStations();
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
