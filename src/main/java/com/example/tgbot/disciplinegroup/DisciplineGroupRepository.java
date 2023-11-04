package com.example.tgbot.disciplinegroup;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Scope(value = "prototype")
public interface DisciplineGroupRepository extends JpaRepository<DisciplineGroup, Long> {
//    List<DisciplineGroup> findTestGroupsByGroup_Name(String groupName);
    List<DisciplineGroup> findDisciplineGroupsByGroup_Name(String groupName);
}
