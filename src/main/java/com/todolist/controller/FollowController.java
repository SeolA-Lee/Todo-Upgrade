package com.todolist.controller;

import com.todolist.auth.user.CustomUserDetails;
import com.todolist.controller.docs.FollowControllerDocs;
import com.todolist.service.FollowService;
import com.todolist.service.dto.response.FollowingListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FollowController implements FollowControllerDocs {

    private final FollowService followService;

    @GetMapping("/following")
    public ResponseEntity<FollowingListResponse> readFollowingList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int pageNum
    ) {
        FollowingListResponse response = followService.getFollowings(userDetails.member(), pageNum);
        return ResponseEntity.ok(response);
    }
}
