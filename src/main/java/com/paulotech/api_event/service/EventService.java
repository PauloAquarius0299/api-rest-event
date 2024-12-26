package com.paulotech.api_event.service;

import com.amazonaws.services.s3.AmazonS3;
import com.paulotech.api_event.domain.cupon.Cupon;
import com.paulotech.api_event.domain.event.*;
import com.paulotech.api_event.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CuponService cuponService;

    public Event createEvent(EventRequestDTO data){
        String imgUrl = null;

        if(data.image() != null){
            imgUrl = this.uploadImg(data.image());
        }

        Event newEvent = new Event();
        newEvent.setTitle(data.title());
        newEvent.setDescription(data.description());
        newEvent.setEventUrl(data.eventUrl());
        newEvent.setDate(new Date(data.date()));
        newEvent.setImgUrl(imgUrl);
        newEvent.setRemote(data.remote());

        eventRepository.save(newEvent);

        if(!data.remote()){
            this.addressService.createAddress(data, newEvent);
        }

        return newEvent;
    }

    public List<EventResponseDTO> getUpcomingEvents(int page, int size){
        Pageable pageable =  PageRequest.of(page, size);
        Page<EventAddressProjection> eventsPage = this.eventRepository.findUpComingEvents(new Date(), pageable);
        return eventsPage.map(event -> new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                        event.getCity() != null ? event.getCity() : "",
                        event.getUf() != null ? event.getUf() : "",
                event.getRemote(),
                event.getEventUrl(),
                event.getImgUrl()))
                .stream()
                .toList();
    }


    public List<EventResponseDTO> getFilteredEvents(int page, int size, String city, String uf, Date startDate, Date endDate){

        city = (city != null) ? city : "";
        uf = (uf != null) ? uf : "";
        startDate = (startDate != null) ? startDate : new Date(0);
        endDate = (endDate != null) ? endDate : new Date();

        Pageable pageable =  PageRequest.of(page, size);
        Page<EventAddressProjection> eventsPage = this.eventRepository.findFilteredEvents(city, uf, startDate, endDate, pageable);
        return eventsPage.map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getCity() != null ? event.getCity() : "",
                        event.getUf() != null ? event.getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl()))
                .stream()
                .toList();
    }

    private String uploadImg(MultipartFile multipartFile){
        String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        try{
            File file = this.convertMultiparToFile(multipartFile);
            s3Client.putObject(bucketName, fileName, file);
            file.delete();
            return s3Client.getUrl(bucketName, fileName).toString();
        }catch(Exception e){
            System.out.println("Erro ao subir o arquivo");
            return "";
        }
    }

    private File convertMultiparToFile(MultipartFile multipartFile) throws IOException {
        File convFile = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }

    public EventDetailsDTO getEventDetails(UUID eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        List<Cupon> coupons = cuponService.consultCupons(eventId, new Date());

        List<EventDetailsDTO.CuponDTO> cuponDTOS = coupons.stream()
                .map(cupon -> new EventDetailsDTO.CuponDTO(
                        cupon.getCode(),
                        cupon.getDiscount(),
                        cupon.getValid()
                )).collect(Collectors.toList());

        return new EventDetailsDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddress() != null ? event.getAddress().getCity() : "",
                event.getAddress() != null ? event.getAddress().getUf() : "",
                event.getImgUrl(),
                cuponDTOS);
    }

    public List<EventResponseDTO> searchEvents(String title){
        title = (title != null) ? title : "";

        List<EventAddressProjection> eventsList = this.eventRepository.findEventsByTitle(title);
        return eventsList.stream().map(event -> new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getCity() != null ? event.getCity() : "",
                event.getUf() != null ? event.getUf() : "",
                event.getRemote(),
                event.getEventUrl(),
                event.getImgUrl()
        )).toList();
    }
}
