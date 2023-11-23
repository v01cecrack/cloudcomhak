package com.example.serversdk.first.group;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
@Scope(value = "prototype")
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAll();
    List<Group> findAllByName(String name);
    Group findByName(String name);
}
