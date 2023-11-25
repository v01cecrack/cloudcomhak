package com.example.serversdk.first.admin;

import com.example.serversdk.auth.repositories.UserRepository;
import com.example.serversdk.first.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    public List<UserDto> getUsers() {
        return userRepository.getUsers();
    }
}
