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
    private String whatIs;
    private String whoIs;
    private String whereIs;
    private LocalTime whatTime;

    // Constructor with Record entity
    public RecordResponseDto(Record record) {
        this.id = record.getId();
        this.today = record.getToday();
        this.category = record.getCategory();
        this.whatIs = record.getWhatIs();
        this.whoIs = record.getWhoIs();
        this.whereIs = record.getWhereIs();
        this.whatTime = record.getWhatTime();
    }
}
