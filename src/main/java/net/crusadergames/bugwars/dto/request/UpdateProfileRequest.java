package net.crusadergames.bugwars.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {


    @Size(min = 3, max = 20)
    private String username;


    @Size(max = 50)
    @Email
    private String email;


    @Size(min = 6, max = 40)
    private String newPassword;




}

