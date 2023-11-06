package com.example.tgbot.result;

import com.example.tgbot.statistics.Statistics;
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

    @Query(value = "SELECT D.name, T.test_name, U.chat_id, COUNT(R.id) AS total_answers, SUM(CASE WHEN R.answer = Q.correct_answer THEN 1 ELSE 0 END) AS correct_answers " +
            "FROM Users U " +
            "JOIN Results R ON U.chat_id = R.user_id " +
            "JOIN Tests T ON R.test_id = T.test_id " +
            "JOIN Questions Q ON R.question_id = Q.id " +
            "JOIN Disciplines D ON T.discipline_name = D.name " +
            "WHERE U.chat_id = :userId AND D.name = :disciplineName " +
            "GROUP BY D.name, T.test_name, U.chat_id", nativeQuery = true)
    List<Object[]> getUserStatisticsByDiscipline(@Param("userId") long userId, @Param("disciplineName") String disciplineName);


}
