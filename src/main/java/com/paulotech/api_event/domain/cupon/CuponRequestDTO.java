package com.paulotech.api_event.domain.cupon;

public record CuponRequestDTO(String code, Integer discount, Long valid) {
}
