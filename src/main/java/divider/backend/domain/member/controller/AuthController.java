package divider.backend.domain.member.controller;

import divider.backend.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import divider.backend.domain.member.entity.Member;
import divider.backend.domain.member.dto.sign.LoginRequestDto;
import divider.backend.domain.member.dto.sign.SignUpRequestDto;
import divider.backend.domain.member.dto.sign.TokenRequestDto;
import divider.backend.response.Response;
import divider.backend.domain.member.service.AuthService;

import static org.springframework.http.HttpStatus.*;
import static divider.backend.response.Response.*;
import static divider.backend.response.SuccessMessage.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth")
@Tag(name = "Auth", description = "Auth API Document")
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;

    // TODO 인증번호 발송 및 확인 API

    @Operation(summary = "Sign Up API", description = "put your sign up info.")
    @ResponseStatus(CREATED)
    @PostMapping("/sign-up")
    public Response signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        Member member = authService.signUp(signUpRequestDto);
        return success(SUCCESS_TO_SIGN_UP);
    }

    @Operation(summary = "Sign In API", description = "put your sign in info.")
    @PostMapping("/sign-in")
    @ResponseStatus(OK)
    public Response signIn(@Valid @RequestBody LoginRequestDto req) {
        return success(SUCCESS_TO_SIGN_IN, authService.signIn(req));
    }

    @Operation(summary = "Logout API", description = "this is logout")
    @PostMapping("/logout")
    @ResponseStatus(OK)
    public Response logout() {
        authService.logout(memberService.getCurrentMember());
        return success(SUCCESS_TO_LOGOUT);
    }

    @Operation(summary = "Reissue API", description = "put your token info which including access token and refresh token.")
    @ResponseStatus(OK)
    @PostMapping("/reissue")
    public Response reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return success(SUCCESS_TO_REISSUE, authService.reissue(tokenRequestDto));
    }



}
