package com.example.tgbot;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "chat_id")
    Long chatId;
    @Column
    String name;
    @Column
    String surname;
    @Column(name = "group_name")
    String group;
}
