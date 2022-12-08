package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.dao.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pagination.PaginationParams;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    @Override
    public ItemOwnerDto getItemById(long userId, long itemId) {
        log.info("Получен запрос на поиск вещи с id: {}", itemId);
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            log.error("Ошибка при поиске вещи с itemId: {}", itemId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при поиске вещи!");
        } else {
            if (itemOptional.get().getOwner().equals(userService.getUserById(userId))) {
                return addBookingInfoAndReturnItemDto(itemOptional.get());
            } else {
                return ItemMapper.toItemOwnerDto(itemOptional.get());
            }
        }
    }

    private ItemOwnerDto addBookingInfoAndReturnItemDto(Item item) {
        ItemOwnerDto itemOwnerDto = ItemMapper.toItemOwnerDto(item);
        Optional<Booking> lastBooking = bookingRepository.getBookingByItemAndEndBeforeOrderByEndDesc(item, LocalDateTime.now());
        lastBooking.ifPresent(booking -> itemOwnerDto.setLastBooking(BookingMapper.toBookingDto(booking)));
        Optional<Booking> nextBooking = bookingRepository.getBookingByItemAndStartAfterOrderByStartAsc(item, LocalDateTime.now());
        nextBooking.ifPresent(booking -> itemOwnerDto.setNextBooking(BookingMapper.toBookingDto(booking)));
        return itemOwnerDto;
    }

    @Override
    public ItemCreateDto addItem(Long userId, ItemCreateDto itemCreateDto) {
        log.info("Получен запрос на добавление вещи пользователя с id: {}. Вещь: {}", userId, itemCreateDto.getName());
        Item item = ItemMapper.toItem(itemCreateDto);
        User owner = userService.getUserById(userId);
        item.setOwner(owner);
        itemCreateDto = ItemMapper.toItemCreateDto(itemRepository.save(item));
        return itemCreateDto;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, ItemPatchDto itemPatchDto) {
        log.info("Получен запрос на обновление данных вещи пользователя с id: {}. Вещь: {}", userId, itemId);
        Item itemPatch = ItemMapper.toItem(itemPatchDto);
        if (!ItemMapper.toItem(getItemById(userId, itemId))
                .getOwner()
                .getId()
                .equals(userId)) {
            log.error("Ошибка при обновлении вещи: {}. Вещь не относится к текущему пользователю id = {}.", itemId, userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при обновлении вещи: " + itemId
                    + ". Вещь не относится к текущему пользователю id = " + userId + ".");
        }
        return itemRepository.save(ItemMapper.patchItem(itemPatch, ItemMapper.toItem(getItemById(userId, itemId))));
    }

    @Override
    public List<ItemOwnerDto> getItemByUserId(Long userId, Integer from, Integer size) {
        log.info("Получен запрос на получение списка вещей пользователя с id: {}.", userId);
        PageRequest pageRequest = PaginationParams.createPageRequest(from, size);
        return itemRepository
                .getItemsByOwnerOrderById(userService.getUserById(userId), pageRequest)
                .stream()
                .map(this::addBookingInfoAndReturnItemDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<Item> searchItems(String text, Integer from, Integer size) {
        log.info("Получен запрос на поиск вещей по фразе: {}.", text);
        PageRequest pageRequest = PaginationParams.createPageRequest(from, size);
        if (text.isBlank()) {
            log.info("Получен запрос на поиск вещей по пустой фразе!");
            return Collections.emptyList();
        }
        return itemRepository.searchAllByString(text.toLowerCase(), pageRequest).stream().collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, Comment comment) {
        User booker = userService.getUserById(userId);
        Item item = ItemMapper.toItem(getItemById(userId, itemId));
        Optional<Booking> optionalBooking = bookingRepository
                .getBookingByBookerAndItemAndStatusEqualsAndEndBefore(booker, item, BookingStatus.APPROVED, LocalDateTime.now());
        if (optionalBooking.isEmpty()) {
            log.error("Ошибка при добавлении отзыва к вещи: {}. " +
                            "Вещь не бралась в аренду пользователем с id = {}.",
                    itemId,
                    userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка при добавлении отзыва к вещи!");
        } else {
            comment.setAuthor(booker);
            comment.setItem(item);
            comment.setCreated(LocalDateTime.now());
            comment = commentRepository.save(comment);
            return CommentMapper.toCommentDto(comment);
        }

    }

    @Override
    public List<Item> getItemsByRequest(ItemRequest itemRequest) {
        return itemRepository.getItemsByRequest(itemRequest);
    }
}
