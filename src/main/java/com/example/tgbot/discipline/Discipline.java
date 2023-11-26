package com.example.tgbot.discipline;

import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.context.annotation.Scope;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Disciplines")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Scope(value = "prototype")
public class Discipline implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    @Unique
    private String name;
}
