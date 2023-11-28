package com.example.serversdk.common.admin;

import com.example.serversdk.common.dtos.AdminDto;
import com.example.serversdk.common.dtos.UserDto;
import com.example.serversdk.common.group.Group;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/superadmin/createadmin")
    public void createAdmin(@RequestBody AdminDto adminDto, HttpServletRequest request) {
        adminService.createAdmin(adminDto, request);
    }



}
