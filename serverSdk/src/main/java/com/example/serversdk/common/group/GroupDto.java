package com.example.serversdk.common.group;

import com.example.serversdk.common.discipline.Discipline;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupDto {
    Group group;
    List<Discipline> disciplines;
}
