package com.example.tgbot.group;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, String> {
    List<Group> findAll();
}
