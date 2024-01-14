package net.crusadergames.bugwars.game.setup;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MapValidatorTests {

    @Test
    public void returnsTrueOnValidMap() {
        String[] lines = new String[]{
                "6,6,Test",
                "XXXXXX",
                "X0  1X",
                "X aa X",
                "X aa X",
                "X2  3X",
                "XXXXXX",
        };
        Assertions.assertThat(new MapValidator(lines).validate()).as("Valid Map").isTrue();

        lines[0] = "6, 6, Test";
        Assertions.assertThat(new MapValidator(lines).validate()).as("Valid Map with spaces in title").isTrue();

        String[] lines2 = new String[]{
                "6,6,Test",
                "XXXXXX",
                "X0  1X",
                "X a  X",
                "X  a X",
                "X2  3X",
                "XXXXXX",
        };
        Assertions.assertThat(new MapValidator(lines2).validate()).as("Twisted Mirror").isTrue();
    }

    @Test
    public void returnsFalseOnBadTitle() {
        String[] lines = new String[]{
                "Test",
                "XXXXXX",
                "X0  1X",
                "X a  X",
                "X  a X",
                "X2  3X",
                "XXXXXX",
        };
        Assertions.assertThat(new MapValidator(lines).validate()).isFalse();
    }

    @Test
    public void returnsFalseOnIncorrectDimensions() {
        String[] lines = new String[]{
                "6,5,Test",
                "XXXXXX",
                "X0  1X",
                "X a  X",
                "X  a X",
                "X2  3X",
                "XXXXXX",
        };
        Assertions.assertThat(new MapValidator(lines).validate()).as("Bad height").isFalse();

        lines[0] = "5,6,Test";
        Assertions.assertThat(new MapValidator(lines).validate()).as("Bad width").isFalse();
    }

    @Test
    public void returnsFalseOnMapNotSurroundedByWalls() {
        String[] lines = new String[]{
                "6,6,Test",
                "XX XXX",
                "X0  1X",
                "X a  X",
                "X  a X",
                "X2  3X",
                "XXXXXX",
        };
        Assertions.assertThat(new MapValidator(lines).validate()).as("Missing top wall").isFalse();

        lines[1] = "XXXXXX";
        lines[3] = "X a  a";
        Assertions.assertThat(new MapValidator(lines).validate()).as("Missing right wall").isFalse();

        lines[1] = "XXXXXX";
        lines[3] = "a a  X";
        Assertions.assertThat(new MapValidator(lines).validate()).as("Missing left wall").isFalse();
    }

    @Test
    public void returnsFalseOnInvalidSymbol() {
        String[] lines = new String[]{
                "6,6,Test",
                "XXXXXX",
                "X0  1X",
                "X b  X",
                "X  b X",
                "X2  3X",
                "XXXXXX",
        };
        Assertions.assertThat(new MapValidator(lines).validate()).isFalse();
    }

    @Test
    public void returnsFalseOnUnequalSwarmSizes() {
        String[] lines = new String[]{
                "6,6,Test",
                "XXXXXX",
                "X0  1X",
                "X a  X",
                "X  a X",
                "X0  0X",
                "XXXXXX",
        };
        Assertions.assertThat(new MapValidator(lines).validate()).isFalse();
    }

    @Test
    public void returnsFalseOnAsymmetricBugPlacement() {
        String[] lines = new String[]{
                "6,6,Test",
                "XXXXXX",
                "X 00 X",
                "X a  X",
                "X  a X",
                "X 1 1X",
                "XXXXXX",
        };
        Assertions.assertThat(new MapValidator(lines).validate()).isFalse();
    }

    @Test
    public void returnsFalseOnOnlyOneLineOfSymmetry() {
        String[] lines = new String[]{
                "6,6,Test",
                "XXXXXX",
                "X0  1X",
                "X aa X",
                "X    X",
                "X2  3X",
                "XXXXXX",
        };
        Assertions.assertThat(new MapValidator(lines).validate()).as("Bad X Axis").isFalse();

        String[] lines2 = new String[]{
                "6,6,Test",
                "XXXXXX",
                "X0  1X",
                "X a  X",
                "X a  X",
                "X2  3X",
                "XXXXXX",
        };
        Assertions.assertThat(new MapValidator(lines2).validate()).as("Bad Y Axis").isFalse();
    }

}
