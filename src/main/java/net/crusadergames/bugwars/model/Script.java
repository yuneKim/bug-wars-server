package net.crusadergames.bugwars.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.crusadergames.bugwars.model.auth.User;

@Entity
@Table(name = "script")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Script {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @NotBlank
    private String name;

    @NotBlank
    private String scriptString;

    @NotBlank
    private String byteCodeString;

    @NotBlank
    private boolean isByteCodeValid;
}
