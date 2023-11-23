package com.example.serversdk.first.group;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    public List<Group> getGroups() {
        return groupRepository.findAll();
    }

    public void postGroup(Group group) {
        groupRepository.save(group);
    }

    public void deleteGroup(long id) {
        groupRepository.deleteById(id);
    }

    public void updateGroup(long id, Group group) {
        group.setId(id);
        groupRepository.save(group);
        log.info("Группа изменена");
    }
}
