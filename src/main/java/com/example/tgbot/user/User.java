package com.example.tgbot.user;

import com.example.tgbot.group.Group;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Scope;

import javax.persistence.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Scope(value = "prototype")
public class User {
    @Id
    @Column(name = "chat_id")
    Long chatId;
    @Column
    String name;
    @Column
    String surname;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_name", referencedColumnName = "name")
    Group group;
}
