package com.project.aistudy.controller.user;

import com.project.aistudy.dto.user.record.RecordRequestDto;
import com.project.aistudy.dto.user.record.RecordResponseDto;
import com.project.aistudy.service.user.RecordService;
import com.project.aistudy.utils.baseResponse.BaseResponse;
import com.project.aistudy.utils.baseResponse.BaseResponseStatus;
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
    public ResponseEntity<BaseResponse<String>> createRecord(@RequestBody RecordRequestDto recordRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = (Long) authentication.getPrincipal(); // JWT 토큰에서 추출된 사용자 ID

        recordService.createRecord(memberId, recordRequestDto);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS, "Record created successfully"));
    }

    @GetMapping("/record")
    public ResponseEntity<BaseResponse<List<RecordResponseDto>>> getRecords(@RequestParam LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = (Long) authentication.getPrincipal(); // JWT 토큰에서 추출된 사용자 ID

        List<RecordResponseDto> records = recordService.getRecords(memberId, date);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS, records));
    }

    @PostMapping("/record/stretching")
    public ResponseEntity<BaseResponse<String>> stretchRecord(@RequestBody Boolean isStretching) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = (Long) authentication.getPrincipal(); // JWT 토큰에서 추출된 사용자 ID

        recordService.isStretching(memberId);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS, "Stretching successfully"));
    }
}
