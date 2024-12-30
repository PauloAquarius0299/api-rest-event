package service;

import com.paulotech.api_event.domain.address.Address;
import com.paulotech.api_event.domain.event.Event;
import com.paulotech.api_event.domain.event.EventRequestDTO;
import com.paulotech.api_event.repositories.AddressRepository;
import com.paulotech.api_event.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

class AddressServiceTest {
    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_shouldSaveAddress() {
        Event event = new Event();
        event.setId(UUID.randomUUID());
        EventRequestDTO data = new EventRequestDTO("Teste Evento", "Descricao Evento", System.currentTimeMillis(), "Cidade Teste", "UF", true, "https://evento.com", null);
        Address address = new Address();
        address.setCity(data.city());
        address.setUf(data.state());
        address.setEvent(event);

        when(addressRepository.save(any(Address.class))).thenReturn(address);

        Address savedAddress = addressService.createAddress(data, event);

        assertNotNull(savedAddress);
        assertEquals(data.city(), savedAddress.getCity());
        assertEquals(data.state(), savedAddress.getUf());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void test_shouldReturnAddress() {
        UUID eventId = UUID.randomUUID();
        Address address = new Address();
        address.setCity("Cidade Teste");
        address.setUf("UF");

        when(addressRepository.findByEventId(eventId)).thenReturn(Optional.of(address));

        Optional<Address> result = addressService.findByEventId(eventId);

        assertTrue(result.isPresent());
        assertEquals("Cidade Teste", result.get().getCity());
        assertEquals("UF", result.get().getUf());
        verify(addressRepository, times(1)).findByEventId(eventId);
    }

    @Test
    void test_shouldReturnEmptyWhenAddressNotFound() {
        UUID eventId = UUID.randomUUID();

        when(addressRepository.findByEventId(eventId)).thenReturn(Optional.empty());

        Optional<Address> result = addressService.findByEventId(eventId);

        assertFalse(result.isPresent());
        verify(addressRepository, times(1)).findByEventId(eventId);
    }
}