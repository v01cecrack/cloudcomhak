package com.example.serversdk.common.admin;

import com.example.serversdk.auth.entities.Roles;
import com.example.serversdk.auth.entities.User;
import com.example.serversdk.auth.repositories.UserRepository;
import com.example.serversdk.common.dtos.AdminDto;
import com.example.serversdk.common.dtos.UserDto;
import com.example.serversdk.common.exception.ConflictException;
import com.example.serversdk.common.exception.ForbiddenException;
import com.example.serversdk.common.group.Group;
import com.example.serversdk.common.result.ResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final ResultService resultService;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getUsers() {
        return userRepository.getUsers();
    }

    @Transactional
    public void setTeacher(long id, List<Group> groups) {
        User user = userRepository.findById(id).orElseThrow(RuntimeException::new);
        user.setRole(Roles.ROLE_TEACHER);
        user.setGroups(groups);
        userRepository.save(user);
    }

    public void deleteUser(long id) {
        userRepository.deleteUserById(id);
    }

    public void createAdmin(AdminDto adminDto, HttpServletRequest request) {
        User user = resultService.auth(request);
        if (!user.getRole().equals(Roles.ROLE_SUPERADMIN)) {
            log.warn("Нельзя создавать админа");
            throw new ForbiddenException("Нельзя создавать админа");
        }
        if (userRepository.findByEmail(adminDto.getEmail()).isPresent()) {
            log.warn("Email уже занят");
            throw new ConflictException("Email уже занят");
        }
        User admin = new User();
        admin.setEmail(adminDto.getEmail());
        admin.setPassword(passwordEncoder.encode(adminDto.getPassword()));
        admin.setRole(Roles.ROLE_ADMIN);
        userRepository.save(admin);
        log.info("Создан админ {}", adminDto.getEmail());
    }
}
