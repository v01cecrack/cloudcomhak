package com.example.serversdk.first.disciplinegroup;

import com.example.serversdk.first.group.Group;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Scope(value = "prototype")
public interface DisciplineGroupRepository extends JpaRepository<DisciplineGroup, Long> {
    List<DisciplineGroup> findByGroup_Name(String groupName);

    void deleteAllByDiscipline_Id(Long id);
    void deleteAllByGroup_Id(Long id);

    List<DisciplineGroup> findDisciplineGroupsByGroupIn(List<Group> groups);

    List<DisciplineGroup> findDisciplineGroupsByDiscipline_Id(Long id);
}
