package com.example.serversdk.result;

import com.example.serversdk.auth.entities.Roles;
import com.example.serversdk.auth.entities.User;
import com.example.serversdk.auth.repositories.UserRepository;
import com.example.serversdk.auth.utils.JwtTokenUtils;
import com.example.serversdk.first.discipline.Discipline;
import com.example.serversdk.first.discipline.DisciplineRepository;
import com.example.serversdk.first.disciplinegroup.DisciplineGroup;
import com.example.serversdk.first.disciplinegroup.DisciplineGroupRepository;
import com.example.serversdk.first.dtos.ResultDto;
import com.example.serversdk.first.dtos.ResultStatsDto;
import com.example.serversdk.first.group.Group;
import com.example.serversdk.first.group.GroupRepository;
import com.example.serversdk.first.test.TestRepository;
import com.example.serversdk.first.user.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserRepository userRepository;
    private final DisciplineGroupRepository disciplineGroupRepository;
    private final StudentRepository studentRepository;
    private final TestRepository testRepository;
    private final ResultRepository resultRepository;
    private final GroupRepository groupRepository;
    private final DisciplineRepository disciplineRepository;

    public ResponseEntity<?> getDisciplines(HttpServletRequest request) {
        User user = auth(request);
        if (user.getRole().equals(Roles.ROLE_ADMIN) || user.getRole().equals(Roles.ROLE_SUPERADMIN)) {
            List<Discipline> disciplines = disciplineRepository.findAll();
            return ResponseEntity.ok(disciplines);
        }
        List<Group> groups = userRepository.findGroupsByUserId(user.getId());
        if (!groups.isEmpty()) {
            List<DisciplineGroup> disciplineGroups = disciplineGroupRepository.findDisciplineGroupsByGroupIn(groups);
            List<Discipline> disciplines = disciplineGroups.stream().map(DisciplineGroup::getDiscipline).collect(Collectors.toList());
            return ResponseEntity.ok(disciplines);
        } else {
            String message = "Нет доступных дисциплин";
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        }

    }


    public ResponseEntity<?> getGroups(HttpServletRequest request, String disciplineName) {
        User user = auth(request);
        if (user.getRole().equals(Roles.ROLE_ADMIN) || user.getRole().equals(Roles.ROLE_SUPERADMIN)) {
            List<Group> groups = groupRepository.findAll();
            return ResponseEntity.ok(groups);
        }
//        List<Group> groups = userRepository.findGroupsByUserId(user.getId());
        List<Group> groups = disciplineGroupRepository.findDisciplineGroupsByDiscipline_Name(disciplineName)
                .stream().map(DisciplineGroup::getGroup).collect(Collectors.toList());
        return ResponseEntity.ok(groups);
    }

    public ResponseEntity<?> getStats(String groupName, String disciplineName, HttpServletRequest request) {
        User user = auth(request);
        Long groupId = groupRepository.findByName(groupName).getId();
        Long disciplineId = disciplineRepository.findByName(disciplineName).getId();
        List<ResultDto> resultDtos = resultRepository.getGroupDisciplineResults(groupId, disciplineId);
        List<ResultStatsDto> results = new ArrayList<>();
        for (ResultDto resultDto : resultDtos) {
            Double percentCorrectOfTest = (resultDto.getCorrectAnswers() * 100.0) / resultDto.getTotalAnswers();
            results.add(ResultStatsDto.builder()
                    .surname(resultDto.getSurname())
                    .name(resultDto.getName())
                    .fatherName(resultDto.getFatherName())
                    .testName(resultDto.getTestName())
                    .percentageCorrect(percentCorrectOfTest)
                    .build());
        }
        return ResponseEntity.ok(results);

    }

    private User auth(HttpServletRequest request) {
        String username = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwt")) {
                    String jwt = cookie.getValue();
                    username = jwtTokenUtils.getUsername(jwt);
                }
            }
        }
        return userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("У вас нет доступа;("));
    }
}
