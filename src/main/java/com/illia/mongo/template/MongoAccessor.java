package com.illia.mongo.template;

import com.illia.model.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class MongoAccessor {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final Object monitor = new Object();

    public Flight saveNew(Flight flight, String collection){
        synchronized (monitor){
            var greatestFlightNumber = mongoTemplate.find(new Query().with(Sort.by(Sort.Direction.DESC, "number"))
                    .limit(1), Flight.class);
            flight.setNumber(greatestFlightNumber.isEmpty() ? 0 : greatestFlightNumber.get(0).getNumber() + 1);
            return mongoTemplate.save(flight, collection);
        }
    }

    public Flight updateExisting(Flight flight, String collection){
        return mongoTemplate.save(flight, collection);
    }

}
