package com.paulotech.api_event.repositories;

import com.paulotech.api_event.domain.event.Event;
import com.paulotech.api_event.domain.event.EventAddressProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query("SELECT e.id AS id, e.title AS title, e.description AS description, e.date AS date, e.imgUrl AS imgUrl, e.eventUrl AS eventUrl, e.remote AS remote, a.city AS city, a.uf AS uf " +
            "FROM Event e LEFT JOIN Address a ON e.id = a.event.id " +
            "WHERE e.date >= :currentDate")
    Page<EventAddressProjection> findUpComingEvents(@Param("currentDate") Date currentDate, Pageable pageable);

    @Query("SELECT e FROM Event e LEFT JOIN e.address a " +
            "WHERE (:title = '' OR e.title LIKE CONCAT('%', :title, '%')) " +
            "AND (:city = '' OR a.city LIKE CONCAT('%', :city, '%')) " +
            "AND (:uf = '' OR a.uf LIKE CONCAT('%', :uf, '%')) " +
            "AND (e.date >= :startDate AND e.date <= :endDate)")
    Page<EventAddressProjection> findFilteredEvents(
            @Param("title") String title,
            @Param("city") String city,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable);


    @Query("SELECT e.id AS id, e.title AS title, e.description AS description, e.date AS date, e.imgUrl AS imgUrl, e.eventUrl AS eventUrl, e.remote AS remote, a.city AS city, a.uf AS uf " +
            "FROM Event e JOIN Address a ON e.id = a.event.id " +
            "WHERE (:title = '' OR e.title LIKE %:title%)")
    List<EventAddressProjection> findEventsByTitle(@Param("title") String title);
}
