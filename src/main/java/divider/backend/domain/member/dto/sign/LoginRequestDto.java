package divider.backend.domain.member.dto.sign;


import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import divider.backend.domain.member.entity.Authority;
import divider.backend.domain.member.entity.Member;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequestDto {
    @NotBlank(message = "{LoginRequestDto.username.notBlank}")
    @Schema(description = "이메일", defaultValue = "test@gmail.com")
    private String username;

    @NotBlank(message = "{LoginRequestDto.password.notBlank}")
    @Schema(description = "비밀번호", defaultValue = "test1234!")
    private String password;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(username, password);
    }
}