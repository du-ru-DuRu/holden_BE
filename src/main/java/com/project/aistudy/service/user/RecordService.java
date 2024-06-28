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
    private static final List<String> ACTIVITIES = List.of("산책", "운동", "독서", "공부");

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
                    if (record.getCategory().equals("아침") || record.getCategory().equals("점심") || record.getCategory().equals("저녁")) {
                        quiz = String.format("나는 %s에 무엇을 먹었을까요?", record.getCategory());
                        List<String> options = new ArrayList<>(FOOD_LIST);
                        Collections.shuffle(options);
                        rightAnswer = record.getWhatIs();
                        if (options.contains(rightAnswer)) {
                            answer = isCorrect ? rightAnswer : options.get(0);
                        } else {
                            options.add(rightAnswer);
                            Collections.shuffle(options);
                            answer = rightAnswer;
                        }
                        option1 = options.get(0);
                        option2 = options.get(1);
                        option3 = options.get(2);
                    } else {
                        i--;
                        continue;
                    }
                    break;
                case 1:
                    if (!record.getCategory().equals("아침") && !record.getCategory().equals("점심") && !record.getCategory().equals("저녁")) {
                        quiz = String.format("나는 %s에서 무엇을 했을까요?", record.getWhereIs());
                        List<String> activities = new ArrayList<>(ACTIVITIES);
                        Collections.shuffle(activities);
                        rightAnswer = record.getWhatIs();
                        if (activities.contains(rightAnswer)) {
                            answer = isCorrect ? rightAnswer : activities.get(0);
                        } else {
                            activities.add(rightAnswer);
                            Collections.shuffle(activities);
                            answer = rightAnswer;
                        }
                        option1 = activities.get(0);
                        option2 = activities.get(1);
                        option3 = activities.get(2);
                    } else {
                        i--;
                        continue;
                    }
                    break;
                case 2:
                    quiz = String.format("나는 누구와 %s에서 활동했을까요?", record.getWhereIs());
                    List<String> people = selectedRecords.stream()
                            .map(Record::getWhoIs)
                            .distinct()
                            .collect(Collectors.toList());
                    Collections.shuffle(people);
                    rightAnswer = record.getWhoIs();
                    if (people.contains(rightAnswer)) {
                        answer = isCorrect ? rightAnswer : people.get(0);
                    } else {
                        people.add(rightAnswer);
                        Collections.shuffle(people);
                        answer = rightAnswer;
                    }
                    option1 = people.get(0);
                    option2 = people.size() > 1 ? people.get(1) : rightAnswer;
                    option3 = people.size() > 2 ? people.get(2) : rightAnswer;
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
