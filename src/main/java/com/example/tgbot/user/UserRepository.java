package com.example.tgbot.user;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Scope(value = "prototype")
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT name FROM Group")
    List<String> findGroups();
}
