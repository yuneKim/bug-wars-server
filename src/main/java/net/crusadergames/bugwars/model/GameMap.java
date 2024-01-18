package net.crusadergames.bugwars.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameMap {
    @NotBlank
    private String name;

    @NotBlank
    private String fileName;

    @NotBlank
    private String previewImgUrl;

    @NotBlank
    private Integer swarms;
}
