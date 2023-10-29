package com.example.tgbot.test;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

@Scope(value = "prototype")
public interface TestRepository extends JpaRepository<Test, Long> {
    Test findByTestName(String testName);

}
