package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.dao.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class ItemServiceImplMockTest {
    private Comment comment;
    private User user;
    private Item item;
    private UserService userMockService = Mockito.mock(UserServiceImpl.class);
    private ItemRepository itemMockRepository = Mockito.mock(ItemRepository.class);
    private BookingRepository bookingMockRepository = Mockito.mock(BookingRepository.class);
    private CommentRepository commentMockRepository = Mockito.mock(CommentRepository.class);
    private ItemService itemService = new ItemServiceImpl(itemMockRepository, bookingMockRepository, userMockService, commentMockRepository);

    @BeforeEach
    void createTestData() {
        comment = new Comment();
        comment.setText("some text");

        user = new User();
        user.setEmail("email@email.com");

        item = new Item();
        item.setOwner(user);
    }

    @Test
    void shouldThrowExceptionWhenAddCommentByIncorrectUser() {

        when(userMockService.getUserById(1L)).thenReturn(user);
        when(itemMockRepository.findById(any())).thenReturn(Optional.of(item));

        Throwable throwable = assertThrows(ResponseStatusException.class,
                () -> itemService.addComment(1L, 1L, comment));
        assertTrue(throwable.getMessage().contains("400"));
    }

    @Test
    void shouldThrowExceptionWhenGetItemByErrorId() {
        Throwable throwable = assertThrows(ResponseStatusException.class,
                () -> itemService.addComment(1L, 99L, comment));
        assertTrue(throwable.getMessage().contains("404"));
    }

    @Test
    void shouldAddComment() {
        when(userMockService.getUserById(1L)).thenReturn(user);
        when(itemMockRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingMockRepository
                .getBookingByBookerAndItemAndStatusEqualsAndEndBefore(any(), any(), eq(BookingStatus.APPROVED), any()))
                .thenReturn(Optional.of(new Booking()));
        when(commentMockRepository.save(any())).thenReturn(comment);
        comment.setId(1L);
        CommentDto commentDto = itemService.addComment(1L, 1L, comment);

        assertEquals("some text", commentDto.getText(), "Текст комментария не совпадает.");
        assertEquals(1L, commentDto.getId(), "Не совпадает идентефикатор комментария.");
    }
}
