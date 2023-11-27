package com.example.serversdk.common.discipline;

import com.example.serversdk.common.group.Group;
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
