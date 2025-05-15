package com.todolist.service;

import com.todolist.entity.Follow;
import com.todolist.entity.Member;
import com.todolist.entity.Todo;
import com.todolist.exception.BadRequestException;
import com.todolist.exception.ConflictException;
import com.todolist.exception.NotFoundException;
import com.todolist.repository.FollowRepository;
import com.todolist.repository.MemberRepository;
import com.todolist.repository.TodoRepository;
import com.todolist.service.dto.response.FollowingListResponse;
import com.todolist.service.dto.response.FollowingResponse;
import com.todolist.service.dto.response.TodoListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;

    public FollowingListResponse getFollowings(Member me, int pageNum) {

        Pageable pageable = PageRequest.of(pageNum, 10);
        Page<FollowingResponse> followings = followRepository.findFolloweeByFollower(me, pageable)
                .map(FollowingResponse::from);

        return FollowingListResponse.from(followings);
    }

    @Transactional
    public void follow(Member me, Long followeeId) {

        if (me.getId().equals(followeeId)) {
            throw new BadRequestException("자기 자신은 팔로우 대상이 아닙니다.");
        }

        Member follower = findMemberOrElseThrow(me.getId(), "사용자를 찾을 수 없습니다.");
        Member followee = findMemberOrElseThrow(followeeId, "팔로우할 사용자를 찾을 수 없습니다.");

        if (followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new ConflictException("이미 팔로우한 회원입니다.");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .followee(followee)
                .build();
        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Member me, Long followeeId) {

        Member follower = findMemberOrElseThrow(me.getId(), "사용자를 찾을 수 없습니다.");
        Member followee = findMemberOrElseThrow(followeeId, "언팔로우할 사용자를 찾을 수 없습니다.");

        if (!followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new BadRequestException("팔로우가 되어 있지 않은 사용자입니다.");
        }

        followRepository.deleteByFollowerAndFollowee(follower, followee);
    }

    @Transactional
    public TodoListResponse readFolloweeTodoList(Member me, Long followeeId, int pageNum) {

        if (me.getId().equals(followeeId)) {
            throw new BadRequestException("본인의 투두리스트를 요청하였습니다.");
        }

        Member follower = findMemberOrElseThrow(me.getId(), "사용자를 찾을 수 없습니다.");
        Member followee = findMemberOrElseThrow(followeeId, "팔로우한 사용자를 찾을 수 없습니다.");

        if (!followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new BadRequestException("해당 사용자를 팔로우하고 있지 않습니다.");
        }

        Pageable pageable = PageRequest.of(pageNum, 10);
        Page<Todo> todos = todoRepository.findByMemberId(followee.getId(), pageable);

        /**
         * 조회수 증가
         */
        todos.forEach(todo -> {
            todo.increaseHit();
            todoRepository.save(todo);
        });

        return TodoListResponse.from(todos);
    }

    private Member findMemberOrElseThrow(Long memberId, String message) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(message));
    }
}
