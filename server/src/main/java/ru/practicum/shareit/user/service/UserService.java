package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User getUserById(long userId);

    User createUser(User user);

    User updateUser(long userId, User patchUser);

    List<User> listUsers();

    void deleteUser(Long userId);
}
