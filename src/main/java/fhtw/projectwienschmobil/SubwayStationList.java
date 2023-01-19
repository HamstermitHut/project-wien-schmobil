package fhtw.projectwienschmobil;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SubwayStationList extends Application{
    private Stage window;
    private ListView<String> listView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Vienna Subway Stations");

        // create the ListView for displaying the subway stations
        listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // load the subway station names from the API
        ObservableList<String> items = FXCollections.observableArrayList(ViennaSubwayStations.getSubwayStations());
        listView.setItems(items);

        // create the "Show Departure Times" button
        Button showDepartureTimesButton = new Button("Show Departure Times");
        showDepartureTimesButton.setOnAction(event -> showDepartureTimes());

        // create the layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(listView, showDepartureTimesButton);

        // show the window
        Scene scene = new Scene(layout, 300, 250);
        window.setScene(scene);
        window.show();
    }
    private void showDepartureTimes() {
        String selectedStation = listView.getSelectionModel().getSelectedItem();
        selectedStation = selectedStation.substring(selectedStation.indexOf(",") + 1);
        Map<String, List<String>> departureTimes = new HashMap<>();
        try {
            departureTimes = ViennaSubwayDepartures.getDepartureTimes(selectedStation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : departureTimes.entrySet()) {
            String line = entry.getKey();
            List<String> times = entry.getValue();
            sb.append(line + ": ");
            for (String time : times) {
                sb.append(time + ", ");
            }
            sb.append("\n");
        }
        TextArea textArea = new TextArea();
        textArea.setText(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setPrefWidth(300);
        textArea.setPrefHeight(250);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().add(textArea);

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Departure Times");
        window.setScene(new Scene(layout));
        window.show();
    }
}
