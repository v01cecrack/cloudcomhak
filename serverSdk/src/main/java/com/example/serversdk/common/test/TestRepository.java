package com.example.serversdk.common.test;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Scope(value = "prototype")
public interface TestRepository extends JpaRepository<Test, Long> {

    List<Test> findTestsByDisciplineName(@Param("disciplineName") String disciplineName);

    List<Test> findAllByDiscipline_Id(Long id);

    Optional<Test> findByTestName(String testName);


}
