package com.example.tgbot.group;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
@Scope(value = "prototype")
public interface GroupRepository extends JpaRepository<Group, String> {
    List<Group> findAll();
}
