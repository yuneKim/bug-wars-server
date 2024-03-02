package net.crusadergames.bugwars.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtDTO {
    private String accessToken;
    private String refreshToken;
    private String username;
    private List<String> roles;
}
