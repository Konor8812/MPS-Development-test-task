package com.illia.demo;

import com.illia.model.Airplane;
import com.illia.model.AirplaneCharacteristics;
import com.illia.model.TemporaryPoint;
import com.illia.model.WayPoint;
import com.illia.service.FlightDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//@Configuration
public class DemonstrationExample {

    @Autowired
    FlightDispatcher dispatcher;

    @Bean
    CommandLineRunner runner() {
        return (args) -> {

            var maxSpeed = 2000;
            var maxAcceleration = 20;
            var heightCorrectionSpeed = 15;
            var courseCorrectionSpeed = 0.72;
            var characteristics = AirplaneCharacteristics.builder()
                    .Amax(maxAcceleration)
                    .Vdir(courseCorrectionSpeed)
                    .Vh(heightCorrectionSpeed)
                    .Vmax(maxSpeed)
                    .build();

            for (int i = 0; i < 10; i++) {
                var wayPoints = getWayPoints();
                var startPoint = wayPoints.get(0);

                var airplane = new Airplane(characteristics, TemporaryPoint.builder()
                        .course(0)
                        .height(startPoint.getHeight())
                        .speed(startPoint.getPassSpeed())
                        .longitude(startPoint.getLongitude())
                        .latitude(startPoint.getLatitude())
                        .build());

                dispatcher.assignFlight(airplane, wayPoints);
            }
        };
    }

    private List<WayPoint> getWayPoints() {
        var randomCoordinatesGenerator = new Random();
        var list = new ArrayList<WayPoint>();
        list.add(WayPoint.builder()
                .height(1200)
                .latitude(randomCoordinatesGenerator.nextDouble(5))
                .longitude(randomCoordinatesGenerator.nextDouble(5))
                .passSpeed(1450)
                .build());
        list.add(WayPoint.builder()
                .height(1250)
                .latitude(randomCoordinatesGenerator.nextDouble(5))
                .longitude(randomCoordinatesGenerator.nextDouble(5) * -1)
                .passSpeed(1510)
                .build());
        list.add(WayPoint.builder()
                .height(1100)
                .latitude(randomCoordinatesGenerator.nextDouble(5) * -1)
                .longitude(randomCoordinatesGenerator.nextDouble(5) * -1)
                .passSpeed(1210)
                .build());
        list.add(WayPoint.builder()
                .height(600)
                .latitude(randomCoordinatesGenerator.nextDouble(5) * -1)
                .longitude(randomCoordinatesGenerator.nextDouble(5))
                .passSpeed(5000)
                .build());
        return list;

    }
}
