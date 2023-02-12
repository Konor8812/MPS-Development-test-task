package com.illia.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class Airplane {
    private long Id;
    private AirplaneCharacteristics characteristics;
    private TemporaryPoint currentPosition;
    private List<Flight> flights;

    public Airplane(AirplaneCharacteristics characteristics, TemporaryPoint currentPosition) {
        this.characteristics = characteristics;
        this.currentPosition = currentPosition;
    }

}
