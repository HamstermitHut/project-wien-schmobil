package fhtw.projectwienschmobil;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ViennaSubwayDepartures {
    private static final String API_URL = "https://www.wienerlinien.at/ogd_realtime/monitor?diva=";

    public static List<String> getDepartureTimes(String selectedStation) throws IOException {
        String url = API_URL + selectedStation;
        System.out.println(url);
        String json = getJsonFromUrl(url);
        ViennaSubwayApiResponse response = new Gson().fromJson(json, ViennaSubwayApiResponse.class);
        ViennaSubwayApiData data = response.getData();
        List<ViennaSubwayApiMonitor> monitors = data.getMonitors();

        if (monitors.isEmpty()) {
            return new ArrayList<>();
        }
        ViennaSubwayApiMonitor monitor = monitors.get(0);

        return monitor.getLines().get(0).departures.getDepartureTimes();
    }

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
        List<ViennaSubwayApiLines> lines;

        public List<ViennaSubwayApiLines> getLines() {
            return lines;
        }
    }

    class ViennaSubwayApiLines {
        @SerializedName("departures")
        ViennaSubwayApiDepartures departures;

        public ViennaSubwayApiDepartures getDepartures() {
            return departures;
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

        public String getTimePlanned() {
            return timePlanned;
        }
    }
}
