package net.crusadergames.bugwars.parser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class BugAssemblyParserTests {
    private BugAssemblyParser parser;

    @BeforeEach
    public void setUp() {
        parser = new BugAssemblyParser(BugAssemblyCommands.getActions(),
                BugAssemblyCommands.getControls());
    }

    @Test
    public void parsesActionCommand() throws BugAssemblyParseException {
        String code = "att\n";
        Assertions.assertThat(parser.parse(code)).isEqualTo(List.of(13));
    }

    @Test
    public void parsesLineWithComment() throws BugAssemblyParseException {
        String code = "mov  # This is a comment\n";
        Assertions.assertThat(parser.parse(code)).isEqualTo(List.of(10));
    }

    @Test
    public void parsesLabelAndTarget() throws BugAssemblyParseException {
        String code = ":START\n" +
                "      goto START";
        Assertions.assertThat(parser.parse(code)).isEqualTo(List.of(35, 0));
    }

    @Test
    public void throwsBugAssemblyParserExceptionWhenLabelIsInvalid() {
        try {
            String code = ":START!LABEL";
            parser.parse(code);
            fail();
        } catch (BugAssemblyParseException e) {
            Assertions.assertThat(e.getMessage()).matches("Problem on line (\\d+): '[^']+'. " +
                    "Invalid label name: Character '(.)' at position (\\d+) is not allowed.");
        }

        try {
            String code = "goto start";
            parser.parse(code);
            fail();
        } catch (BugAssemblyParseException e) {
            Assertions.assertThat(e.getMessage()).matches("Problem on line (\\d+): '[^']+'. " +
                    "Invalid label name: Character '(.)' at position (\\d+) is not allowed.");
        }
    }

    @Test
    public void handlesLabelDefinedAfterTarget() throws BugAssemblyParseException {
        String code = """
                :START
                  mov # this is a comment
                  rotr
                  att
                  goto SECOND_LABEL

                :SECOND_LABEL
                  rotl
                  goto START""";
        Assertions.assertThat(parser.parse(code)).isEqualTo(List.of(10, 11, 13, 35, 5, 12, 35, 0));
    }

    @Test
    public void handlesLabelDefinedAfterMultipleTargets() throws BugAssemblyParseException {
        String code = """
                :START
                  mov # this is a comment
                  rotr
                  ifAlly SECOND_LABEL
                  att
                  goto SECOND_LABEL

                :SECOND_LABEL
                  rotl
                  goto START""";
        Assertions.assertThat(parser.parse(code)).isEqualTo(List.of(10, 11, 31, 7, 13, 35, 7, 12, 35, 0));
    }

    @Test
    public void handlesLabelsOnConsecutiveLines() throws BugAssemblyParseException {
        String code = """
                :START
                :SECOND_START
                  mov # this is a comment
                  rotr
                  ifAlly SECOND_START
                  att
                  goto SECOND_LABEL

                :SECOND_LABEL
                  att
                  goto START""";
        Assertions.assertThat(parser.parse(code)).isEqualTo(List.of(10, 11, 31, 0, 13, 35, 7, 13, 35, 0));
    }

    @Test
    public void handlesDanglingLabelDefinition() throws BugAssemblyParseException {
        String code = """
                :START
                :SECOND_START
                  mov # this is a comment
                  rotr
                  ifAlly SECOND_START
                  att
                  goto SECOND_LABEL

                :SECOND_LABEL
                """;
        Assertions.assertThat(parser.parse(code)).isEqualTo(List.of(10, 11, 31, 0, 13, 35, 0));
    }

    @Test
    public void throwsBugAssemblyParserExceptionWhenMissingLabel() {
        String code = """
                :
                      mov  // This is a comment
                      rotr
                      att
                      goto START""";
        Assertions.assertThatThrownBy(() -> parser.parse(code))
                .isInstanceOf(BugAssemblyParseException.class)
                .hasMessageMatching("Problem on line (\\d+): '[^']+'. " +
                        "':' not followed by label.");
    }

    @Test
    public void throwsBugAssemblyParserExceptionWhenMissingLabelTarget() {
        String code = """
                :START
                  mov # this is a comment
                  rotr
                  ifAlly SECOND_LABEL
                  att
                  goto SECOND_LABEL""";
        Assertions.assertThatThrownBy(() -> parser.parse(code))
                .isInstanceOf(BugAssemblyParseException.class)
                .hasMessageStartingWith("Could not find label for the following targets:");
    }

    @Test
    public void throwsBugAssemblyParserExceptionWhenLabelIsDefinedTwice() {
        String code = """
                :START
                :SECOND_START
                  mov # this is a comment
                  rotr
                  ifAlly SECOND_START
                  att
                  goto SECOND_LABEL

                :SECOND_START
                  att
                  goto START""";
        Assertions.assertThatThrownBy(() -> parser.parse(code))
                .isInstanceOf(BugAssemblyParseException.class)
                .hasMessageMatching("Problem on line (\\d+): '[^']+'. " +
                        "Label already declared on line (\\d+).");
    }

    @Test
    public void throwsBugAssemblyParserExceptionWhenActionLineHasExtraTokens() {
        String code = "  rotr something\n";
        Assertions.assertThatThrownBy(() -> parser.parse(code))
                .isInstanceOf(BugAssemblyParseException.class)
                .hasMessageMatching("Problem on line (\\d+): '[^']+'. Too many tokens. " +
                        "Expected 1. Found (\\d+).");
    }

    @Test
    public void throwsBugAssemblyParserExceptionWhenControlLineHasExtraTokens() {
        String code = "  ifEmpty LABEL something else\n";
        Assertions.assertThatThrownBy(() -> parser.parse(code))
                .isInstanceOf(BugAssemblyParseException.class)
                .hasMessageMatching("Problem on line (\\d+): '[^']+'. Too many tokens. " +
                        "Expected 2. Found (\\d+).");
    }

    @Test
    public void throwsBugAssemblyParserExceptionWhenCommandIsNotRecognized() {
        String code = "attack\n";
        Assertions.assertThatThrownBy(() -> parser.parse(code))
                .isInstanceOf(BugAssemblyParseException.class)
                .hasMessageMatching("Problem on line (\\d+): '[^']+'. " +
                        "Did not recognize command '[^']+'");
    }
}
