package com.example.serversdk.common.group;

import com.example.serversdk.auth.repositories.UserRepository;
import com.example.serversdk.common.discipline.Discipline;
import com.example.serversdk.common.discipline.DisciplineRepository;
import com.example.serversdk.common.disciplinegroup.DisciplineGroup;
import com.example.serversdk.common.disciplinegroup.DisciplineGroupRepository;
import com.example.serversdk.common.exception.ConflictException;
import com.example.serversdk.common.user.Student;
import com.example.serversdk.common.user.StudentDto;
import com.example.serversdk.common.user.StudentRepository;
import com.example.serversdk.common.result.ResultRepository;
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
    private final UserRepository userRepository;
    private final ResultRepository resultRepository;

    public List<Group> getGroups() {
        return groupRepository.findAll();
    }

    @Transactional
    public void postGroup(GroupDto groupDto) {
        log.info("Добавление группы {}", groupDto.getGroup().getName());
        if (groupRepository.findByName(groupDto.getGroup().getName()).isPresent()) {
            throw new ConflictException("Группа с таким названием уже существует");
        }
        groupRepository.save(groupDto.getGroup());
        List<Discipline> disciplines = groupDto.getDisciplines();

        List<DisciplineGroup> disciplineGroups = disciplines.stream().map(discipline -> DisciplineGroup.builder()
                .group(groupDto.getGroup()).discipline(discipline).build()).collect(Collectors.toList());

        disciplineGroupRepository.saveAll(disciplineGroups);
    }

    @Transactional
    public void deleteGroup(long id) {
        userRepository.deleteByGroups_Id(id);
        List<Student> students = studentRepository.findAllByGroup_Id(id);
        resultRepository.deleteAllByUserIn(students);
        studentRepository.deleteAllByGroup_Id(id);
        disciplineGroupRepository.deleteAllByGroup_Id(id);
        groupRepository.deleteById(id);
    }

    public void updateGroup(long id, Group group) {
        Group oldGroup = groupRepository.findById(id).get();
        oldGroup.setName(group.getName());
        groupRepository.save(oldGroup);
        log.info("Группа изменена");
    }

    public List<StudentDto> getStudentsOfGroup(String groupName) {
        return studentRepository.findStudentsByGroupName(groupName);
    }

    public void deleteStudent(Long chatId) {
        studentRepository.deleteStudentByChatId(chatId);
    }

}
