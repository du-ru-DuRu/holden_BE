package com.project.aistudy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate today;

    @Column(nullable = false)
    private String category;

    @Column
    private String what;

    @Column
    private String who;

    @Column
    private String where;

    @Column(nullable = false)
    private LocalTime when;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
