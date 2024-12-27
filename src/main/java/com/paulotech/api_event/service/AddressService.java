package com.paulotech.api_event.service;

import com.paulotech.api_event.domain.address.Address;
import com.paulotech.api_event.domain.event.Event;
import com.paulotech.api_event.domain.event.EventRequestDTO;
import com.paulotech.api_event.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public Address createAddress(EventRequestDTO date, Event event){
        Address address = new Address();
        address.setCity(date.city());
        address.setUf(date.state());
        address.setEvent(event);

        return addressRepository.save(address);
    }

    public Optional<Address> findByEventId(UUID eventId) {
        return addressRepository.findByEventId(eventId);
    }
}
