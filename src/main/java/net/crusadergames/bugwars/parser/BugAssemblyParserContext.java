package net.crusadergames.bugwars.parser;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class BugAssemblyParserContext {
    private final Map<String, Integer> labelLineNumbers = new HashMap<>();
    private final Map<String, List<Integer>> labelPlaceholderLineNumbers = new HashMap<>();
    private String line;
    private int lineNumber;
    private int charPosition;

    public void setLineInfo(String line, int lineNumber) {
        this.line = line;
        this.lineNumber = lineNumber;
    }

    public Integer getLabelLineNumber(String label) {
        return this.labelLineNumbers.get(label);
    }

    public void addLabelLineNumber(String label) {
        this.labelLineNumbers.put(label, this.lineNumber);
    }

    public List<Integer> getLabelPlaceholderLineNumbers(String label) {
        return labelPlaceholderLineNumbers.get(label);
    }

    public void addLabelPlaceholderLineNumber(String label) {
        if (this.labelPlaceholderLineNumbers.containsKey(label)) {
            this.labelPlaceholderLineNumbers.get(label).add(this.lineNumber);
        } else {
            List<Integer> placeholderLineNumbers = new ArrayList<>(List.of(this.lineNumber));
            this.labelPlaceholderLineNumbers.put(label, placeholderLineNumbers);
        }
    }
}
