package fhtw.projectwienschmobil;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server extends Thread{

    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private ObjectOutputStream outputStream;
    protected Socket socket;
    boolean exit;

    public Server(Socket paramSocket) throws IOException {
        this.socket = paramSocket;
        this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.outputStream=new ObjectOutputStream(this.socket.getOutputStream());
        this.exit=true;
    }
    @Override
    public void run() {
        try {
            while(exit){
                leseNachricht(this.socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendStations() throws IOException {
        List<String> stations=ViennaSubwayStations.getSubwayStations();
        outputStream.writeObject(stations);
        this.outputStream.flush();
        this.socket.getOutputStream().flush();
    }


    public void sendDepartureTimestoClient(String station) throws IOException {
        Map<String, List<String>> departureTimes = new HashMap<>();
        try{
            departureTimes=ViennaSubwayDepartures.getDepartureTimes(station);
        }catch (Exception e){
            departureTimes=null;
            System.out.println("Keine Zeiten");
        }
        outputStream.writeObject(departureTimes);
        outputStream.flush();
        this.socket.getOutputStream().flush();
    }

    public void leseNachricht(Socket paramSocket) throws IOException {
        String str = this.bufferedReader.readLine();

        //Schließen des Clients wenn EXIT eingetippt wurde
        if (str.equals("EXIT")) {
            this.socket.close();
            this.bufferedReader.close();
            this.printWriter.close();
            this.outputStream.close();
            this.exit=false;
        }
        if(str.equals("stations")){
            sendStations();
        }else if (str.contains("send times;")){
            sendDepartureTimestoClient(str.substring(str.indexOf(";")+1));
        }
    }


    public void schreibeNachricht(Socket paramSocket, String paramString) throws IOException {
        this.printWriter.println(paramString);
        this.printWriter.flush();
    }





}
