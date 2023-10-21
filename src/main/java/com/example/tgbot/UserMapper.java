package com.example.tgbot;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        return User.builder()
                .chatId(userDto.getChatId())
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .group(userDto.getGroup())
                .build();
    }
}
