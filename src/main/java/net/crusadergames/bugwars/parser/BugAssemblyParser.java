package net.crusadergames.bugwars.parser;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BugAssemblyParser {
    private final Map<String, Integer> actions;
    private final Map<String, Integer> controls;
    private final Map<String, Integer> labels = new HashMap<>();
    private final Map<String, List<Integer>> labelPlaceholders = new HashMap<>();
    private final List<Integer> bytecode = new ArrayList<>();
    private final BugAssemblyParserContext context = new BugAssemblyParserContext();

    public BugAssemblyParser(Map<String, Integer> actions, Map<String, Integer> controls) {
        this.actions = actions;
        this.controls = controls;
    }

    public List<Integer> parse(String code) throws BugAssemblyParseException {
        String[] lines = code.split("\\R");
        for (int i = 0; i < lines.length; i++) {
            context.setLineInfo(lines[i], i + 1);
            parseLine(lines[i]);
        }

        checkForMissingDestinations();
        fixDanglingLabelDefinition();
        return bytecode;
    }

    private void parseLine(String line) throws BugAssemblyParseException {
        String[] tokens = extractTokens(line);

        // skip empty line
        if (tokens.length == 0) return;

        if (line.startsWith(":")) {
            processLabel(tokens);
        } else if (controls.containsKey(tokens[0])) {
            processFlowControl(tokens);
        } else if (actions.containsKey(tokens[0])) {
            processAction(tokens);
        } else {
            throw new BugAssemblyParseException(String.format(
                    "Problem on line %s: '%s'. Did not recognize command '%s'",
                    context.getLineNumber(),
                    line,
                    tokens[0]
            ));
        }
    }

    private String[] extractTokens(String line) {
        line = removeComment(line);
        return Pattern.compile("\\S+")
                .matcher(line)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);
    }

    private String removeComment(String line) {
        int commentStart = line.indexOf("#");
        if (commentStart == -1) return line;

        return line.substring(0, commentStart);
    }

    private void processLabel(String[] tokens) throws BugAssemblyParseException {
        String label = tokens[0].substring(1);
        if (label.isEmpty()) {
            throw new BugAssemblyParseException(
                    String.format(
                            "Problem on line %s: '%s'. ':' not followed by label.",
                            context.getLineNumber(),
                            context.getLine()
                    )
            );
        }
        validateLabelName(label);
        checkForDuplicateLabel(label);
        context.addLabelLineNumber(label);
        labels.put(label, bytecode.size());
        if (labelPlaceholders.containsKey(label)) {
            labelPlaceholders.get(label).forEach(location -> bytecode.set(location, bytecode.size()));
            labelPlaceholders.remove(label);
        }
    }

    private void validateLabelName(String label) throws BugAssemblyParseException {
        for (int i = 0; i < label.length(); i++) {
            context.setCharPosition(i);
            validateCharacter(label.charAt(i));
        }
    }

    private void checkForDuplicateLabel(String label) throws BugAssemblyParseException {
        if (labels.containsKey(label)) {
            throw new BugAssemblyParseException(
                    String.format(
                            "Problem on line %s: '%s'. Label already declared on line %s.",
                            context.getLineNumber(),
                            context.getLine(),
                            context.getLabelLineNumber(label)
                    )
            );
        }
    }

    private void validateCharacter(char c) throws BugAssemblyParseException {
        if (Character.isUpperCase(c) || Character.isDigit(c) || c == '_') return;

        throw new BugAssemblyParseException(
                String.format(
                        "Problem on line %s: '%s'. Invalid label name: Character '%s' at position %s is not allowed.",
                        context.getLineNumber(),
                        context.getLine(),
                        c,
                        context.getCharPosition()
                )
        );
    }

    private void processFlowControl(String[] tokens) throws BugAssemblyParseException {
        if (tokens.length > 2) {
            throw new BugAssemblyParseException(
                    String.format(
                            "Problem on line %s: '%s'. Too many tokens. Expected 2. Found %s.",
                            context.getLineNumber(),
                            context.getLine(),
                            tokens.length
                    )
            );
        }
        String command = tokens[0];
        String target = tokens[1];
        validateLabelName(target);
        bytecode.add(controls.get(command));
        bytecode.add(getDestination(target));
    }

    private Integer getDestination(String target) {
        Integer position;
        if (labels.containsKey(target)) {
            position = labels.get(target);
        } else if (labelPlaceholders.containsKey(target)) {
            context.addLabelPlaceholderLineNumber(target);
            labelPlaceholders.get(target).add(bytecode.size());
            position = -1;
        } else {
            context.addLabelPlaceholderLineNumber(target);
            List<Integer> placeholderLocations = new ArrayList<>(List.of(bytecode.size()));
            labelPlaceholders.put(target, placeholderLocations);
            position = -1;
        }
        return position;
    }

    private void processAction(String[] tokens) throws BugAssemblyParseException {
        if (tokens.length > 1) {
            throw new BugAssemblyParseException(
                    String.format(
                            "Problem on line %s: '%s'. Too many tokens. Expected 1. Found %s.",
                            context.getLineNumber(),
                            context.getLine(),
                            tokens.length
                    )
            );
        }
        bytecode.add(actions.get(tokens[0]));
    }

    private void checkForMissingDestinations() throws BugAssemblyParseException {
        if (labelPlaceholders.isEmpty()) return;

        Set<String> missingLabels = labelPlaceholders.keySet();

        // produces a string like: LABEL on lines [4, 6], OTHER_LABEL on lines [7, 9]
        String missingLabelList = missingLabels.stream()
                .sorted(Comparator.comparing((label) -> context.getLabelPlaceholderLineNumbers(label).get(0)))
                .map((label) -> String.format(
                                "%s on lines [%s]",
                                label,
                                context.getLabelPlaceholderLineNumbers(label).stream()
                                        .map(String::valueOf)
                                        .collect(Collectors.joining(", "))
                        )
                )
                .collect(Collectors.joining(", "));

        throw new BugAssemblyParseException("Could not find label for the following targets: " + missingLabelList);
    }

    private void fixDanglingLabelDefinition() {
        if (bytecode.size() < 2) return;

        int penultimateCommand = bytecode.get(bytecode.size() - 2);
        int ultimateCommand = bytecode.get(bytecode.size() - 1);
        if (controls.containsValue(penultimateCommand) && ultimateCommand >= bytecode.size()) {
            bytecode.set(bytecode.size() - 1, 0);
        }
    }
}
