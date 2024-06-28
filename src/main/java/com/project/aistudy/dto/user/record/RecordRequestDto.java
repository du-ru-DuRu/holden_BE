package com.project.aistudy.dto.user.record;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Data
public class RecordRequestDto {
    private LocalDate today;
    private String category;
    private String what;
    private String who;
    private String where;
    private LocalTime when;
}

