package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.exception.DuplicateEmailException;
import com.todolist.repository.MemberRepository;
import com.todolist.service.dto.request.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
class MemberServiceTest {

    private MemberService memberService;
    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        memberService = new MemberService(memberRepository, passwordEncoder);
    }

    @Test
    @DisplayName("회원가입에 성공 시 비밀번호는 암호화된다.")
    void register() {
        // given
        String email = "test@example.com";
        String password = "password1234";
        String encodedPassword = "encoded1234";

        RegisterRequest request = new RegisterRequest(email, password);

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        // when
        memberService.register(request);

        // then
        verify(memberRepository, times(1)).existsByEmail(email);
        verify(passwordEncoder, times(1)).encode(password);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("이미 존재하는 아이디면 가입에 실패한다.")
    void registerFailedDueToDuplicateEmail() {
        // given
        String email = "test@example.com";
        String password = "password1234";

        RegisterRequest request = new RegisterRequest(email, password);

        when(memberRepository.existsByEmail(email)).thenReturn(true);

        // when & then
        assertThrows(DuplicateEmailException.class, () -> memberService.register(request));
    }
}