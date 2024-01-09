package net.crusadergames.bugwars.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.crusadergames.bugwars.game.entity.Entity;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {
    private Entity[][] initialBattleground;
    private List<List<?>> turns;
}
