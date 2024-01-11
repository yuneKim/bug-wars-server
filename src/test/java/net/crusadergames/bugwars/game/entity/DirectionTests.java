package net.crusadergames.bugwars.game.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;

public class DirectionTests {
    @Test
    public void faceCenter_returnsDirectionFacingCenter() {
        Direction direction = Direction.faceCenter(new Point(15, 15), 31, 31);
        Assertions.assertThat(direction).as("handles dead center bug").isEqualTo(Direction.SOUTH);

        direction = Direction.faceCenter(new Point(15, 10), 31, 31);
        Assertions.assertThat(direction).as("handles bug positioned north").isEqualTo(Direction.SOUTH);

        direction = Direction.faceCenter(new Point(15, 20), 31, 31);
        Assertions.assertThat(direction).as("handles bug positioned south").isEqualTo(Direction.NORTH);

        direction = Direction.faceCenter(new Point(10, 15), 31, 31);
        Assertions.assertThat(direction).as("handles bug positioned west").isEqualTo(Direction.EAST);

        direction = Direction.faceCenter(new Point(20, 15), 31, 31);
        Assertions.assertThat(direction).as("handles bug positioned east").isEqualTo(Direction.WEST);

        direction = Direction.faceCenter(new Point(20, 20), 31, 31);
        Assertions.assertThat(direction).as("bug placed on diagonal favors north/south").isEqualTo(Direction.NORTH);
    }

    @Test
    public void turnLeft_returnsCounterClockwiseDirection() {
        Direction direction = Direction.NORTH.turnLeft();
        Assertions.assertThat(direction).isEqualTo(Direction.WEST);

        direction = Direction.WEST.turnLeft();
        Assertions.assertThat(direction).isEqualTo(Direction.SOUTH);

        direction = Direction.SOUTH.turnLeft();
        Assertions.assertThat(direction).isEqualTo(Direction.EAST);

        direction = Direction.EAST.turnLeft();
        Assertions.assertThat(direction).isEqualTo(Direction.NORTH);
    }

    @Test
    public void turnRight_returnsCounterClockwiseDirection() {
        Direction direction = Direction.NORTH.turnRight();
        Assertions.assertThat(direction).isEqualTo(Direction.EAST);

        direction = Direction.WEST.turnRight();
        Assertions.assertThat(direction).isEqualTo(Direction.NORTH);

        direction = Direction.SOUTH.turnRight();
        Assertions.assertThat(direction).isEqualTo(Direction.WEST);

        direction = Direction.EAST.turnRight();
        Assertions.assertThat(direction).isEqualTo(Direction.SOUTH);
    }

    @Test
    public void forward_returnsPointInFrontOfCurrentPosition() {
        Point point = Direction.NORTH.forward(new Point(15, 15));
        Point expected = new Point(15, 14);
        Assertions.assertThat(point).isEqualTo(expected);

        point = Direction.WEST.forward(new Point(15, 15));
        expected = new Point(14, 15);
        Assertions.assertThat(point).isEqualTo(expected);

        point = Direction.SOUTH.forward(new Point(15, 15));
        expected = new Point(15, 16);
        Assertions.assertThat(point).isEqualTo(expected);

        point = Direction.EAST.forward(new Point(15, 15));
        expected = new Point(16, 15);
        Assertions.assertThat(point).isEqualTo(expected);
    }
}
