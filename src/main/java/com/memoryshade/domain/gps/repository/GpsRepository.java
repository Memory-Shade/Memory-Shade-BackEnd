package com.memoryshade.domain.gps.repository;

import com.memoryshade.domain.gps.exception.GpsErrorCode;
import com.memoryshade.domain.gps.model.Gps;
import com.memoryshade.global.exception.ExceptionList;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface GpsRepository extends Repository<Gps, Long> {

    Gps save(Gps gps);
    void delete(Gps gps);

    Optional<Gps> findByZoneIdAndUser_UserId(Long zoneId, Long userId);

    default Gps getByZoneIdAndUserId(Long zoneId, Long userId) {
        return findByZoneIdAndUser_UserId(zoneId, userId).
                orElseThrow(() -> new ExceptionList(GpsErrorCode.GPS_NOT_FOUND));
    }

    List<Gps> findAllByUser_UserId(Long userId);
}
