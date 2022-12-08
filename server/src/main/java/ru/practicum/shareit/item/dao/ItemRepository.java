package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> getItemsByOwnerOrderById(User owner, Pageable pageRequest);

    List<Item> getItemsByRequest(ItemRequest itemRequest);

    @Query("SELECT i FROM items i WHERE i.available IS TRUE AND (LOWER(i.name) LIKE %:searchString% " +
            "OR LOWER(i.description) LIKE %:searchString%)")
    Page<Item> searchAllByString(String searchString, Pageable pageRequest);
}
