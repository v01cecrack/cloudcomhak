package com.example.serversdk.first.group;

import com.example.serversdk.first.discipline.Discipline;
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
