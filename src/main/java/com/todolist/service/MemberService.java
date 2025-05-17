package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.exception.DuplicateEmailException;
import com.todolist.exception.UnauthorizedException;
import com.todolist.auth.jwt.JwtTokenProvider;
import com.todolist.auth.jwt.dto.JwtToken;
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
    private final JwtTokenProvider jwtTokenProvider;

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
                .nickname(request.nickname())
                .build();
        memberRepository.save(member);

        return RegisterResponse.from(member);
    }

    @Transactional
    public JwtToken login(MemberRequest request) {
        log.info("로그인 요청: email = {}", request.email());
        // 가입된 이메일이 없을 경우 로그인 X
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("존재하지 않은 아이디입니다."));

        // 비밀번호가 일치하지 않을 경우 로그인 X
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        log.info("로그인 성공: member_id = {}", member.getId());
        return jwtTokenProvider.createToken(member);
    }
}
