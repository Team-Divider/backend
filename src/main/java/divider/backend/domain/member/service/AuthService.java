package divider.backend.domain.member.service;

import javax.transaction.Transactional;

import divider.backend.config.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import divider.backend.config.jwt.JwtProvider;
import divider.backend.domain.member.dto.sign.*;
import divider.backend.domain.member.entity.Authority;
import divider.backend.domain.member.entity.Member;
import divider.backend.exception.situation.LoginFailureException;
import divider.backend.exception.situation.UsernameAlreadyExistsException;
import divider.backend.domain.member.repository.MemberRepository;
import org.springframework.util.StringUtils;

import java.time.Duration;

import static java.time.Duration.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    public Member signUp(SignUpRequestDto req) {
        validateSignUpInfo(req);
        Member member = createSignupFormOfUser(req);
        memberRepository.save(member);
        return member;
    }

    public TokenResponseDto signIn(LoginRequestDto req) {
        Member member = validateExistsByUsername(req);
        validatePassword(req, member);

        Authentication authentication = getUserAuthentication(req);
        TokenDto tokenDto = jwtProvider.generateTokenDto(authentication);
        Duration duration = ofMillis(tokenDto.getRefreshTokenExpiresIn());
        redisService.setValues("RT: " + authentication.getName(), tokenDto.getRefreshToken(), duration);
        return new TokenResponseDto(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
    }

    public void logout(Member member) {
        redisService.deleteValues("RT: " + member.getUsername());
    }

    public TokenResponseDto reissue(TokenRequestDto tokenRequestDto) {
        validateRefreshToken(tokenRequestDto);

        Authentication authentication = jwtProvider.getAuthentication(tokenRequestDto.getAccessToken());

        validateExistsByRefreshToken(authentication);
        validateRefreshTokenOwner(authentication, tokenRequestDto);

        TokenDto tokenDto = jwtProvider.generateTokenDto(authentication);
        Duration duration = ofMillis(tokenDto.getRefreshTokenExpiresIn());
        redisService.setValues("RT: " + authentication.getName(), tokenDto.getRefreshToken(), duration);

        return new TokenResponseDto(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
    }

    private void validateSignUpInfo(SignUpRequestDto signUpRequestDto) {
        if (memberRepository.existsByUsername(signUpRequestDto.getUsername())) {
            throw new UsernameAlreadyExistsException(signUpRequestDto.getUsername());
        }
    }

    private Member createSignupFormOfUser(SignUpRequestDto req) {
        return Member.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .authority(Authority.ROLE_USER)
                .build();
    }

    private Member validateExistsByUsername(LoginRequestDto req) {
        return memberRepository.findByUsername(req.getUsername()).orElseThrow(() -> {
            throw new LoginFailureException();
        });
    }

    private void validatePassword(LoginRequestDto loginRequestDto, Member member) {
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new LoginFailureException();
        }
    }

    private Authentication getUserAuthentication(LoginRequestDto req) {
        UsernamePasswordAuthenticationToken authenticationToken = req.toAuthentication();
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    private void validateExistsByRefreshToken(Authentication authentication) {
        String refreshToken = redisService.getValues("RT: " + authentication.getName());
        if (!StringUtils.hasText(refreshToken)) {
            throw new RuntimeException("로그아웃된 사용자입니다.");
        }
    }

    private void validateRefreshToken(TokenRequestDto tokenRequestDto) {
        if (!jwtProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }
    }

    private void validateRefreshTokenOwner(Authentication authentication, TokenRequestDto tokenRequestDto) {
        if (!redisService.getValues("RT: " + authentication.getName()).equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }
    }
}
