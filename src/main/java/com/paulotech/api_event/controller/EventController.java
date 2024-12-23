package com.paulotech.api_event.controller;

import com.paulotech.api_event.domain.event.Event;
import com.paulotech.api_event.domain.event.EventRequestDTO;
import com.paulotech.api_event.domain.event.EventResponseDTO;
import com.paulotech.api_event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Event> create(@RequestParam("title") String title,
                                        @RequestParam(value = "description", required = false) String description,
                                        @RequestParam("date") Long date,
                                        @RequestParam("city") String city,
                                        @RequestParam("state") String state,
                                        @RequestParam("remote") Boolean remote,
                                        @RequestParam("eventUrl") String eventUrl,
                                        @RequestParam(value="image",required = false)MultipartFile  image)
    {
        EventRequestDTO eventRequestDTO = new EventRequestDTO(title, description, date, city, state, remote, eventUrl, image);
        Event newEvent = this.eventService.createEvent(eventRequestDTO);
        return ResponseEntity.ok(newEvent);
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getEvent(@RequestParam(defaultValue = "C") int page,
                                                           @RequestParam(defaultValue = "10") int size){
        List<EventResponseDTO> allEvents = this.eventService.getEvents(page, size);
        return ResponseEntity.ok(allEvents);
    }
}
