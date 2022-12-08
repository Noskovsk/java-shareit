package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "db.name=testItem",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceImplTest {
    private final ItemService itemService;
    private final EntityManager entityManager;


    protected List<User> createTestUserIntoDb(Integer count) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            userList.add(new User());
            userList.get(i).setName("user" + (i + 1));
            userList.get(i).setEmail("email" + (i + 1) + "@email.com");
            entityManager.persist(userList.get(i));
        }
        entityManager.flush();
        return userList;
    }

    @Test
    void shouldReturnOneItemOfUser() {
        List<User> userList = createTestUserIntoDb(2);
        List<ItemCreateDto> itemCreateDtos = List.of(new ItemCreateDto(), new ItemCreateDto());
        int counter = 0;
        for (ItemCreateDto itemCreate : itemCreateDtos) {
            itemCreate.setName("Item" + counter);
            itemCreate.setDescription("ItemDesc" + counter);
            itemCreate.setAvailable(true);
            itemService.addItem(userList.get(counter).getId(), itemCreate);
            counter++;
        }
        assertEquals(1, itemService.getItemByUserId(userList.get(1).getId(), 0, 10).size(), "Количество вещей не совпадает");
    }

    @Test
    void shouldUpdateItem() {
        List<User> userList = createTestUserIntoDb(1);
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Item1");
        itemCreateDto.setDescription("ItemDesc");
        itemCreateDto.setAvailable(true);
        ItemCreateDto itemCreateDtoDb = itemService.addItem(userList.get(0).getId(), itemCreateDto);

        ItemPatchDto itemPatchDto = new ItemPatchDto();
        itemPatchDto.setDescription("updDesc");
        Item item = itemService.updateItem(userList.get(0).getId(), itemCreateDtoDb.getId(), itemPatchDto);

        assertEquals(itemPatchDto.getDescription(), item.getDescription(), "Описание не совпадает");
    }

    @Test
    void shouldSearchByString() {
        List<User> userList = createTestUserIntoDb(2);
        List<ItemCreateDto> itemCreateDtos = List.of(new ItemCreateDto(), new ItemCreateDto());
        int counter = 0;
        for (ItemCreateDto itemCreate : itemCreateDtos) {
            itemCreate.setName("Item" + counter);
            itemCreate.setDescription("ItemDesc" + counter);
            itemCreate.setAvailable(true);
            itemService.addItem(userList.get(counter).getId(), itemCreate);
            counter++;
        }
        assertEquals(1, itemService.searchItems("ItemDesc1", 0, 10).size(), "Количество вещей не совпадает");
    }

    @Test
    void shouldReturnEmtyListWhenEmtySearchString() {
        List<Item> itemList = itemService.searchItems("", 0, 50);
        assertEquals(0, itemList.size(), "Количество вещей не совпадает");
    }

    @Test
    void shouldNotUpdateOtherUserItem() {
        List<User> userList = createTestUserIntoDb(2);
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Item1");
        itemCreateDto.setDescription("ItemDesc");
        itemCreateDto.setAvailable(true);
        ItemCreateDto itemCreateDtoDb = itemService.addItem(userList.get(0).getId(), itemCreateDto);

        ItemPatchDto itemPatchDto = new ItemPatchDto();
        itemPatchDto.setDescription("updDesc");

        Throwable throwable = assertThrows(ResponseStatusException.class,
                () -> itemService.updateItem(userList.get(1).getId(), itemCreateDtoDb.getId(), itemPatchDto));
        assertTrue(throwable.getMessage().contains("404"));
    }
}
