package com.example.serversdk.first.test;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Scope(value = "prototype")
public interface TestRepository extends JpaRepository<Test, Long> {
    Test findByTestName(String testName);

    List<Test> findTestsByDisciplineName(@Param("disciplineName") String disciplineName);

    List<Test> findAllByDiscipline_Id(Long id);


}
