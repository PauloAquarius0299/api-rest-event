package com.paulotech.api_event.service;

import com.paulotech.api_event.domain.cupon.Cupon;
import com.paulotech.api_event.domain.cupon.CuponRequestDTO;
import com.paulotech.api_event.domain.event.Event;
import com.paulotech.api_event.repositories.CuponRepository;
import com.paulotech.api_event.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class CuponService {
    @Autowired
    private CuponRepository cuponRepository;

    @Autowired
    private EventRepository eventRepository;

    public Cupon addCuponToEvent(UUID eventId, CuponRequestDTO cuponData){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));

        Cupon cupon = new Cupon();
        cupon.setCode(cuponData.code());
        cupon.setDiscount(cuponData.discount());
        cupon.setValid(new Date(cuponData.valid()));
        cupon.setEvent(event);

        return cuponRepository.save(cupon);
    }
}
