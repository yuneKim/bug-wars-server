package net.crusadergames.bugwars.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JsonIgnoreProperties({"scripts"})
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(name = "name")
    private String name;

    @NotBlank
    @Column(name = "script_string")
    private String scriptString;

    @NotBlank
    @Column(name = "bytecode_string")
    private String bytecodeString;

    @NotBlank
    @Column(name = "is_bytecode_valid")
    private boolean isBytecodeValid;
}
