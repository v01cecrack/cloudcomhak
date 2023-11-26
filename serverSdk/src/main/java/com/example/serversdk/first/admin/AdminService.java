package com.example.serversdk.first.admin;

import com.example.serversdk.auth.entities.Roles;
import com.example.serversdk.auth.entities.User;
import com.example.serversdk.auth.repositories.UserRepository;
import com.example.serversdk.first.dtos.AdminDto;
import com.example.serversdk.first.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getUsers() {
        return userRepository.getUsers();
    }

    @Transactional
    public void setTeacher(long id) {
        User user = userRepository.findById(id).orElseThrow(RuntimeException::new);
        user.setRole(Roles.ROLE_TEACHER);
    }

    public void deleteUser(long id) {
        userRepository.deleteUserById(id);
    }

    public void createAdmin(AdminDto adminDto) {
        User admin = new User();
        admin.setEmail(adminDto.getEmail());
        admin.setPassword(passwordEncoder.encode(adminDto.getPassword()));
        admin.setRole(Roles.ROLE_ADMIN);
        userRepository.save(admin);
    }
}
