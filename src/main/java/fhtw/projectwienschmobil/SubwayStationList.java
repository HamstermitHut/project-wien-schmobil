package fhtw.projectwienschmobil;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;


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
        // get the selected subway station from the list view
        String selectedStation = listView.getSelectionModel().getSelectedItem();
        selectedStation = selectedStation.substring(selectedStation.indexOf(",") + 1);
        if (selectedStation == null) {
            // display an error message if no station is selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No station selected");
            alert.setContentText("Please select a station from the list.");
            alert.showAndWait();
            return;
        }

        // get the departure times for the selected station
        List<String> departureTimes;
        try {
            departureTimes = ViennaSubwayDepartures.getDepartureTimes(selectedStation);
        } catch (IOException e) {
            // display an error message if there was a problem connecting to the API
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("API connection failed");
            alert.setContentText("Could not connect to the Vienna Subway API. Please check your internet connection and try again.");
            alert.showAndWait();
            return;
        }
        // create a new window to display the departure times
        Stage newWindow = new Stage();
        newWindow.setTitle("Departure Times for " + selectedStation);

        // create a list view to display the departure times
        ListView<String> departureTimesListView = new ListView<>();
        departureTimesListView.setItems(FXCollections.observableArrayList(departureTimes));

        // create the layout
        VBox layout = new VBox();
        layout.getChildren().add(departureTimesListView);

        // show the window
        Scene scene = new Scene(layout, 300, 250);
        newWindow.setScene(scene);
        newWindow.show();
    }

}
