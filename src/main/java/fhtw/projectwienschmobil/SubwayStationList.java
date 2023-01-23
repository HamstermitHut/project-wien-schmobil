package fhtw.projectwienschmobil;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

import java.time.Duration;

import java.time.LocalTime;
import java.util.*;

/**
 * The class "SubwayStationList" is defined in our application as a client which is able to set up a connection with a server. The client is able to make requests to get data from the server
 *
 */
public class SubwayStationList extends Application{

     //The ui-window

    private Stage window;

    //A list for the stations

    private ListView<String> listView;
    private Socket socket;
    private PrintWriter printWriter;
    private ObjectInputStream objectInputStream;
    //Anzeige der Abfahrtszeiten
    private TextArea textArea;
    //Auswahl zwischen Uhrzeit und Minuten
    private ComboBox time;
    //Check was ausgewählt wurde
    private Boolean isMinute;

    private ComboBox direction;

    private Button save;

    /**
     * Starts the application
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Building the UI and setting up a TCP-connection to a server, by using streams, sockets, printWriter and bufferReader.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        //Initilisierung der Verbindung zum threadedServer
        String ip = "127.0.0.1";
        int port = 1234;
        this.socket = new Socket(ip, port);
        this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
        this.objectInputStream=new ObjectInputStream(socket.getInputStream());

        //Anfordern der Liste der Stationen
        schreibeNachricht(socket, "stations\n");

        //Werte für die ComboBox
        ObservableList<String>options=FXCollections.observableArrayList("Minuten","Uhrzeit");
        time=new ComboBox(options);
        time.getSelectionModel().selectFirst();

        List<String> stations =(List<String>)objectInputStream.readObject();
        this.isMinute=true;
        window = primaryStage;
        window.setTitle("WienSchmobil");
        ObservableList<String>directionOptions=FXCollections.observableArrayList("Beide Richtungen","Hinfahrt","Rückfahrt");

        direction=new ComboBox(directionOptions);
        direction.getSelectionModel().selectFirst();
        direction.setOnAction(event -> showDepartureTimes());


        save=new Button("Speichern");
        save.setOnAction(event -> {
            try {
                saveDepartures();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // create the ListView for displaying the subway stations
        listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.getSelectionModel().selectFirst();

        // load the subway station names from the API
        ObservableList<String> items = FXCollections.observableArrayList(stations);
        listView.setItems(items);
        listView.setOnMouseClicked(event -> showDepartureTimes());


        Button close = new Button("Schließen");
        close.setOnAction(event -> {

            printWriter.write("EXIT");
            printWriter.flush();

            try {
                this.objectInputStream.close();
                this.printWriter.close();
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        });

        //choose between minutes and timestamps
        time.setOnAction(event -> {
            String s = (String) this.time.getValue();
            if(s.equals("Minuten")){
                this.isMinute=true;
            }else{
                this.isMinute=false;
            }
            showDepartureTimes();
        });

        // create the layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));

        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setPrefWidth(300);
        textArea.setPrefHeight(250);

        Label stationList = new Label();
        stationList.setText("Bitte wählen Sie eine Station aus");
        stationList.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

        Label abfahrtZeiten = new Label();
        abfahrtZeiten.setText("Abfahrtzeiten");
        abfahrtZeiten.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

        Label auswahlZeit = new Label();
        auswahlZeit.setText("Abfahrtzeiten \n anzeigen als:");
        auswahlZeit.setFont(Font.font("Verdana", FontWeight.BOLD, 10));



        HBox buttons = new HBox(5);
        buttons.setPadding(new Insets(20, 20, 20, 0));
        buttons.getChildren().addAll(auswahlZeit,time,direction,close,save);

        layout.getChildren().addAll(stationList,listView,abfahrtZeiten,textArea,buttons);



        // show the window
        Scene scene = new Scene(layout, 600, 700);
        window.setScene(scene);
        window.show();
    }
    /**
     * Communication with the server to send messages using "sockets" and "printWriter".
     * @param socket
     * @param nachricht
     * @throws IOException
     */
    void schreibeNachricht(Socket socket, String nachricht) throws IOException {
        this.printWriter.println(nachricht);
        this.printWriter.flush();
    }


    /**
     * Makes a request to the server with the selected station as a parameter. The response is processed and displayed in a TextArea.
     * This function handles the logic of whether the times should be displayed in minutes or as a time and the display of a selected direction of travel.
     */
    private void showDepartureTimes() {
        String selectedStation = listView.getSelectionModel().getSelectedItem();
        selectedStation = selectedStation.substring(selectedStation.indexOf(",") + 1);
        Map<String, List<String>> departureTimes = new HashMap<>();
        this.printWriter.println("send times;"+selectedStation);
        this.printWriter.flush();

        try {
            departureTimes =(Map<String, List<String>>) this.objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        if (departureTimes == null) {
            textArea.setText("");
            return;
        }
        for (Map.Entry<String, List<String>> entry : departureTimes.entrySet()) {
            String line = entry.getKey();
            List<String> times = entry.getValue();
            if(direction.getValue().equals("Beide Richtungen")){
                sb.append(line.replace("Richtung: H","").replace("Richtung: R","")+ ": ");
                sb.append("\n");

                appendTime(sb,times);

                /**
                for (String time : times) {

                    if(this.isMinute){

                        LocalTime start = LocalTime.now();
                        LocalTime end =LocalTime.of(Integer.parseInt(time.substring(time.indexOf(':')-2, time.indexOf(':'))),Integer.parseInt(time.substring(time.indexOf(':')+1, time.lastIndexOf(':'))),Integer.parseInt(time.substring(time.lastIndexOf(':')+1, time.lastIndexOf(':')+3)));
                        Duration duration=Duration.between(start,end);

                        sb.append(duration.getSeconds()/60+", ");
                    }else{
                        sb.append(time.substring(time.indexOf(':')-2, time.lastIndexOf('+')-4)+", ");
                    }
                }
                sb.append("\n");
                 */
            }else if(line.contains("Richtung: H")&&String.valueOf(direction.getValue()).equals("Hinfahrt")){
                sb.append(line.replace("Richtung: H","")+ ": ");
                sb.append("\n");

                appendTime(sb,times);
            }else if(line.contains("Richtung: R")&&String.valueOf(direction.getValue()).equals("Rückfahrt")){
                sb.append(line.replace("Richtung: R","")+ ": ");
                sb.append("\n");

                appendTime(sb,times);
            }

        }
        textArea.setText("");
        textArea.setText(sb.toString());
    }

    /**
     * This method is used to save the current departure times in a file. This method is triggered by clicking on "Speichern".
     * The file will be saved in the root directory of this project as a ".txt"
     * @throws IOException
     */

    public void saveDepartures() throws IOException {
        File path = new File("departures.txt");
        path.createNewFile();
        RandomAccessFile randomAccessFile= new RandomAccessFile(path.getAbsolutePath(),"rw");
        randomAccessFile.seek(0L);
        randomAccessFile.writeBytes(this.textArea.getText());
        randomAccessFile.close();
    }

    /**
     * Uses the respones of the server to calculate the time until arrival or the time in HH:MM:SS to display it in the app
     * @param sb
     * @param times
     */
    public void appendTime(StringBuilder sb,List<String> times){

        for (String time : times) {

            if(this.isMinute){
                LocalTime start = LocalTime.now();
                LocalTime end =LocalTime.of(Integer.parseInt(time.substring(time.indexOf(':')-2, time.indexOf(':'))),Integer.parseInt(time.substring(time.indexOf(':')+1, time.lastIndexOf(':'))),Integer.parseInt(time.substring(time.lastIndexOf(':')+1, time.lastIndexOf(':')+3)));
                Duration duration=Duration.between(start,end);

                sb.append(duration.getSeconds()/60+", ");
            }else{
                sb.append(time.substring(time.indexOf(':')-2, time.lastIndexOf('+')-4)+", ");
            }
        }
        sb.append("\n");

    }

}
