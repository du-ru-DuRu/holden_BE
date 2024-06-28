package com.project.aistudy.dto.user.record;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Data
public class RecordRequestDto {
    private LocalDate today;
    private String category;
    private String whatIs;
    private String whoIs;
    private String whereIs;
    private LocalTime whatTime;
}

