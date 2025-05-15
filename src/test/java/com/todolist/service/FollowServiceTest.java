package com.todolist.service;

import com.todolist.entity.Follow;
import com.todolist.entity.Member;
import com.todolist.exception.BadRequestException;
import com.todolist.exception.ConflictException;
import com.todolist.repository.FollowRepository;
import com.todolist.repository.MemberRepository;
import com.todolist.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
class FollowServiceTest {

    private FollowService followService;
    private FollowRepository followRepository;
    private MemberRepository memberRepository;
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        followRepository = mock(FollowRepository.class);
        memberRepository = mock(MemberRepository.class);
        todoRepository = mock(TodoRepository.class);
        followService = new FollowService(followRepository, memberRepository, todoRepository);
    }

    /**
     * 팔로우 테스트
     */
    @Test
    @DisplayName("다른 사람을 팔로우할 수 있다.")
    void follow() {
        // given
        Member me = buildMember("me@example.com", "me");
        Member other = buildMember("other@example.com", "other");
        ReflectionTestUtils.setField(me, "id", 1L);
        ReflectionTestUtils.setField(other, "id", 2L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(me));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(other));
        when(followRepository.existsByFollowerAndFollowee(me, other)).thenReturn(false);

        // when
        followService.follow(me, 2L);

        // then
        verify(followRepository, times(1)).save(any(Follow.class));
    }

    @Test
    @DisplayName("자기 자신은 팔로우할 수 없다.")
    void followFailedDueToBadRequest() {
        // given
        Member me = buildMember("me@example.com", "me");
        ReflectionTestUtils.setField(me, "id", 1L);

        // when & then
        assertThrows(BadRequestException.class, () -> followService.follow(me, me.getId()));
    }

    @Test
    @DisplayName("이미 팔로우한 경우 예외가 발생한다.")
    void followFailedDueToConflict() {
        // given
        Member me = buildMember("me@example.com", "me");
        Member other = buildMember("other@example.com", "other");
        ReflectionTestUtils.setField(me, "id", 1L);
        ReflectionTestUtils.setField(other, "id", 2L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(me));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(other));
        when(followRepository.existsByFollowerAndFollowee(me, other)).thenReturn(true);

        // when & then
        assertThrows(ConflictException.class, () -> followService.follow(me, 2L));
    }

    /**
     * 편의 메소드
     */
    private Member buildMember(String email, String nickname) {
        return Member.builder()
                .email(email)
                .password("password1234")
                .nickname(nickname)
                .build();
    }
}