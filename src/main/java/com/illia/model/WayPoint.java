package com.illia.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WayPoint {
    private double latitude;
    private double longitude;
    private double height;
    private double passSpeed;
}
