package com.memoryshade.domain.gps.repository;

import com.memoryshade.domain.gps.model.Gps;
import org.springframework.data.repository.Repository;

public interface GpsRepository extends Repository<Gps, Long> {

    Gps save(Gps gps);
}
