package com.example.tgbot.disciplinegroup;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Scope(value = "prototype")
public interface DisciplineGroupRepository extends JpaRepository<DisciplineGroup, Long> {
//    List<DisciplineGroup> findTestGroupsByGroup_Name(String groupName);
    List<DisciplineGroup> findByGroup_Name(String groupName);
}
