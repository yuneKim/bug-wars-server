package net.crusadergames.bugwars.repository;

import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScriptRepository extends JpaRepository<Script, Long> {

    List<Script> findByUser(User user);

    List<Script> findByIsBytecodeValidTrue();

    Boolean existsByNameIgnoreCase(String name);

}
