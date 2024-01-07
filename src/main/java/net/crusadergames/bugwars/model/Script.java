package net.crusadergames.bugwars.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.crusadergames.bugwars.model.auth.User;

@Entity
@Table(name = "scripts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Script {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(name = "name", length = 100)
    @Size(min = 1, max = 100)
    private String name;

    @NotBlank
    @Column(name = "raw", length = 10000)
    @Size(max = 10000)
    private String raw;

    @NotBlank
    @Size(max = 10000)
    @Column(name = "bytecode", length = 10000)
    private String bytecode;

    @Column(name = "is_bytecode_valid")
    private boolean isBytecodeValid;

}
