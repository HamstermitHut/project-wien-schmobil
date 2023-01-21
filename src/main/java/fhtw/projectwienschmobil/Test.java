package fhtw.projectwienschmobil;


import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public class Test {
    public static void main(String[] args) {


        LocalTime start = LocalTime.now();

        LocalTime end =LocalTime.of(21,30,25);

        Duration duration=Duration.between(start,end);
        System.out.println(duration.getSeconds()/60);



        List<String> stations = ViennaSubwayStations.getSubwayStations();
        for (String station : stations) {
            System.out.println(station);
        }
    }
}
