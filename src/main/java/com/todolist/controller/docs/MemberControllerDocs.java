package com.todolist.controller.docs;

import com.todolist.auth.jwt.dto.JwtToken;
import com.todolist.service.dto.request.MemberRequest;
import com.todolist.service.dto.response.RegisterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Member", description = "Member API")
public interface MemberControllerDocs {

    @Operation(summary = "회원가입", description = "회원가입을 요청합니다.")
    @ApiResponse(description = "회원가입 성공", responseCode = "201")
    ResponseEntity<RegisterResponse> register(MemberRequest request);

    @Operation(summary = "로그인", description = "로그인을 요청합니다.")
    @ApiResponse(description = "로그인 성공", responseCode = "200")
    ResponseEntity<JwtToken> login(MemberRequest request);
}
