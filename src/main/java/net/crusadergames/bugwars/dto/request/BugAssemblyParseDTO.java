package net.crusadergames.bugwars.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BugAssemblyParseDTO {
    @NotBlank
    private String code;
}
