package ru.practicum.shareit.repsitory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    protected List<User> createTestUserIntoDb(Integer count) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            userList.add(new User());
            userList.get(i).setName("user" + (i + 1));
            userList.get(i).setEmail("email" + (i + 1) + "@email.com");
            em.getEntityManager().persist(userList.get(i));
        }
        em.getEntityManager().flush();
        return userList;
    }

    @Test
    void shouldGetItemRequestsByRequestor() {
        List<User> userList = createTestUserIntoDb(1);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(userList.get(0));
        itemRequest.setCreated(LocalDateTime.now());
        em.getEntityManager().persist(itemRequest);
        em.getEntityManager().flush();

        List<ItemRequest> itemRequestList = itemRequestRepository.getItemRequestsByRequestor(userList.get(0));

        assertEquals(1, itemRequestList.size(), "Неверный размер списка");
        assertEquals(itemRequest.getId(), itemRequestList.get(0).getId(), "Несовпадает id записей.");
    }

    @Test
    void shouldGetItemRequestsByRequestorIsNot() {
        List<User> userList = createTestUserIntoDb(2);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(userList.get(0));
        itemRequest.setCreated(LocalDateTime.now());
        em.getEntityManager().persist(itemRequest);
        em.getEntityManager().flush();

        List<ItemRequest> itemRequestList = itemRequestRepository
                .getItemRequestsByRequestorIsNot(userList.get(1), Pageable.ofSize(5)).stream().collect(Collectors.toList());

        assertEquals(1, itemRequestList.size(), "Неверный размер списка");
        assertEquals(itemRequest.getId(), itemRequestList.get(0).getId(), "Несовпадает id записей.");
    }
}
