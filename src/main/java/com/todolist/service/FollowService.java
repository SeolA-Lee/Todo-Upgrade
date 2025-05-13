package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.repository.FollowRepository;
import com.todolist.service.dto.response.FollowingListResponse;
import com.todolist.service.dto.response.FollowingResponse;
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

    public FollowingListResponse getFollowings(Member me, int pageNum) {

        Pageable pageable = PageRequest.of(pageNum, 10);
        Page<FollowingResponse> followings = followRepository.findFolloweeByFollower(me, pageable)
                .map(FollowingResponse::from);

        return FollowingListResponse.from(followings);
    }
}
