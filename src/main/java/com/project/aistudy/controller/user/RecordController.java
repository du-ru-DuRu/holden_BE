package com.project.aistudy.controller.user;

import com.project.aistudy.dto.user.record.RecordRequestDto;
import com.project.aistudy.dto.user.record.RecordResponseDto;
import com.project.aistudy.service.user.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class RecordController {

    @Autowired
    private RecordService recordService;

    @PostMapping("/record")
    public ResponseEntity<String> createRecord(@RequestBody RecordRequestDto recordRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName(); // JWT 토큰에서 추출된 사용자 ID

        recordService.createRecord(Long.parseLong(memberId), recordRequestDto);
        return ResponseEntity.ok("Record created successfully");
    }

    @GetMapping("/record")
    public ResponseEntity<List<RecordResponseDto>> getRecords(@RequestParam LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName(); // JWT 토큰에서 추출된 사용자 ID

        List<RecordResponseDto> records = recordService.getRecords(Long.parseLong(memberId), date);
        return ResponseEntity.ok(records);
    }

    @PostMapping("/record/stretching")
    public ResponseEntity<String> stretchRecord(@RequestBody Boolean isStretching) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName();
        recordService.isStretching(memberId);
        return ResponseEntity.ok("Stretching successfully");
    }
}
