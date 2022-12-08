package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final EntityManager manager;

    protected List<User> createTestUserIntoDb(Integer count) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            userList.add(new User());
            userList.get(i).setName("user" + (i + 1));
            userList.get(i).setEmail("email" + (i + 1) + "@email.com");
            manager.persist(userList.get(i));
        }
        manager.flush();
        return userList;
    }

    @Test
    void shouldGetRequestById() {
        User user = createTestUserIntoDb(1).get(0);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        manager.persist(itemRequest);
        manager.flush();
        ItemRequestDto itemRequestGetted = itemRequestService.getRequestById(user.getId(), itemRequest.getId());

        assertEquals(itemRequest.getId(),
                itemRequestGetted.getId(),
                "Id запроса не совпадает с ожидаемым");
        assertEquals("description",
                itemRequestGetted.getDescription(),
                "Описание завпроса вещи не совпадает с ожидаемым");
    }

    @Test
    void shouldNotGetRequestByWrongId() {
        User user = createTestUserIntoDb(1).get(0);
        Throwable throwable = assertThrows(ResponseStatusException.class,
                () -> itemRequestService.getRequestById(user.getId(), 999L));
        assertTrue(throwable.getMessage().contains("404"));
    }

    @Test
    void shouldCreateItemRequest() {
        User user = createTestUserIntoDb(1).get(0);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");
        ItemRequest itemRequest = itemRequestService.createItemRequest(user.getId(), itemRequestDto);

        assertEquals("description",
                itemRequest.getDescription(),
                "Описание запроса вещи не совпадает с ожидаемым");
    }

    @Test
    void shouldGetMyItemRequests() {
        List<User> userList = createTestUserIntoDb(2);
        User userOne = userList.get(0);
        User userTwo = userList.get(1);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("descriptionOne");
        ItemRequest itemRequestMy = itemRequestService.createItemRequest(userOne.getId(), itemRequestDto);
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("descriptionTwo");
        itemRequestService.createItemRequest(userTwo.getId(), itemRequestDto);

        List<ItemRequestDto> dtoList = itemRequestService.getMyItemRequests(userOne.getId());

        assertEquals(1, dtoList.size(), "Неверное количество запросов");
        assertEquals(itemRequestMy.getDescription(), dtoList.get(0).getDescription(),
                "Описание завпроса вещи не совпадает с ожидаемым");
    }

    @Test
    void shouldGetItemRequestsFromUser() {
        List<User> userList = createTestUserIntoDb(2);
        User userOne = userList.get(0);
        User userTwo = userList.get(1);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("descriptionOne");
        itemRequestService.createItemRequest(userOne.getId(), itemRequestDto);
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("descriptionTwo");
        ItemRequest itemRequestNotMy = itemRequestService.createItemRequest(userTwo.getId(), itemRequestDto);

        List<ItemRequestDto> dtoList = itemRequestService.getItemRequestFromUsers(userOne.getId(), 0, 10);

        assertEquals(1, dtoList.size(), "Неверное количество запросов");
        assertEquals(itemRequestNotMy.getDescription(), dtoList.get(0).getDescription(),
                "Описание завпроса вещи не совпадает с ожидаемым");
    }
}
