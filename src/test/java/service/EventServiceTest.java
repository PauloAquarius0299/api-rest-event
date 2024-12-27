package service;

import com.amazonaws.services.s3.AmazonS3;
import com.paulotech.api_event.domain.event.*;
import com.paulotech.api_event.repositories.EventRepository;
import com.paulotech.api_event.service.AddressService;
import com.paulotech.api_event.service.CuponService;
import com.paulotech.api_event.service.EventService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.awt.print.Pageable;
import java.util.*;

class EventServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private AddressService addressService;

    @Mock
    private CuponService cuponService;

    @Mock
    private EventRepository repository;

    @InjectMocks
    private EventService eventService;

    private final String adminkey = "test-admin-key";
    private final String bucketName = "test-bucket";

   @Test
    void createEvent_shouldSaveEventSuccessfully(){
       EventRequestDTO requestDTO = new EventRequestDTO("Evento Teste", "Descrição do evento", new Date().getTime(),
               "Cidade Teste", "UF", true, "https://evento.com", null);
       Event event = new Event();

       when(repository.save(any(Event.class))).thenReturn(event);

       Event savedEvent = eventService.createEvent(requestDTO);

       assertNotNull(savedEvent);
       verify(repository, times(1)).save(any(Event.class));
   }

   @Test
    void test_shouldReturnListOfEvents(){
       Pageable pageable = (Pageable) PageRequest.of(0, 10);
       List<EventAddressProjection> events = List.of(mock(EventAddressProjection.class));
       Page<EventAddressProjection> eventsPage = new PageImpl<>(events);

       when(repository.findUpComingEvents(any(Date.class), eq((org.springframework.data.domain.Pageable) pageable))).thenReturn(eventsPage);

       List<EventResponseDTO> result = eventService.getUpcomingEvents(0, 10);

       assertFalse(result.isEmpty());
       verify(repository, times(1)).findUpComingEvents(any(Date.class), (org.springframework.data.domain.Pageable) eq(pageable));
   }

   @Test
    void test_shouldReturnEventDetails(){
       UUID eventId = UUID.randomUUID();
       Event event = new Event();
       event.setId(eventId);
       event.setTitle("Teste do evento");
       event.setDescription("Descrinção teste do evento");
       event.setDate(new Date());
       event.setEventUrl("https://evento.com");

       when(repository.findById(eventId)).thenReturn(Optional.of(event));
       when(addressService.findByEventId(eventId)).thenReturn(Optional.empty());
       when(cuponService.consultCupons(eventId, new Date())).thenReturn(Collections.emptyList());


       EventDetailsDTO result = eventService.getEventDetails(eventId);

       assertNotNull(result);
       assertEquals(eventId, result.id());
       verify(repository, times(1)).findById(eventId);
   }

   @Test
    void test_shouldDeleteEvent(){
       UUID eventId = UUID.randomUUID();
       Event event = new Event();
       event.setId(eventId);

       when(repository.findById(eventId)).thenReturn(Optional.of(event));

       eventService.deleteEvent(eventId, "test-admin-key");

       verify(repository, times(1)).delete(event);
   }

   @Test
    void test_shouldReturnFilteredEvents(){
       PageRequest pegeable = PageRequest.of(0, 10);
       List<EventAddressProjection> events = List.of(mock(EventAddressProjection.class));
       Page<EventAddressProjection> eventsPage = new PageImpl<>(events);
       
       when(repository.findFilteredEvents(anyString(), anyString(), any(Date.class), any(Date.class), eq(pegeable))).thenReturn(eventsPage);

       List<EventResponseDTO> result = eventService.getFilteredEvents(0, 10, "Cidade teste", "UF", new Date(), new Date());

       assertFalse(result.isEmpty());
       verify(repository, times(1)).findFilteredEvents(anyString(), anyString(), any(Date.class), any(Date.class),
               eq(org.springframework.data.domain.Pageable.unpaged()));
   }

   @Test
    void test_shouldReturnUrlOnUploadImage() throws Exception{
       MultipartFile multipartFile = mock(MultipartFile.class);
       when(multipartFile.getBytes()).thenReturn(new byte[0]);
       when(multipartFile.getOriginalFilename()).thenReturn("imagem.jpg");

       ReflectionTestUtils.setField(eventService, "bucketName", bucketName);
       String result = ReflectionTestUtils.invokeMethod(eventService, "uploadImg", multipartFile);

       assertNotNull(result);
       assertTrue(result.contains("https://s3.amazonaws.com"));
   }
}
