package net.crusadergames.bugwars.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameMapDTO {
    private Integer id;
    private String name;
    private String previewImgUrl;
    private Integer swarms;
}
