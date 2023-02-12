package com.illia.model;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Builder
@Data
@Document
public class Flight {
    @Id
    private String id;

    private long number;
    private List<WayPoint> wayPoints;
    private List<TemporaryPoint> passedPoints;
}
