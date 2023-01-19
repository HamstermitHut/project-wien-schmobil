package fhtw.projectwienschmobil;


import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<String> stations = ViennaSubwayStations.getSubwayStations();
        for (String station : stations) {
            System.out.println(station);
        }
    }
}
