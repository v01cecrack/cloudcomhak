package com.example.tgbot.user;

import org.springframework.context.annotation.Scope;

@Scope(value = "prototype")
public class UserMapper {
    public static User toUser(UserDto userDto) {
        return User.builder()
                .chatId(userDto.getChatId())
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .fatherName(userDto.getFatherName())
                .university(userDto.getUniversity())
                .group(userDto.getGroup())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .chatId(user.getChatId())
                .name(user.getName())
                .surname(user.getSurname())
                .fatherName(user.getFatherName())
                .university(user.getUniversity())
                .group(user.getGroup())
                .build();
    }
}
