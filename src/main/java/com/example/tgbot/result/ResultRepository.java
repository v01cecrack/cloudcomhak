package com.example.tgbot.result;

import com.example.tgbot.test.Test;
import com.example.tgbot.user.User;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Scope(value = "prototype")
public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findFirstByUserAndTest(User user, Test test);

    @Query("select D.name, T.testName, U.chatId, COUNT(DISTINCT R.id) AS total_answers, SUM(CASE WHEN R.answer = A.answer AND A.correct = true then 1 else 0 end) as correct_answers " +
            "FROM User U " +
            "JOIN Result R ON U.chatId = R.user.chatId " +
            "JOIN Test T ON R.test.testId = T.testId " +
            "JOIN Question Q ON R.question.id = Q.id " +
            "JOIN Answer A ON Q.id = A.question.id " +
            "JOIN Discipline D ON T.discipline.name = D.name " +
            "WHERE U.chatId = :userId AND D.name = :disciplineName " +
            "GROUP BY D.name, T.testName, U.chatId"
    )
    List<Object[]> getUserStatisticsByDiscipline(@Param("userId") long userId, @Param("disciplineName") String disciplineName);


}
