package com.example.tgbot.result;

import com.example.tgbot.test.Test;
import com.example.tgbot.user.User;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Scope(value = "prototype")
public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findFirstByUserAndTest(User user, Test test);

}
