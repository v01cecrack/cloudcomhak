package com.example.serversdk.auth.repositories;


import com.example.serversdk.auth.entities.User;
import com.example.serversdk.first.dtos.UserDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    void deleteByGroups_Id(Long groupId);

    @Query("SELECT NEW com.example.serversdk.first.dtos.UserDto(U.username, U.email, U.role) from User U ")
    List<UserDto> getUsers();
}
