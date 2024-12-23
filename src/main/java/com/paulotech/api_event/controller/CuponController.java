package com.paulotech.api_event.controller;

import com.paulotech.api_event.domain.cupon.Cupon;
import com.paulotech.api_event.domain.cupon.CuponRequestDTO;
import com.paulotech.api_event.service.CuponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/coupon")
public class CuponController {

    @Autowired
    private CuponService cuponService;

    @PostMapping("/event/{eventId}")
    public ResponseEntity<Cupon> addCuponToEvent(@PathVariable UUID eventId, @RequestBody CuponRequestDTO data){
        Cupon cupons = cuponService.addCuponToEvent(eventId, data);
        return ResponseEntity.ok(cupons);
    }
}
