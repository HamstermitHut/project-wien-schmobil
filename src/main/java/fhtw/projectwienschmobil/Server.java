package fhtw.projectwienschmobil;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A server which is suposed to make API-calls to the "WienerLinienApi" and send the results to the client.
 */
public class Server extends Thread{

    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private ObjectOutputStream outputStream;
    protected Socket socket;
    boolean exit;

    /**
     * Constructor to initialize the streams and sockets
     * @param paramSocket
     * @throws IOException
     */
    public Server(Socket paramSocket) throws IOException {
        this.socket = paramSocket;
        this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.outputStream=new ObjectOutputStream(this.socket.getOutputStream());
        this.exit=true;
    }

    /**
     run(), is executed when the thread is started until the "while" is no longer "true" or the server thread is closed
     */
    @Override
    public void run() {
        try {
            while(exit){
                leseNachricht();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     sendStations() gets from method getSubwayStations(); from the ViennaSubwayStations class, all stations and sends it over a tcp connection stream to the client
     * @throws IOException
     */
    public void sendStations() throws IOException {
        List<String> stations=ViennaSubwayStations.getSubwayStations();
        outputStream.writeObject(stations);
        this.outputStream.flush();
        this.socket.getOutputStream().flush();
    }

    /**
     * Sends the current departuretimes of a selected station to the client over a tcp-connection
     * @param station: a selected station from the client
     * @throws IOException
     */
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

    /**
     * Reads the current message from a client over a tcp-connection using bufferedReader.
     * @throws IOException
     */
    public void leseNachricht() throws IOException {
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
}
