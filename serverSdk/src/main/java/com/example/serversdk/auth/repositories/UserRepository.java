package com.example.serversdk.auth.repositories;


import com.example.serversdk.auth.entities.User;
import com.example.serversdk.first.dtos.UserDto;
import com.example.serversdk.first.group.Group;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String username);

    void deleteByGroups_Id(Long groupId);

    void deleteUserById(Long id);

    @Query("SELECT NEW com.example.serversdk.first.dtos.UserDto(U.id, U.fullname, U.description, U.email, U.role) from User U ")
    List<UserDto> getUsers();

    @Query("SELECT DISTINCT u.groups FROM User u WHERE u.id = :userId")
    List<Group> findGroupsByUserId(@Param("userId") Long userId);


}
