package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;
    private List<User> userList;

    @BeforeEach
    void prepareData() {
        userList = List.of(new User(), new User());
        userList.get(0).setId(1L);
        userList.get(0).setName("one");
        userList.get(0).setEmail("one@one.com");
        userList.get(1).setId(2L);
        userList.get(1).setName("two");
        userList.get(1).setEmail("two@two.com");
    }

    @Test
    void shouldGetUserById() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(userList.get(0));
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userList.get(0).getId()), Long.class));
    }

    @Test
    void shouldGetListOfUsers() throws Exception {
        when(userService.listUsers()).thenReturn(userList);
        mvc.perform(get("/users"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(userList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(userList.get(1).getId()), Long.class));
    }

    @Test
    void shouldCreateUser() throws Exception {
        when(userService.createUser(userList.get(0))).thenReturn(userList.get(0));
        mvc.perform(post("/users").content(mapper.writeValueAsString(userList.get(0)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userList.get(0).getId()), Long.class));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        userList.get(0).setName("myNameIs");
        when(userService.updateUser(1L, userList.get(0))).thenReturn(userList.get(0));
        mvc.perform(patch("/users/1").content(mapper.writeValueAsString(userList.get(0)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userList.get(0).getName())));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
