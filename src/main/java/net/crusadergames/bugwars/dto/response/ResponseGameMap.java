package net.crusadergames.bugwars.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseGameMap {
    private Long id;
    private String name;
    private String base64PreviewImg;
    private Integer swarms;
}
