package com.project.aistudy.utils.Scheduler;

import com.project.aistudy.entity.Member;
import com.project.aistudy.repository.user.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StretchingResetScheduler {

    @Autowired
    private MemberRepository memberRepository;

    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
    public void resetStretchingCount() {
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            member.setStretching(0);
            memberRepository.save(member);
        }
    }
}