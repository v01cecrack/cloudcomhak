package com.example.serversdk.first.discipline;

import com.example.serversdk.first.discipline.Discipline;
import com.example.serversdk.first.group.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
@Builder
public class DisciplineDto {
    Discipline discipline;
    List<Group> groups;
}
