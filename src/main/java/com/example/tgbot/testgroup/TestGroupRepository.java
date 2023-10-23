package com.example.tgbot.testgroup;

import com.example.tgbot.test.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestGroupRepository extends JpaRepository<TestGroup, Long> {
    List<TestGroup> findTestGroupsByGroup_Name(String groupName);
}
