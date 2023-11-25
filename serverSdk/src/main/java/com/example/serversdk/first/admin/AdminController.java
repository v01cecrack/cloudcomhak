package com.example.serversdk.first.admin;

import com.example.serversdk.auth.entities.User;
import com.example.serversdk.first.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping()
    public List<UserDto> getUsers() {
        return adminService.getUsers();
    }
}
