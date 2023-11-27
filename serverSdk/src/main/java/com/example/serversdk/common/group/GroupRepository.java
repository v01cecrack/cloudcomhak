package com.example.serversdk.common.group;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Scope(value = "prototype")
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAll();
    Optional <Group> findByName(String name);

}
