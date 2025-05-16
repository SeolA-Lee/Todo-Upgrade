package com.todolist.service;

import com.todolist.entity.Follow;
import com.todolist.entity.Member;
import com.todolist.exception.BadRequestException;
import com.todolist.exception.ConflictException;
import com.todolist.repository.FollowRepository;
import com.todolist.repository.MemberRepository;
import com.todolist.repository.TodoRepository;
import com.todolist.service.dto.response.FollowingListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
     * 팔로잉 목록 조회
     */
    @Test
    @DisplayName("팔로우 하는 사람들을 리스트로 조회할 수 있다.")
    void getFollowings() {
        // given
        Member me = buildMember("me@example.com", "me", 1L);
        Member followee1 = buildMember("followee1@example.com", "followee1", 2L);
        Member followee2 = buildMember("followee2@example.com", "followee2", 3L);

        int pageNum = 0;
        Pageable pageable = PageRequest.of(pageNum, 10);

        Page<Member> followingPage = new PageImpl<>(List.of(followee1, followee2), pageable, 2);

        when(followRepository.findFolloweeByFollower(me, pageable)).thenReturn(followingPage);

        // when
        FollowingListResponse response = followService.getFollowings(me, pageNum);

        // then
        verify(followRepository, times(1)).findFolloweeByFollower(me, pageable);

        assertThat(response.followings()).hasSize(2);
        assertThat(response.isLast()).isTrue();
    }

    /**
     * 팔로우 테스트
     */
    @Test
    @DisplayName("다른 사람을 팔로우할 수 있다.")
    void follow() {
        // given
        Member me = buildMember("me@example.com", "me", 1L);
        Member other = buildMember("other@example.com", "other", 2L);

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
        Member me = buildMember("me@example.com", "me", 1L);

        // when & then
        assertThrows(BadRequestException.class, () -> followService.follow(me, me.getId()));
    }

    @Test
    @DisplayName("이미 팔로우한 경우 예외가 발생한다.")
    void followFailedDueToConflict() {
        // given
        Member me = buildMember("me@example.com", "me", 1L);
        Member other = buildMember("other@example.com", "other", 2L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(me));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(other));
        when(followRepository.existsByFollowerAndFollowee(me, other)).thenReturn(true);

        // when & then
        assertThrows(ConflictException.class, () -> followService.follow(me, 2L));
    }

    /**
     * 언팔로우 테스트
     */
    @Test
    @DisplayName("다른 사람을 언팔로우할 수 있다.")
    void unfollow() {
        // given
        Member me = buildMember("me@example.com", "me", 1L);
        Member other = buildMember("other@example.com", "other", 2L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(me));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(other));
        when(followRepository.existsByFollowerAndFollowee(me, other)).thenReturn(true);

        // when
        followService.unfollow(me, 2L);

        // then
        verify(followRepository, times(1)).deleteByFollowerAndFollowee(me, other);
    }

    @Test
    @DisplayName("팔로우하지 않은 사람을 언팔로우할 경우 예외가 발생한다.")
    void unfollowFailedDueToBadRequest() {
        // given
        Member me = buildMember("me@example.com", "me", 1L);
        Member other = buildMember("other@example.com", "other", 2L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(me));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(other));
        when(followRepository.existsByFollowerAndFollowee(me, other)).thenReturn(false);

        // when & then
        assertThrows(BadRequestException.class, () -> followService.unfollow(me, 2L));
    }

    /**
     * 편의 메소드
     */
    private Member buildMember(String email, String nickname, Long id) {
        Member member = Member.builder()
                .email(email)
                .password("password1234")
                .nickname(nickname)
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }
}