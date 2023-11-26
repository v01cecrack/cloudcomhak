package com.example.serversdk.first.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultStatsDto {
    private Long chatId;
    private String surname;
    private String name;
    private String fatherName;
    private String testName;
    private Double percentageCorrect;
}
