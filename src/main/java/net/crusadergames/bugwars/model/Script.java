package net.crusadergames.bugwars.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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

    @ToString.Exclude
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

    public Script(Long id, String name, String bytecode) {
        this.id = id;
        this.name = name;
        this.bytecode = bytecode;
    }

    public int[] deserializeBytecode() {
        String[] ints = bytecode.replaceAll("[\\[\\]]", "").split(", ");
        int[] result = new int[ints.length];
        for (int i = 0; i < ints.length; i++) {
            result[i] = Integer.parseInt(ints[i]);
        }
        return result;
    }
}
