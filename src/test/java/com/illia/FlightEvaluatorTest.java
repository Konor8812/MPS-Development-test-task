package com.illia;

import com.illia.model.Airplane;
import com.illia.model.AirplaneCharacteristics;
import com.illia.model.TemporaryPoint;
import com.illia.model.WayPoint;
import com.illia.evaluator.FlightEvaluator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



@SpringBootTest(classes = {FlightEvaluator.class})
public class FlightEvaluatorTest {

    @Autowired
    FlightEvaluator flightEvaluator;

    @Test
    public void processorTestShouldPassAllPoints() {


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

        for (int i = 0; i < 3; i++) {
            var wayPoints = getWayPoints();
            var startPoint = wayPoints.get(0);

            var airplane = new Airplane(characteristics, TemporaryPoint.builder()
                    .course(0)
                    .height(startPoint.getHeight())
                    .speed(startPoint.getPassSpeed())
                    .longitude(startPoint.getLongitude())
                    .latitude(startPoint.getLatitude())
                    .build());

            flightEvaluator.evaluateRoutePoints(airplane, wayPoints);
        }
    }

    private List<WayPoint> getWayPoints() {
        var randomCoordinatesGenerator = new Random();
        var list = new ArrayList<WayPoint>();
        list.add(WayPoint.builder()
                .height(1200)
                .latitude(randomCoordinatesGenerator.nextDouble(10))
                .longitude(randomCoordinatesGenerator.nextDouble(10))
                .passSpeed(1450)
                .build());
        list.add(WayPoint.builder()
                .height(1250)
                .latitude(randomCoordinatesGenerator.nextDouble(10))
                .longitude(randomCoordinatesGenerator.nextDouble(10) * -1)
                .passSpeed(1510)
                .build());
        list.add(WayPoint.builder()
                .height(1100)
                .latitude(randomCoordinatesGenerator.nextDouble(10) * -1)
                .longitude(randomCoordinatesGenerator.nextDouble(10) * -1)
                .passSpeed(1210)
                .build());
        list.add(WayPoint.builder()
                .height(600)
                .latitude(randomCoordinatesGenerator.nextDouble(10) * -1)
                .longitude(randomCoordinatesGenerator.nextDouble(10))
                .passSpeed(5000)
                .build());
        return list;

    }

}
