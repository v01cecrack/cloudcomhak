package com.example.serversdk.first.admin;

import com.example.serversdk.auth.entities.User;
import com.example.serversdk.first.dtos.AdminDto;
import com.example.serversdk.first.dtos.UserDto;
import com.example.serversdk.first.group.Group;
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
    public void setTeacher(@PathVariable long id,@RequestBody List<Group> groups) {
        adminService.setTeacher(id, groups);
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
