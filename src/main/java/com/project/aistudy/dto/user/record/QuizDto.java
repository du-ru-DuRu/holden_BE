package com.project.aistudy.dto.user.record;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizDto {
    private String quiz;
    private String answer;
}