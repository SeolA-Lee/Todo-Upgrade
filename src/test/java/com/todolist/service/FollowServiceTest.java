package com.todolist.service;

import com.todolist.entity.Follow;
import com.todolist.entity.Member;
import com.todolist.entity.Todo;
import com.todolist.entity.TodoDetail;
import com.todolist.entity.enums.TodoDetailStatus;
import com.todolist.entity.enums.TodoStatus;
import com.todolist.exception.BadRequestException;
import com.todolist.exception.ConflictException;
import com.todolist.repository.FollowRepository;
import com.todolist.repository.MemberRepository;
import com.todolist.repository.TodoRepository;
import com.todolist.service.dto.response.FollowingListResponse;
import com.todolist.service.dto.response.TodoListResponse;
import com.todolist.service.dto.response.TodoWithDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
     * 팔로우한 사람의 투두 조회 테스트
     */
    @Test
    @DisplayName("팔로우 하는 사람을 누르면 해당 유저의 세부 할 일과 함께 투두 리스트를 조회할 수 있다.")
    void readFolloweeTodoList() {
        // given
        Member me = buildMember("me@example.com", "me", 1L);
        Member followee = buildMember("followee@example.com", "followee", 2L);

        List<Todo> followeeTodoList = new ArrayList<>();

        Todo todo1 = buildTodo(followee, "할 일 1");
        TodoDetail todo1Detail = buildTodoDetail(todo1, "1 - 세부 할 일");
        todo1.addTodoDetail(todo1Detail);
        followeeTodoList.add(todo1);

        Todo todo2 = buildTodo(followee, "할 일 2");
        followeeTodoList.add(todo2);

        int pageNum = 0;
        Pageable pageable = PageRequest.of(pageNum, 10);

        Page<Todo> followeeTodoPage = new PageImpl<>(followeeTodoList, pageable, 2);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(me));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(followee));
        when(followRepository.existsByFollowerAndFollowee(me, followee)).thenReturn(true);
        when(todoRepository.findByMemberId(followee.getId(), pageable)).thenReturn(followeeTodoPage);

        // when
        TodoListResponse response = followService.readFolloweeTodoList(me, followee.getId(), pageNum);

        // then
        verify(todoRepository, times(1)).findByMemberId(followee.getId(), pageable);
        verify(todoRepository, times(2)).save(any(Todo.class));

        assertThat(response.todos()).hasSize(2);

        TodoWithDetailResponse todoWithDetailResponse1 = response.todos().getFirst();
        assertThat(todoWithDetailResponse1.hit()).isEqualTo(1); // 조회 시 조회수가 올라가며, 조회수 조회 가능
        assertThat(todoWithDetailResponse1.todo()).isEqualTo("할 일 1");
        assertThat(todoWithDetailResponse1.detailList()).hasSize(1);
        assertThat(todoWithDetailResponse1.detailList().getFirst().todo()).isEqualTo("1 - 세부 할 일"); // 세부 할 일까지 함께 조회됨

        TodoWithDetailResponse todoWithDetailResponse2 = response.todos().get(1);
        assertThat(todoWithDetailResponse2.hit()).isEqualTo(1); // 조회 시 조회수가 올라가며, 조회수 조회 가능
        assertThat(todoWithDetailResponse2.todo()).isEqualTo("할 일 2");
        assertThat(todoWithDetailResponse2.detailList()).isEmpty();
    }

    @Test
    @DisplayName("자기 자신의 투두 리스트를 조회할 경우 예외가 발생한다.(본인 투두 조회 API: /todo)")
    void readFolloweeTodoListFailedDueToBadRequest() {
        // given
        Member me = buildMember("me@example.com", "me", 1L);

        // when & then
        assertThrows(BadRequestException.class, () -> followService.readFolloweeTodoList(me, me.getId(), 0));
    }

    @Test
    @DisplayName("팔로우하지 않은 사용자의 투두 리스트 조회를 요청할 경우 예외가 발생한다.")
    void readFolloweeTodoListFailedDueToNotFollowedUserRequest() {
        // given
        Member me = buildMember("me@example.com", "me", 1L);
        Member other = buildMember("other@example.com", "other", 2L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(me));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(other));
        when(followRepository.existsByFollowerAndFollowee(me, other)).thenReturn(false);

        // when & then
        assertThrows(BadRequestException.class, () -> followService.readFolloweeTodoList(me, other.getId(), 0));
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

    private Todo buildTodo(Member member, String content) {
        return Todo.builder()
                .member(member)
                .todoList(content)
                .status(TodoStatus.NOT_STARTED)
                .build();
    }

    private TodoDetail buildTodoDetail(Todo parentTodo, String content) {
        return TodoDetail.builder()
                .todo(parentTodo)
                .detailList(content)
                .status(TodoDetailStatus.NOT_STARTED)
                .build();
    }
}