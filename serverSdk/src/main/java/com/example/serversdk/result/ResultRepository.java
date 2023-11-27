package com.example.serversdk.result;

import com.example.serversdk.first.dtos.ResultDto;
import com.example.serversdk.first.test.Test;
import com.example.serversdk.first.user.Student;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Scope(value = "prototype")
public interface ResultRepository extends JpaRepository<Result, Long> {


    void deleteAllByUserIn(List<Student> students);

    @Query("SELECT NEW com.example.serversdk.first.dtos.ResultDto(U.chatId, U.surname, U.name, U.fatherName, T.testName, " +
            "SUM(CASE WHEN R.answer = A.answer AND A.correct = true THEN 1 ELSE 0 END), " +
            "COUNT(DISTINCT R.id)) " +
            "FROM Student U " +
            "JOIN Result R ON U.chatId = R.user.chatId " +
            "JOIN Test T ON R.test.Id = T.Id " +
            "JOIN Question Q ON R.question.id = Q.id " +
            "JOIN Answer A ON Q.id = A.question.id " +
            "JOIN Discipline D ON T.discipline.id = D.id " +
            "JOIN Group G ON U.group.id = G.id " +
            "WHERE G.id = :groupId AND D.id = :disciplineId " +
            "GROUP BY U.chatId, U.surname, U.name, U.fatherName, T.testName")
    List<ResultDto> getGroupDisciplineResults(@Param("groupId") Long groupId, @Param("disciplineId") Long disciplineId);




}
