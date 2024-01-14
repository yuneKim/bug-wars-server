package net.crusadergames.bugwars.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "maps")
@Data
public class GameMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String filePath;

    @NotBlank
    private String imgFilePath;

    @NotBlank
    private Integer swarms;
}
