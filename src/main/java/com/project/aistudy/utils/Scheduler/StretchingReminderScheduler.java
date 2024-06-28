package com.project.aistudy.utils.Scheduler;

import com.project.aistudy.entity.Member;
import com.project.aistudy.repository.user.MemberRepository;
import com.project.aistudy.service.user.FCMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StretchingReminderScheduler {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FCMService fcmService;

    @Scheduled(cron = "0 0 20 * * ?")  // 저녁 8시에 실행
    public void sendStretchingReminder() {
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            if (member.getStretching() == 0 && member.getFireBaseToken() != null) {
                fcmService.sendNotification(
                        member.getFireBaseToken(),
                        "오늘 하루의 스트레칭을 잊으셨어요!",
                        "지금 가요! https://holden-frontend.vercel.app/ 바로가기"
                );
            }
        }
    }
}

