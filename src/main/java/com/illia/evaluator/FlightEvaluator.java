package com.illia.evaluator;


import com.illia.model.*;
import com.illia.mongo.template.MongoAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FlightEvaluator {
    private static final double METRES_IN_ONE_DEGREE = 100_000;

   @Autowired
    MongoAccessor mongoAccessor;

    public List<TemporaryPoint> evaluateRoutePoints(Airplane airplane, List<WayPoint> wayPoints) {
        var passedWayPoints = new ArrayList<WayPoint>();
        passedWayPoints.add(wayPoints.get(0));

        var passedTemporaryPoints = new ArrayList<TemporaryPoint>();
        passedTemporaryPoints.add(airplane.getCurrentPosition());

        var flight = mongoAccessor.saveNew(Flight.builder()
                .wayPoints(passedWayPoints)
                .passedPoints(passedTemporaryPoints)
                .build(), "flight");

        var maxSpeed = airplane.getCharacteristics().getVmax();
        var maxAcceleration = airplane.getCharacteristics().getAmax();
        var heightCorrectionRate = airplane.getCharacteristics().getVh();
        var courseCorrectionRate = airplane.getCharacteristics().getVdir();

        for (int k = 1; k < wayPoints.size(); k++ ) {
            var nextWp = wayPoints.get(k);

            var neededLatitude = nextWp.getLatitude();
            var neededLongitude = nextWp.getLongitude();
            var neededCourse = .0;
            var neededHeight = nextWp.getHeight();
            var neededSpeed = nextWp.getPassSpeed();

            for(int i = 0; ; i++){
                var currentPosition = airplane.getCurrentPosition();

                var currentLatitude = currentPosition.getLatitude();
                var currentLongitude = currentPosition.getLongitude();
                var currentHeight = currentPosition.getHeight();
                var currentSpeed = currentPosition.getSpeed();
                var currentCourse = currentPosition.getCourse();

                // wait
                var passedLongitude = calculatePassedLongitudeValue(currentCourse, currentSpeed);
                var passedLatitude = calculatePassedLatitudeValue(currentCourse, currentSpeed);

                if (isWayPointPassed(neededLatitude, neededLongitude, currentLatitude, currentLongitude, passedLatitude, passedLongitude)) {
                    passedWayPoints.add(nextWp);
                    flight.setWayPoints(passedWayPoints);
                    flight = mongoAccessor.updateExisting(flight, "flight");
                    break;
                }

                currentLatitude += passedLatitude;
                currentLongitude += passedLongitude;

                neededCourse = evaluateNeededCourse(neededLatitude, neededLongitude, currentLatitude, currentLongitude);
                if (currentCourse > neededCourse + courseCorrectionRate + 0.00001) {
                    currentCourse -= courseCorrectionRate;
                } else if (currentCourse < neededCourse - courseCorrectionRate - 0.00001) {
                    currentCourse += courseCorrectionRate;
                } else {
                    currentCourse = neededCourse;
                }

                currentSpeed = solveSpeed(neededSpeed, currentSpeed, maxSpeed, maxAcceleration);
                currentHeight = solveHeight(neededHeight, currentHeight, heightCorrectionRate);

                var nextPosition = TemporaryPoint.builder()
                        .latitude(currentLatitude)
                        .longitude(currentLongitude)
                        .height(currentHeight)
                        .speed(currentSpeed)
                        .course(currentCourse)
                        .build();

                airplane.setCurrentPosition(nextPosition);
                passedTemporaryPoints.add(nextPosition);

                flight.setPassedPoints(passedTemporaryPoints);
                flight = mongoAccessor.updateExisting(flight, "flight");
            }
        }

        return passedTemporaryPoints;
    }

    private static double solveSpeed(double neededSpeed, double currentSpeed, double maxSpeed, double maxAcceleration) {
        if (currentSpeed + maxAcceleration <= maxSpeed) {
            if (neededSpeed >= currentSpeed + maxAcceleration) {
                return currentSpeed + maxAcceleration;
            } else if (neededSpeed < currentSpeed - maxAcceleration) {
                return currentSpeed - maxAcceleration;
            }
            return neededSpeed;
        } else {
            return maxSpeed;
        }
    }

    private static double solveHeight(double neededHeight, double currentHeight, double maxHeightCorrection) {
        if (neededHeight >= currentHeight + maxHeightCorrection) {
            return currentHeight + maxHeightCorrection;
        } else if (neededHeight < currentHeight - maxHeightCorrection) {
            return currentHeight - maxHeightCorrection;
        } else {
            return neededHeight;
        }
    }

    private static boolean isWayPointPassed(double neededLatitude, double neededLongitude, double currentLatitude, double currentLongitude, double passedLatitude, double passedLongitude) {
        var latDiff = neededLatitude - currentLatitude;
        var lonDiff = neededLongitude - currentLongitude;

        return ((latDiff <= 0 && latDiff > 0.000001 - passedLatitude * -1)
                || (latDiff >= 0 && latDiff < 0.000001 + passedLatitude))
                && ((lonDiff <= 0 && lonDiff > 0.000001 - passedLongitude * -1)
                || (lonDiff >= 0 && lonDiff < 0.000001 + passedLongitude));

    }

    private static double calculatePassedLongitudeValue(double currentCourse, double currentSpeed) {
        var koef = Math.sin(Math.toRadians(currentCourse));
        return currentSpeed * koef / METRES_IN_ONE_DEGREE;
    }


    private static double calculatePassedLatitudeValue(double currentCourse, double currentSpeed) {
        var koef = Math.cos(Math.toRadians(currentCourse));
        return currentSpeed * koef / METRES_IN_ONE_DEGREE;
    }

    private static double evaluateNeededCourse(double neededLatitude, double neededLongitude, double currentLatitude, double currentLongitude) {
        double x = neededLongitude - currentLongitude;
        double y = neededLatitude - currentLatitude;
        var s = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        var courseInDegrees = Math.toDegrees(Math.acos(y / s));

        if (neededLongitude > currentLongitude) {
            return courseInDegrees;
        } else {
            return courseInDegrees * -1;
        }
    }
}
