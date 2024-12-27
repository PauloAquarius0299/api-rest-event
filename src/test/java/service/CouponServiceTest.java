package service;

import com.paulotech.api_event.domain.cupon.Cupon;
import com.paulotech.api_event.domain.cupon.CuponRequestDTO;
import com.paulotech.api_event.domain.event.Event;
import com.paulotech.api_event.repositories.CuponRepository;
import com.paulotech.api_event.repositories.EventRepository;
import com.paulotech.api_event.service.CuponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

class CouponServiceTest {
    @Mock
    private CuponRepository couponRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CuponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_shouldSaveCoupon() {
        UUID eventId = UUID.randomUUID();
        Event event = new Event();
        event.setId(eventId);
        CuponRequestDTO couponData = new CuponRequestDTO("TESTCODE", 20, new Date().getTime());
        Cupon coupon = new Cupon();
        coupon.setCode(couponData.code());
        coupon.setDiscount(couponData.discount());
        coupon.setValid(new Date(couponData.valid()));
        coupon.setEvent(event);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(couponRepository.save(any(Cupon.class))).thenReturn(coupon);

        Cupon savedCoupon = couponService.addCuponToEvent(eventId, couponData);

        assertNotNull(savedCoupon);
        assertEquals("TESTCODE", savedCoupon.getCode());
        verify(eventRepository, times(1)).findById(eventId);
        verify(couponRepository, times(1)).save(any(Cupon.class));
    }

    @Test
    void test_shouldThrowExceptionWhenEventNotFound() {
        UUID eventId = UUID.randomUUID();
        CuponRequestDTO couponData = new CuponRequestDTO("TESTCODE", 20, new Date().getTime());

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> couponService.addCuponToEvent(eventId, couponData));
        verify(eventRepository, times(1)).findById(eventId);
        verify(couponRepository, never()).save(any(Cupon.class));
    }

    @Test
    void test_shouldReturnListOfCoupons() {
        UUID eventId = UUID.randomUUID();
        Date currentDate = new Date();

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Teste de evento");
        event.setDescription("Descrição do evento");
        event.setDate(new Date());
        event.setEventUrl("https://evento.com");

        List<Cupon> coupons = List.of(new Cupon(eventId, "CP123", 20, currentDate, event));

        when(couponRepository.findByEventIdAndValidAfter(eventId, currentDate)).thenReturn(coupons);

        List<Cupon> result = couponService.consultCupons(eventId, currentDate);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("CP123", result.get(0).getCode());
        verify(couponRepository, times(1)).findByEventIdAndValidAfter(eventId, currentDate);
    }

    @Test
    void test_shouldReturnEmptyListWhenNoCouponsAvailable() {
        UUID eventId = UUID.randomUUID();
        Date currentDate = new Date();

        when(couponRepository.findByEventIdAndValidAfter(eventId, currentDate)).thenReturn(Collections.emptyList());

        List<Cupon> result = couponService.consultCupons(eventId, currentDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(couponRepository, times(1)).findByEventIdAndValidAfter(eventId, currentDate);
    }
}
