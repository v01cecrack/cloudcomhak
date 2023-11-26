package com.example.serversdk.first.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDto {
    private Long chatId;
    private String surname;
    private String name;
    private String fatherName;
    private String testName;
    private Long correctAnswers;
    private Long totalAnswers;
}
