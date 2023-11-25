package com.example.serversdk.first.user;

import com.example.serversdk.first.group.Group;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Scope(value = "prototype")
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query(value = "SELECT name FROM Group")
    List<String> findGroups();

    @Query("SELECT NEW com.example.serversdk.first.user.StudentDto(S.surname, S.name, S.fatherName) from Student S " +
            "JOIN Group G ON S.group.name = G.name " +
            "WHERE G.name = :groupName")
    List<StudentDto> findStudentsByGroupName(@Param("groupName") String groupName);

    void deleteStudentByChatId(Long chatId);

    List<Student> findAllByGroup_Id(Long id);

    void deleteAllByGroup_Id(Long id);

}
