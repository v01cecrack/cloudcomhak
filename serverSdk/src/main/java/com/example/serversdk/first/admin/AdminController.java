package com.example.serversdk.first.admin;

import com.example.serversdk.auth.entities.User;
import com.example.serversdk.first.dtos.AdminDto;
import com.example.serversdk.first.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/admin")
    public List<UserDto> getUsers() {
        return adminService.getUsers();
    }

    @PatchMapping("/admin/teacher/{id}")
    public void setTeacher(@PathVariable long id) {
        adminService.setTeacher(id);
    }


    @DeleteMapping("/admin/{id}")
    public void deleteUser(@PathVariable long id) {
        adminService.deleteUser(id);
    }

    @PostMapping("/superadmin/createadmin")
    public void createAdmin(AdminDto adminDto) {
        adminService.createAdmin(adminDto);
    }



}
