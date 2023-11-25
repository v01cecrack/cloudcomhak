package com.example.serversdk.first.disciplinegroup;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Scope(value = "prototype")
public interface DisciplineGroupRepository extends JpaRepository<DisciplineGroup, Long> {
    List<DisciplineGroup> findByGroup_Name(String groupName);

    void deleteAllByDiscipline_Id(Long id);
    void deleteAllByGroup_Id(Long id);
}
