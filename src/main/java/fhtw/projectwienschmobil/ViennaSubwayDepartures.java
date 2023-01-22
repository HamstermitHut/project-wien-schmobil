package fhtw.projectwienschmobil;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Class handles the api connection with wiener linien api
 */
public class ViennaSubwayDepartures {
    private static final String API_URL = "https://www.wienerlinien.at/ogd_realtime/monitor?diva=";

    /**
     * get json from getJsonFromUrl(url) and parse json with the gson library
     * so that the departureTimes can be return as "Map"
     * @param selectedStation
     * @return departureTimes
     * @throws IOException
     */
    public static Map<String, List<String>> getDepartureTimes(String selectedStation) throws IOException {
        String url = API_URL + selectedStation;
        String json = getJsonFromUrl(url);
        ViennaSubwayApiResponse response = new Gson().fromJson(json, ViennaSubwayApiResponse.class);
        ViennaSubwayApiData data = response.getData();
        List<ViennaSubwayApiMonitor> monitors = data.getMonitors();

        if (monitors.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, List<String>> departureTimes = new HashMap<>();
        for (ViennaSubwayApiMonitor monitor : monitors) {
            List<ViennaSubwayApiLine> lines = monitor.getLines();
            for (ViennaSubwayApiLine line : lines) {
                String lineNumber = line.getName()+"-"+line.getTowards()+" Plattform "+line.getPlatform()+ " Richtung: "+line.getDirection();
                lineNumber=lineNumber.replace("ÃŸ","ß").replace("Ã¶","ö").replace("Ã–","Ö").replace("Ã¼","ü").replace("Ã¤","ä").replace("Ã„","Ä").replace("Ã©","é");
                List<String> lineDepartureTimes = new ArrayList<>();
                for (ViennaSubwayApiDeparture departure : line.departures.departure) {
                    lineDepartureTimes.add(departure.getDepartureTime().getTimePlanned());
                }
                departureTimes.put(lineNumber, lineDepartureTimes);
            }
        }

        return departureTimes;
    }

    /**
     * get the json from the api
     * @param url
     * @return json
     * @throws IOException
     */
    private static String getJsonFromUrl(String url) throws IOException {

        URL website = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) website.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        try (InputStreamReader in = new InputStreamReader(connection.getInputStream());
             BufferedReader reader = new BufferedReader(in)) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
    }

    /**
     * ViennaSubwayApiLine definiert die Variablen für den Zugriff auf die API der WienerLinien. Jeder @SerializedName steht für eine Variable aus der API
     */
    class ViennaSubwayApiLine {
        @SerializedName("name")
        private String name;
        @SerializedName("towards")
        private String towards;
        @SerializedName("direction")
        private String direction;
        @SerializedName("platform")
        private String platform;
        @SerializedName("richtungsId")
        private String richtungsId;
        @SerializedName("barrierFree")
        private boolean barrierFree;
        @SerializedName("realtimeSupported")
        private boolean realtimeSupported;
        @SerializedName("trafficjam")
        private boolean trafficjam;
        @SerializedName("departures")
        private ViennaSubwayApiDepartures departures;
        @SerializedName("type")
        private String type;
        @SerializedName("lineId")
        private int lineId;
        public String getName() {
            return name;
        }
        public String getTowards() {
            return towards;
        }
        public String getDirection() {
            return direction;
        }
        public String getPlatform() {
            return platform;
        }
        public String getRichtungsId() {
            return richtungsId;
        }
        public boolean isBarrierFree() {
            return barrierFree;
        }
        public boolean isRealtimeSupported() {
            return realtimeSupported;
        }
        public boolean isTrafficjam() {
            return trafficjam;
        }
        public ViennaSubwayApiDepartures getDepartures() {
            return departures;
        }
        public String getType() {
            return type;
        }
        public int getLineId() {
            return lineId;
        }
    }
    class ViennaSubwayApiResponse {

        @SerializedName("data")
        ViennaSubwayApiData data;

        public ViennaSubwayApiData getData() {
            return data;
        }
    }

    class ViennaSubwayApiData {
        @SerializedName("monitors")
        List<ViennaSubwayApiMonitor> monitors;

        public List<ViennaSubwayApiMonitor> getMonitors() {
            return monitors;
        }
    }

    class ViennaSubwayApiMonitor {
        @SerializedName("lines")
        List<ViennaSubwayApiLine> lines;

        public List<ViennaSubwayApiLine> getLines() {
            return lines;
        }
    }

    class ViennaSubwayApiDepartures {
        @SerializedName("departure")
        List<ViennaSubwayApiDeparture> departure;

        public List<ViennaSubwayApiDeparture> getDeparture() {
            return departure;
        }

        public List<String> getDepartureTimes() {
            List<String> departureTimes = new ArrayList<>();
            for (ViennaSubwayApiDeparture dep : departure) {
                ViennaSubwayApiDepartureTime departureTime = dep.getDepartureTime();
                String time = departureTime.getTimePlanned();
                departureTimes.add(time);
            }
            return departureTimes;
        }
    }

    class ViennaSubwayApiDeparture {
        @SerializedName("departureTime")
        ViennaSubwayApiDepartureTime departureTime;

        public ViennaSubwayApiDepartureTime getDepartureTime() {
            return departureTime;
        }
    }

    class ViennaSubwayApiDepartureTime {
        @SerializedName("timePlanned")
        String timePlanned;
        String timeReal;

        public String getTimePlanned() {
            return timePlanned;
        }

        @SerializedName("timeReal")
        public String getTimeReal(){return timeReal;}
    }
}
