package net.crusadergames.bugwars.repository;

import net.crusadergames.bugwars.model.GameMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameMapRepository extends JpaRepository<GameMap, Long> {
}
