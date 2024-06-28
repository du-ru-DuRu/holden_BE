package com.project.aistudy.dto.user.record;

import com.project.aistudy.entity.Record;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Data
public class RecordResponseDto {
    private Long id;
    private LocalDate today;
    private String category;
    private String what;
    private String who;
    private String where;
    private LocalTime when;

    // Constructor with Record entity
    public RecordResponseDto(Record record) {
        this.id = record.getId();
        this.today = record.getToday();
        this.category = record.getCategory();
        this.what = record.getWhat();
        this.who = record.getWho();
        this.where = record.getWhere();
        this.when = record.getWhen();
    }
}
