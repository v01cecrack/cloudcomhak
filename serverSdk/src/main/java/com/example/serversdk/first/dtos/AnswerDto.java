package com.example.serversdk.first.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDto {
    Long id;
    String answer;
    Long questionId;
    Boolean correct;
}
