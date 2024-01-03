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
    @Column(name = "name", length = 100)
    @Size(min = 1, max = 100)
    private String name;

    @NotBlank
    @Column(name = "script_string", length = 10000)
    @Size(max = 10000)
    private String scriptString;

    @NotBlank
    @Size(max = 10000)
    @Column(name = "bytecode_string", length = 10000)
    private String bytecodeString;

    @NotBlank
    @Column(name = "is_bytecode_valid")
    private boolean isBytecodeValid;

}
