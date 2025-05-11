package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.exception.DuplicateEmailException;
import com.todolist.exception.UnauthorizedException;
import com.todolist.auth.jwt.JwtTokenProvider;
import com.todolist.auth.jwt.dto.JwtToken;
import com.todolist.repository.MemberRepository;
import com.todolist.service.dto.request.MemberRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
class MemberServiceTest {

    private MemberService memberService;
    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        memberService = new MemberService(memberRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    @DisplayName("회원가입에 성공 시 비밀번호는 암호화된다.")
    void register() {
        // given
        String email = "test@example.com";
        String password = "password1234";
        String encodedPassword = "encoded1234";
        String nickname = "테스터";

        MemberRequest request = new MemberRequest(email, password, nickname);

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
        String nickname = "테스터";

        MemberRequest request = new MemberRequest(email, password, nickname);

        when(memberRepository.existsByEmail(email)).thenReturn(true);

        // when & then
        assertThrows(DuplicateEmailException.class, () -> memberService.register(request));
    }

    @Test
    @DisplayName("로그인 성공 시 JWT 토큰이 발급된다.")
    void login() {
        // given
        String email = "test@example.com";
        String password = "password1234";
        String nickname = "테스터";

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        MemberRequest request = new MemberRequest(email, password, nickname);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, member.getPassword())).thenReturn(true);
        when(jwtTokenProvider.createToken(any(Member.class)))
                .thenReturn(new JwtToken("accessToken", "refreshToken"));

        // when
        JwtToken token = memberService.login(request);

        // then
        assertThat(token.accessToken()).isEqualTo("accessToken");
        assertThat(token.refreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("존재하지 않는 이메일일 경우 예외가 발생한다.")
    void loginFailedDueToEmailNotFound() {
        // given
        String email = "notfound@example.com";
        String password = "password1234";
        String nickname = "테스터";

        MemberRequest request = new MemberRequest(email, password, nickname);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UnauthorizedException.class, () -> memberService.login(request));
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않을 경우 예외가 발생한다.")
    void loginFailedDueToWrongPassword() {
        // given
        String email = "test@example.com";
        String password = "password1234";
        String nickname = "테스터";

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        MemberRequest request = new MemberRequest(email, password, nickname);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, member.getPassword())).thenReturn(false);

        // when & then
        assertThrows(UnauthorizedException.class, () -> memberService.login(request));
    }
}