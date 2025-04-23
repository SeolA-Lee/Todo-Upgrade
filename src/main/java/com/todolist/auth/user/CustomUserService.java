package com.todolist.auth.user;

import com.todolist.entity.Member;
import com.todolist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserService {

    private final MemberRepository memberRepository;

    public CustomUserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유효하지 않은 사용자입니다: userId = " + userId));
        return new CustomUserDetails(member);
    }
}
