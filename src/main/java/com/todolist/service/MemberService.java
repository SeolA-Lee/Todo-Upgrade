package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.exception.DuplicateEmailException;
import com.todolist.repository.MemberRepository;
import com.todolist.service.dto.request.MemberRequest;
import com.todolist.service.dto.response.RegisterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse register(MemberRequest request) {
        // 가입하려는 이메일이 존재할 경우 가입 X
        if (memberRepository.existsByEmail(request.email())) {
            log.warn("가입하려는 이메일({})이 이미 존재함", request.email());
            throw new DuplicateEmailException();
        }

        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        memberRepository.save(member);

        return RegisterResponse.from(member);
    }
}
