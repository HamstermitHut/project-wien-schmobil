package fhtw.projectwienschmobil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViennaSubwayStations {

    private static final String CSV_URL = "https://www.wienerlinien.at/ogd_realtime/doku/ogd/wienerlinien-ogd-haltestellen.csv";

    public static List<String> getSubwayStations() {
        try {
            // open the CSV file from the URL
            URL url = new URL(CSV_URL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            // read the CSV file line by line
            List<String> lines = reader.lines().collect(Collectors.toList());
            // extract the station names from the CSV file
            List<String> stationNames = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                // split the line into fields
                String[] fields = lines.get(i).split(";");

                // the station name is in the second field
                String name = fields[1].replace("ÃŸ","ß").replace("Ã¶","ö").replace("Ã–","Ö").replace("Ã¼","ü").replace("Ã¤","ä").replace("Ã„","Ä").replace("Ã©","é");
                name += "," + fields[0];
                stationNames.add(name);
            }

            return stationNames;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}