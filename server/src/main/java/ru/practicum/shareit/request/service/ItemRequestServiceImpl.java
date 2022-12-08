package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.pagination.PaginationParams;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public ItemRequest createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на добавление запроса на вещь: {}, от пользователя с userId: {}", itemRequestDto.getDescription(), userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(userService.getUserById(userId));
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        log.info("Получен запрос на поиск запроса вещи с id: {}, от пользователя с userId: {}", requestId, userId);
        userService.getUserById(userId);
        Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(requestId);
        if (itemRequestOptional.isEmpty()) {
            log.error("Ошибка при поиске запроса вещи с id: {}", requestId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при поиске запроса вещи!");
        } else {
            List<ItemCreateDto> itemCreateDtos = itemService
                    .getItemsByRequest(itemRequestOptional.get())
                    .stream()
                    .map(ItemMapper::toItemCreateDto)
                    .collect(Collectors.toList());
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequestOptional.get());
            itemRequestDto.setItems(itemCreateDtos);
            return itemRequestDto;
        }
    }

    @Override
    public List<ItemRequestDto> getMyItemRequests(Long userId) {
        log.info("Получен запрос на поиск всех вещей пользователя с id: {}", userId);
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository
                .getItemRequestsByRequestor(userService.getUserById(userId))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        collectItemsFromRequests(itemRequestDtos);
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getItemRequestFromUsers(Long userId, Integer from, Integer size) {
        log.info("Получен запрос на поиск всех запросов вещей от пользователя с id: {}", userId);
        PageRequest pageRequest = PaginationParams.createPageRequest(from, size, "created");
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository
                .getItemRequestsByRequestorIsNot(userService.getUserById(userId), pageRequest)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        collectItemsFromRequests(itemRequestDtos);
        return itemRequestDtos;
    }

    private void collectItemsFromRequests(List<ItemRequestDto> itemRequestDtos) {
        itemRequestDtos.forEach(i -> i.setItems(
                itemService.getItemsByRequest(ItemRequestMapper.toItemRequest(i))
                        .stream()
                        .map(ItemMapper::toItemCreateDto)
                        .collect(Collectors.toList())));
    }
}
