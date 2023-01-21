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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;


public class SubwayStationList extends Application{
    private Stage window;
    private ListView<String> listView;
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private ObjectInputStream objectInputStream;
    //Anzeige der Abfahrtszeiten
    private TextArea textArea;
    //Auswahl zwischen Uhrzeit und Minuten
    private ComboBox time;
    //Check was ausgewählt wurde
    private Boolean isMinute;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        String zuSendendeNachricht, ip = "127.0.0.1";
        int port = 1234;
        this.socket = new Socket(ip, port);
        this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.objectInputStream=new ObjectInputStream(socket.getInputStream());
        schreibeNachricht(socket,"stations\n");
        ObservableList<String>options=FXCollections.observableArrayList("Minuten","Uhrzeit");
        time=new ComboBox(options);
        List<String> stations =(List<String>)objectInputStream.readObject();
        time.getSelectionModel().selectFirst();
        this.isMinute=true;
        window = primaryStage;
        window.setTitle("WienSchmobil");

        // create the ListView for displaying the subway stations
        listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.getSelectionModel().selectFirst();

        // load the subway station names from the API
        ObservableList<String> items = FXCollections.observableArrayList(stations);
        listView.setItems(items);

        // create the "Show Departure Times" button
        Button showDepartureTimesButton = new Button("Abfahrtszeiten anzeigen");
        showDepartureTimesButton.setOnAction(event -> {showDepartureTimes();});

        Button close = new Button("Schließen");
        close.setOnAction(event -> {

            printWriter.write("EXIT");
            printWriter.flush();

            try {
                this.objectInputStream.close();
                this.printWriter.close();
                this.socket.close();
                this.bufferedReader.close();
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
        //layout.getChildren().addAll(listView, showDepartureTimesButton);

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
        auswahlZeit.setFont(Font.font("Verdana", FontWeight.BOLD, 11));



        HBox buttons = new HBox(5);
        buttons.setPadding(new Insets(20, 20, 20, 0));
        buttons.getChildren().addAll(auswahlZeit,time,showDepartureTimesButton,close);

        layout.getChildren().addAll(stationList,listView,abfahrtZeiten,textArea,buttons);



        // show the window
        Scene scene = new Scene(layout, 600, 700);
        window.setScene(scene);
        window.show();
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
        for (Map.Entry<String, List<String>> entry : departureTimes.entrySet()) {
            String line = entry.getKey();
            List<String> times = entry.getValue();
            sb.append(line+ ": ");
            sb.append("\n");

            for (String time : times) {

                if(this.isMinute){
                    sb.append(Math.abs(Integer.parseInt(time.substring(time.indexOf(':')+1, time.lastIndexOf(':'))))+", ");
                }else{
                    sb.append(time.substring(time.indexOf(':')-2, time.lastIndexOf('+')-4)+", ");
                    System.out.println(Math.abs(LocalDateTime.now().getHour()-Integer.parseInt(time.substring(time.indexOf(':')-2, time.indexOf(':')))));
                }
            }
            sb.append("\n");
        }
        textArea.setText(sb.toString());
    }

    public void saveDepartures(){

    }
}
