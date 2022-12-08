package ru.practicum.shareit.user.dto;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.user.model.User;


public class UserMapper {
    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    public static User toUser(UserDto userDto) {
        return MODEL_MAPPER.map(userDto, User.class);
    }

    public static User patchUser(User patchUser, User userToBePatched) {
        MODEL_MAPPER.getConfiguration().setSkipNullEnabled(true);
        MODEL_MAPPER.map(patchUser, userToBePatched);
        return userToBePatched;
    }
}
