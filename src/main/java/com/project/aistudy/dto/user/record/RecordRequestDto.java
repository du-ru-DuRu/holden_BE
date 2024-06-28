package com.project.aistudy.dto.user.record;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RecordRequestDto {
    private LocalDate today;
    private String category;
    private String what;
    private String who;
    private String where;
    private String when;
}

