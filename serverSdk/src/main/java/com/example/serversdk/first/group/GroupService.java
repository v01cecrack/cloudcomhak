package com.example.serversdk.first.group;

import com.example.serversdk.first.discipline.Discipline;
import com.example.serversdk.first.discipline.DisciplineRepository;
import com.example.serversdk.first.disciplinegroup.DisciplineGroup;
import com.example.serversdk.first.disciplinegroup.DisciplineGroupRepository;
import com.example.serversdk.first.user.StudentDto;
import com.example.serversdk.first.user.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final DisciplineRepository disciplineRepository;
    private final DisciplineGroupRepository disciplineGroupRepository;

    public List<Group> getGroups() {
        return groupRepository.findAll();
    }

    @Transactional
    public void postGroup(GroupDto groupDto) {
        log.info("Добавление группы {}", groupDto.getGroup().getName());
        groupRepository.save(groupDto.getGroup());
        List<Discipline> disciplines = groupDto.getDisciplines();
        if (disciplines.isEmpty()) {
            disciplineRepository.saveAll(disciplines);
        }
        List<DisciplineGroup> disciplineGroups = disciplines.stream().map(discipline -> DisciplineGroup.builder()
                .group(groupDto.getGroup()).discipline(discipline).build()).collect(Collectors.toList());

        disciplineGroupRepository.saveAll(disciplineGroups);
    }

    public void deleteGroup(long id) {
        groupRepository.deleteById(id);
    }

    public void updateGroup(long id, Group group) {
        group.setId(id);
        groupRepository.save(group);
        log.info("Группа изменена");
    }

    public List<StudentDto> getStudentsOfGroup(String groupName) {
        return studentRepository.findStudentsByGroupName(groupName);
    }

    public void deleteStudent(Long chatId) {
        studentRepository.deleteStudentByChatId(chatId);
    }

}
