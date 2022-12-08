package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceImplMockTest {


    @Test
    void shouldThrowExceptionWhenUpdateUserWithEmptyName() {

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserServiceImpl(userRepository);

        User patchUser = new User();
        patchUser.setName(" ");

        Throwable throwable = assertThrows(ResponseStatusException.class, () -> userService.updateUser(0, patchUser));
        assertTrue(throwable.getMessage().startsWith("500"));
    }


}
