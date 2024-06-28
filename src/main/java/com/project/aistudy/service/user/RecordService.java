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

    private static final List<String> FOOD_LIST = List.of("제육볶음", "계란볶음밥", "족발", "비빔밥", "김치찌개");

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
            String option1 = "";
            String option2 = "";
            String option3 = "";
            String answer = "";
            String rightAnswer = "";

            switch (random.nextInt(3)) {
                case 0:
                    // 무엇을 먹었는지 묻는 문제
                    if (record.getCategory().equals("아침") || record.getCategory().equals("점심") || record.getCategory().equals("저녁")) {
                        quiz = String.format("나는 %s에 무엇을 먹었을까요?", record.getCategory());
                        List<String> options = new ArrayList<>(FOOD_LIST);
                        Collections.shuffle(options);
                        option1 = options.get(0);
                        option2 = options.get(1);
                        option3 = options.get(2);
                        rightAnswer = record.getWhatIs();
                        if (isCorrect || (!options.contains(rightAnswer))) {
                            answer = rightAnswer;
                        } else {
                            answer = options.get(3);
                        }
                    } else {
                        i--; // 카테고리가 아침, 점심, 저녁이 아니면 다시 시도
                        continue;
                    }
                    break;
                case 1:
                    // 기타 활동이면 어디서 혹은 무엇을 했는지 묻는 문제
                    if (!record.getCategory().equals("아침") && !record.getCategory().equals("점심") && !record.getCategory().equals("저녁")) {
                        quiz = String.format("나는 %s에서 무엇을 했을까요?", record.getWhereIs());
                        List<String> activities = new ArrayList<>(List.of("산책", "운동", "독서", "공부"));
                        Collections.shuffle(activities);
                        option1 = activities.get(0);
                        option2 = activities.get(1);
                        option3 = activities.get(2);
                        rightAnswer = record.getWhatIs();
                        if (isCorrect || (!activities.contains(rightAnswer))) {
                            answer = rightAnswer;
                        } else {
                            answer = activities.get(3);
                        }
                    } else {
                        i--; // 기타 활동이 아니면 다시 시도
                        continue;
                    }
                    break;
                case 2:
                    // 누구랑 했는지 묻는 문제
                    quiz = String.format("나는 누구와 %s에서 활동했을까요?", record.getWhereIs());
                    List<String> people = selectedRecords.stream()
                            .map(Record::getWhoIs)
                            .distinct()
                            .collect(Collectors.toList());
                    Collections.shuffle(people);
                    option1 = people.get(0);
                    option2 = people.get(1);
                    option3 = people.get(2);
                    rightAnswer = record.getWhoIs();
                    if (isCorrect || (!people.contains(rightAnswer))) {
                        answer = rightAnswer;
                    } else {
                        answer = people.get(3);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + random.nextInt(3));
            }

            quizzes.add(new QuizDto(quiz, option1, option2, option3, answer, rightAnswer));
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
