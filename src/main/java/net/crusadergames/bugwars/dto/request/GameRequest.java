package net.crusadergames.bugwars.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.crusadergames.bugwars.game.Swarm;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRequest {

    @NotBlank
    private List<Swarm> swarms;

    @NotBlank
    private String mapName;
}
