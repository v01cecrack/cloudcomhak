package com.example.tgbot;

import lombok.*;

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
public class Group {
    @Id
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "group")
    private List<User> users;
}
