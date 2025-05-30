package com.todolist.controller;

import com.todolist.auth.jwt.dto.JwtToken;
import com.todolist.controller.docs.MemberControllerDocs;
import com.todolist.service.MemberService;
import com.todolist.service.dto.request.MemberRequest;
import com.todolist.service.dto.response.RegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody MemberRequest request) {
        RegisterResponse response = memberService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@RequestBody MemberRequest request) {
        JwtToken token = memberService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
