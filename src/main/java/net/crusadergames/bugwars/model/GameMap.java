package net.crusadergames.bugwars.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "maps")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String fileName;

    @NotBlank
    private String previewImgUrl;

    @NotBlank
    private Integer swarms;
}
