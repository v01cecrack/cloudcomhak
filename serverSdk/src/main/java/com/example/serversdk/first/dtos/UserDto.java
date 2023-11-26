package com.example.serversdk.first.dtos;

import com.example.serversdk.auth.entities.Roles;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    String fullname;
    String description;
    String email;
    Roles role;
}
