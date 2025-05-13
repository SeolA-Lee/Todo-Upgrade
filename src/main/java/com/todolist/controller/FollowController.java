package com.todolist.controller;

import com.todolist.auth.user.CustomUserDetails;
import com.todolist.controller.docs.FollowControllerDocs;
import com.todolist.service.FollowService;
import com.todolist.service.dto.response.FollowingListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/follow/{followeeId}")
    public ResponseEntity<Void> follow(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(name = "followeeId") Long followeeId
    ) {
        followService.follow(userDetails.member(), followeeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/follow/{followeeId}")
    public ResponseEntity<Void> unfollow(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(name = "followeeId") Long followeeId
    ) {
        followService.unfollow(userDetails.member(), followeeId);
        return ResponseEntity.noContent().build();
    }
}
