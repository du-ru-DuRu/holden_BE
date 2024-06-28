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
    private String whatIs;

    @Column
    private String whoIs;

    @Column
    private String whereIs;

    @Column
    private LocalTime whatTime;


    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
