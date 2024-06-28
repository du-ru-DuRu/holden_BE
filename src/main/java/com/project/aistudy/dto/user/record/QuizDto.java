package com.project.aistudy.dto.user.record;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizDto {
    private String quiz;
    private String option1;
    private String option2;
    private String option3;
    private String answer;
    private String rightAnswer;
}