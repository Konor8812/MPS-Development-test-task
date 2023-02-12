package com.illia.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AirplaneCharacteristics{
    private double Vmax; //  Максимальная скорость м/c
    private double Amax; // Скорость изменения скорости(Максимальное ускорение) м/c^2
    private double Vh; // Скорость изменения высоты м/c
    private double Vdir; // Скорость изменения курса град./с
}