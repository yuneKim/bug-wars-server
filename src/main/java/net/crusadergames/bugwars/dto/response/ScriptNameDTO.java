package net.crusadergames.bugwars.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptNameDTO {
    private Long id;
    private String name;
    private String author;
}
