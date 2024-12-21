package com.paulotech.api_event.repositories;

import com.paulotech.api_event.domain.cupon.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CuponRepository extends JpaRepository<Cupon, UUID> {
}
