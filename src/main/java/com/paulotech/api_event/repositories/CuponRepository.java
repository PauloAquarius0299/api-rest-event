package com.paulotech.api_event.repositories;

import com.paulotech.api_event.domain.cupon.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface CuponRepository extends JpaRepository<Cupon, UUID> {
    List<Cupon> findByEventIdAndValidAfter(UUID eventId, Date currentDate);
}
