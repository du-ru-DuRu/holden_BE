package com.project.aistudy.service.user;
import com.project.aistudy.dto.user.record.QuizDto;
import com.project.aistudy.dto.user.record.RecordRequestDto;
import com.project.aistudy.dto.user.record.RecordResponseDto;
import com.project.aistudy.entity.Member;
import com.project.aistudy.entity.Record;
import com.project.aistudy.repository.user.MemberRepository;
import com.project.aistudy.repository.user.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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

    public List<QuizDto> generateQuiz(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        List<Record> records = recordRepository.findByMember(member);

        if (records.size() < 3) {
            throw new RuntimeException("Not enough records to generate quiz");
        }

        Collections.shuffle(records);
        List<Record> selectedRecords = records.subList(0, 3);

        List<QuizDto> quizzes = new ArrayList<>();
        Random random = new Random();

        // 적어도 하나의 정답이 포함되도록 보장
        int correctAnswerIndex = random.nextInt(3);

        for (int i = 0; i < selectedRecords.size(); i++) {
            Record record = selectedRecords.get(i);
            boolean isCorrect = (i == correctAnswerIndex);
            String quiz;
            String correctActivity = getActivityQuestion(record);

            if (isCorrect) {
                quiz = correctActivity;
                quizzes.add(new QuizDto(quiz, "O"));
            } else {
                // 필드를 섞어서 잘못된 질문 생성
                Record randomRecord = selectedRecords.get(random.nextInt(selectedRecords.size()));
                String randomWhoIs = randomRecord.getWhoIs();
                String randomWhereIs = randomRecord.getWhereIs();
                String randomWhatIs = randomRecord.getWhatIs();
                String randomCategory = randomRecord.getCategory();
                String incorrectActivity;

                if (record.getCategory().equals("아침") || record.getCategory().equals("점심") || record.getCategory().equals("저녁")) {
                    incorrectActivity = String.format("나는 %s와 %s에서 %s를 %s에 먹었다.",
                            randomWhoIs.equals(record.getWhoIs()) ? "다른 사람" : record.getWhoIs(),
                            randomWhereIs.equals(record.getWhereIs()) ? "다른 장소" : record.getWhereIs(),
                            randomWhatIs.equals(record.getWhatIs()) ? "다른 음식" : record.getWhatIs(),
                            randomCategory.equals(record.getCategory()) ? "다른 시간" : record.getCategory());
                } else {
                    incorrectActivity = String.format("나는 %s와 %s에서 %s를 했다.",
                            randomWhoIs.equals(record.getWhoIs()) ? "다른 사람" : record.getWhoIs(),
                            randomWhereIs.equals(record.getWhereIs()) ? "다른 장소" : record.getWhereIs(),
                            randomWhatIs.equals(record.getWhatIs()) ? "다른 활동" : record.getWhatIs());
                }
                quizzes.add(new QuizDto(incorrectActivity, "X"));
            }
        }
        return quizzes;
    }

    private String getActivityQuestion(Record record) {
        String activity;
        switch (record.getCategory()) {
            case "아침":
            case "점심":
            case "저녁":
                activity = String.format("나는 %s와 %s에서 %s를 %s에 먹었다.",
                        record.getWhoIs(),
                        record.getWhereIs(),
                        record.getWhatIs(),
                        record.getCategory());
                break;
            case "기타 활동":
                activity = String.format("나는 %s와 %s에서 %s를 했다.",
                        record.getWhoIs(),
                        record.getWhereIs(),
                        record.getWhatIs());
                break;
            default:
                activity = String.format("나는 %s와 %s에서 %s를 %s에 했다.",
                        record.getWhoIs(),
                        record.getWhereIs(),
                        record.getWhatIs(),
                        record.getCategory());
                break;
        }
        return activity;
    }
}
