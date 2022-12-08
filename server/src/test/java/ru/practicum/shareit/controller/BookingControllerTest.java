package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingService bookingService;
    private List<Booking> bookingList;

    @BeforeEach
    void prepareData() {
        bookingList = List.of(new Booking(), new Booking());
    }

    @Test
    void shouldGetBookingById() throws Exception {
        when(bookingService.getBookingById(eq(1L), eq(1L))).thenReturn(bookingList.get(0));
        bookingList.get(0).setId(1L);
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingList.get(0).getId()), Long.class));
    }

    @Test
    void shouldCreateBooking() throws Exception {
        when(bookingService.createBooking(eq(1L), any())).thenReturn(bookingList.get(0));
        BookingDto bookingDto = BookingDto.builder().id(1L).build();
        mvc.perform(post("/bookings").content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingList.get(0).getId()), Long.class));
    }

    @Test
    void shouldUpdateBooking() throws Exception {
        when(bookingService.updateBooking(eq(1L), eq(1L), eq(true))).thenReturn(bookingList.get(0));
        mvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingList.get(0).getId()), Long.class));
    }

    @Test
    void shouldGetBookingsOfUser() throws Exception {
        when(bookingService.getBookingsOfUser(eq(1L), any(), any(), any())).thenReturn(bookingList);
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldGetBookingsOfOwner() throws Exception {
        when(bookingService.getBookingsOfOwner(eq(1L), any(), any(), any())).thenReturn(bookingList);
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
