package com.example.serversdk.auth.entities;

import com.example.serversdk.first.group.Group;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@Data
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    @NotNull
    private String username;

    @Column(name = "password")
    @NotNull
    private String password;

    @Column(name = "email")
    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Roles role;

    @ManyToMany
    @JoinTable(
            name = "Users_Groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Collection<Group> groups;
}
