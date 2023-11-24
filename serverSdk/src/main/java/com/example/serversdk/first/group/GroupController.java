package com.example.serversdk.first.group;

import com.example.serversdk.first.user.StudentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @GetMapping
    public List<Group> getGroups() {
        return groupService.getGroups();
    }

    @PostMapping
    public void postGroup(@RequestBody Group group) {
        groupService.postGroup(group);
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable long id) {
        groupService.deleteGroup(id);
    }

    @PatchMapping("/{id}")
    public void updateGroup(@PathVariable long id, @RequestBody Group group) {
        groupService.updateGroup(id, group);
    }

    @GetMapping("/{groupName}")
    public List<StudentDto> getStudentsOfGroup(@PathVariable String groupName) {
        return groupService.getStudentsOfGroup(groupName);
    }

    @DeleteMapping("/{chatId}")
    public void deleteStudent(@PathVariable long chatId) {
        groupService.deleteStudent(chatId);
    }

}
