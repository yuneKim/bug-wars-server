package net.crusadergames.bugwars.repository;

import net.crusadergames.bugwars.model.SampleString;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleStringRepository extends JpaRepository<SampleString, Long> {
}
