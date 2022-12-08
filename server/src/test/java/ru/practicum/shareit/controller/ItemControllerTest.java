package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

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

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private ItemService itemService;
    private List<ItemOwnerDto> itemOwnerDtoList;
    private List<ItemCreateDto> itemCreateDtos;
    private List<ItemPatchDto> itemPatchDtos;
    private List<Item> itemList;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void prepareData() {
        itemOwnerDtoList = List.of(new ItemOwnerDto(), new ItemOwnerDto());
        itemCreateDtos = List.of(new ItemCreateDto(), new ItemCreateDto());
        itemPatchDtos = List.of(new ItemPatchDto(), new ItemPatchDto());
        itemList = List.of(new Item(), new Item());
        itemOwnerDtoList.get(0).setId(1L);
        itemOwnerDtoList.get(1).setId(2L);
        itemList.get(0).setId(1L);
        itemList.get(1).setId(2L);
    }

    @Test
    void shouldGetItemById() throws Exception {
        when(itemService.getItemById(eq(1L), eq(1L))).thenReturn(itemOwnerDtoList.get(0));
        itemOwnerDtoList.get(0).setId(1L);
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemOwnerDtoList.get(0).getId()), Long.class));
    }

    @Test
    void shouldAddItem() throws Exception {
        when(itemService.addItem(eq(1L), any())).thenReturn(itemCreateDtos.get(0));
        itemCreateDtos.get(0).setName("name1");
        itemCreateDtos.get(0).setDescription("desc1");
        itemCreateDtos.get(0).setAvailable(true);
        mvc.perform(post("/items").content(mapper.writeValueAsString(itemCreateDtos.get(0)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemCreateDtos.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemCreateDtos.get(0).getName())));

    }

    @Test
    void shouldUpdateItem() throws Exception {
        when(itemService.updateItem(eq(1L), eq(1L), any())).thenReturn(itemList.get(0));
        itemPatchDtos.get(0).setName("nameUpdated");
        itemList.get(0).setId(1L);
        itemList.get(0).setName("nameUpdated");
        mvc.perform(patch("/items/1").content(mapper.writeValueAsString(itemPatchDtos.get(0)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("nameUpdated")));
    }

    @Test
    void shouldSearchItems() throws Exception {
        when(itemService.searchItems(any(), any(), any())).thenReturn(itemList);
        mvc.perform(get("/items/search?text=search")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(itemList.get(1).getId()), Long.class));
    }

    @Test
    void shouldGetItemsByUserId() throws Exception {
        when(itemService.getItemByUserId(eq(1L), any(), any())).thenReturn(itemOwnerDtoList);
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemOwnerDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(itemOwnerDtoList.get(1).getId()), Long.class));
    }

    @Test
    void shouldAddComment() throws Exception {
        Comment comment = new Comment();
        comment.setText("comment");
        CommentDto commentDto = CommentDto.builder().text("comment").build();
        when(itemService.addComment(any(), any(), any())).thenReturn(commentDto);
        mvc.perform(post("/items/1/comment").content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }
}
