package net.crusadergames.bugwars.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableResponse {
    private String table;
    private Page<?> page;
    private List<String> fieldOrder;
    private Map<String, String> types;
}
