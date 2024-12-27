package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulotech.api_event.controller.EventController;
import com.paulotech.api_event.domain.event.Event;
import com.paulotech.api_event.domain.event.EventDetailsDTO;
import com.paulotech.api_event.domain.event.EventRequestDTO;
import com.paulotech.api_event.domain.event.EventResponseDTO;
import com.paulotech.api_event.service.EventService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@WebMvcTest(EventController.class)
class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void test_createEventSuccess() throws Exception {
        EventRequestDTO requestDTO = new EventRequestDTO("Evento", "descrinção teste event", new Date().getTime(),
                "Cidade teste", "UF", true, "https://www.event.com", null);
        Event responseEvent = new Event();

        when(eventService.createEvent(requestDTO)).thenReturn(responseEvent);

        mockMvc.perform(multipart("/api/event")
                        .file("image", new byte[0])
                        .param("title", requestDTO.title())
                        .param("description", requestDTO.description())
                        .param("date", String.valueOf(requestDTO.date()))
                        .param("city", requestDTO.city())
                        .param("state", requestDTO.state())
                        .param("remote", String.valueOf(requestDTO.remote()))
                        .param("eventUrl", requestDTO.eventUrl())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    void test_getEventDetails() throws Exception {
        UUID eventId = UUID.randomUUID();
        List<EventDetailsDTO.CuponDTO> cupons = List.of(
                new EventDetailsDTO.CuponDTO("CODE123", 10, new Date()),
                new EventDetailsDTO.CuponDTO("CODE456", 20, new Date())
        );
        EventDetailsDTO eventDetailsDTO = new EventDetailsDTO(
                UUID.fromString("b4919825-dd11-4f6c-b77d-faffb48c1801"),
                "Test de evento",
                "O maior evento do munod",
                new Date(),
                "Belo Horizonte",
                "MG",
                "",
                "https://www.event.com",
                cupons
        );

        when(eventService.getEventDetails(eventId)).thenReturn(eventDetailsDTO);

        mockMvc.perform(get("/api/event/{eventId}", eventId)).andExpect(status().isOk()).andExpect(jsonPath("$.title").exists());
    }

    @Test
    void test_getEvents() throws Exception {
        List<EventResponseDTO> responseList = getEventResponseDTO();

        when(eventService.getUpcomingEvents(0, 10)).thenReturn(responseList);

        mockMvc.perform(get("/api/event")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void test_deleteEvent() throws Exception {
        UUID eventId = UUID.randomUUID();
        String adminKey = "testAdminKey";

        doNothing().when(eventService).deleteEvent(eventId, adminKey);

        mockMvc.perform(delete("/api/event/{eventId}", eventId)
                        .content(adminKey)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isNoContent());
    }

    @Test
    void test_getFilteredEvents() throws Exception {
        List<EventResponseDTO> responseList = getEventResponseDTO();
        String startDate = "2024-10-24";
        String endDate = "2024-10-25";

        when(eventService.getFilteredEvents(0, 10, "Brasilia", "DF",
                new SimpleDateFormat("yyyy-MM-dd").parse(startDate),
                new SimpleDateFormat("yyyy-MM-dd").parse(endDate))).thenReturn(responseList);

        mockMvc.perform(get("/api/event/filter")
                        .param("page", "0")
                        .param("size", "10")
                        .param("city", "Brasilia")
                        .param("uf", "DF")
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void test_getSearchEvents() throws Exception {
        List<EventResponseDTO> responseList = getEventResponseDTO();

        when(eventService.searchEvents("evento")).thenReturn(responseList);

        mockMvc.perform(get("/api/event/search")
                        .param("title", "evento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    private static List<EventResponseDTO> getEventResponseDTO() {
        return List.of(new EventResponseDTO(
                UUID.fromString("b4919825-dd11-4f6c-b77d-faffb48c1801"),
                "Teste de evento",
                "A maior conferencia do mundo",
                new Date(),
                "Brasilia",
                "DF",
                false,
                "https://www.teste.com",
                ""
        ));
    }
}
