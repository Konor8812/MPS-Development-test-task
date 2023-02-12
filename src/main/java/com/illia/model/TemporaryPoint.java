package com.illia.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TemporaryPoint {
    private double latitude;
    private double longitude;
    private double height;
    private double speed;
    private double course;
}
