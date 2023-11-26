package com.example.serversdk.auth.entities;

import com.example.serversdk.first.group.Group;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.persistence.CascadeType;
import java.util.Collection;

@Entity
@Data
@Table(name = "Users")
@Valid
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fullname")
    @NotEmpty
    private String fullname;

    @Column(name = "description")
    private String description;

    @Column(name = "password")
    @NotEmpty
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Collection<Group> groups;
}
