package com.project.aistudy.service.user;
import com.project.aistudy.dto.user.record.RecordRequestDto;
import com.project.aistudy.dto.user.record.RecordResponseDto;
import com.project.aistudy.entity.Member;
import com.project.aistudy.entity.Record;
import com.project.aistudy.repository.user.MemberRepository;
import com.project.aistudy.repository.user.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordService {

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private MemberRepository memberRepository;

    public void createRecord(Long memberId, RecordRequestDto recordRequestDto) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));

        Record record = new Record();
        record.setToday(recordRequestDto.getToday());
        record.setCategory(recordRequestDto.getCategory());
        record.setWhatIs(recordRequestDto.getWhatIs());
        record.setWhoIs(recordRequestDto.getWhoIs());
        record.setWhereIs(recordRequestDto.getWhereIs());
        record.setWhatTime(recordRequestDto.getWhatTime());
        record.setMember(member);

        recordRepository.save(record);
    }

    public List<RecordResponseDto> getRecords(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        List<Record> records = recordRepository.findByMemberAndToday(member, date);
        return records.stream()
                .map(record -> new RecordResponseDto(record))
                .collect(Collectors.toList());
    }

    public void isStretching(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        Integer howMany = member.getStretching();
        member.setStretching(howMany);
        memberRepository.save(member);
    }
}