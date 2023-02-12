package com.illia.service;

import com.illia.model.Airplane;
import com.illia.model.WayPoint;
import com.illia.evaluator.FlightEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class FlightDispatcher {

    @Autowired
    FlightEvaluator flightEvaluator;

    public void assignFlight(Airplane airplane, List<WayPoint> route){
        Executors.newSingleThreadExecutor().execute(() -> {
            log.info("Thread " + Thread.currentThread().getName() + " started");
            flightEvaluator.evaluateRoutePoints(airplane, route);
            log.info("Thread " + Thread.currentThread().getName() + " finished");
        });
    }
}
