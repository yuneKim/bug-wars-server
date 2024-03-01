package net.crusadergames.bugwars.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponseDTO {
    private String username;
    private String profileName;
    private String email;
    private String profilePicture;
    private int scriptAmount;
}
