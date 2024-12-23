package com.paulotech.api_event.repositories;

import com.paulotech.api_event.domain.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query("SELECT e FROM Event e WHERE e.date >= :currentDate")
    Page<Event> findUpComingEvents(@Param("currentDate") Date currentDate, Pageable pageable);

    @Query("SELECT e FROM Event e LEFT JOIN e.address a " +
            "WHERE (:title = '' OR e.title LIKE CONCAT('%', :title, '%')) " +
            "AND (:city = '' OR a.city LIKE CONCAT('%', :city, '%')) " +
            "AND (:uf = '' OR a.uf LIKE CONCAT('%', :uf, '%')) " +
            "AND (e.date >= :startDate AND e.date <= :endDate)")
    Page<Event> findFilteredEvents(
            @Param("title") String title,
            @Param("city") String city,
            @Param("uf") String uf,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable);


}
