package com.example.tgbot.group;

import com.example.tgbot.user.User;
import lombok.*;
import org.springframework.context.annotation.Scope;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Groups")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Scope(value = "prototype")
public class Group {   //TODO ID ID
    @Id
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "group")
    private List<User> users;
}
