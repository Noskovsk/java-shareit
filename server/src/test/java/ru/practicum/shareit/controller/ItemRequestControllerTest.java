package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    private List<ItemRequestDto> requestDtoList;
    private List<ItemRequest> requestList;


    @BeforeEach
    void prepareData() {
        requestDtoList = List.of(new ItemRequestDto(), new ItemRequestDto());
        requestDtoList.get(0).setDescription("desc1");
        requestDtoList.get(0).setDescription("desc2");
        requestList = List.of(new ItemRequest(), new ItemRequest());
        requestList.get(0).setDescription("desc1");
        requestList.get(0).setId(1L);
        requestList.get(0).setDescription("desc2");
        requestList.get(0).setId(2L);
    }

    @Test
    void shouldCreateItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(eq(1L), any())).thenReturn(requestList.get(0));
        mvc.perform(post("/requests").content(mapper.writeValueAsString(requestDtoList.get(0)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestList.get(0).getId()), Long.class));
    }

    @Test
    void shouldGetItemById() throws Exception {
        when(itemRequestService.getRequestById(eq(1L), eq(1L))).thenReturn(requestDtoList.get(0));
        requestDtoList.get(0).setId(1L);
        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoList.get(0).getId()), Long.class));
    }

    @Test
    void shouldGetRequestListOfOwn() throws Exception {
        when(itemRequestService.getMyItemRequests(eq(1L))).thenReturn(requestDtoList);
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(requestDtoList.get(0).getId()), Long.class));
    }

    @Test
    void shouldFetRequestListFromOtherUser() throws Exception {
        when(itemRequestService.getItemRequestFromUsers(eq(1L), any(), any())).thenReturn(requestDtoList);
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(requestDtoList.get(0).getId()), Long.class));
    }
}
