package com.project.aistudy.utils.Scheduler;

import com.project.aistudy.entity.Member;
import com.project.aistudy.repository.user.MemberRepository;
import com.project.aistudy.service.user.FCMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MealReminderScheduler {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FCMService fcmService;
    @Scheduled(cron = "0 03,05 7 * * ?")
    @Scheduled(cron = "0 0 10 * * ?")  // 오전 10시에 실행
    @Scheduled(cron = "0 0 13 * * ?")  // 오후 1시에 실행
    @Scheduled(cron = "0 0 19 * * ?")  // 저녁 7시에 실행
    public void sendMealReminder() {
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            if (member.getFireBaseToken() != null) {
                fcmService.sendNotification(
                        member.getFireBaseToken() ,
                        "식사 맛있게 하셨나요?",
                        "하루를 기록하러가요! https://holden-frontend.vercel.app/"
                );
            }
        }
    }
}
