package net.crusadergames.bugwars.game.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;

public class BugTests {

    @Test
    public void getAction_returnsActions() {
        Bug bug = new Bug(
                new Point(15, 15),
                0,
                new int[]{10, 11},
                Direction.SOUTH
        );

        Assertions.assertThat(bug.getAction(null)).isEqualTo(10);
        Assertions.assertThat(bug.getAction(null)).isEqualTo(11);
        Assertions.assertThat(bug.getAction(null)).isEqualTo(10);
        Assertions.assertThat(bug.getAction(null)).isEqualTo(11);
    }

    @Test
    public void getAction_handlesIfEnemy() {
        Bug bug = new Bug(
                new Point(15, 15),
                0,
                new int[]{30, 5, 10, 35, 0, 13, 35, 0},
                Direction.SOUTH
        );
        Bug enemy = new Bug(
                new Point(15, 16),
                1,
                new int[]{10, 11},
                Direction.SOUTH
        );
        Bug ally = new Bug(
                new Point(15, 16),
                0,
                new int[]{10, 11},
                Direction.SOUTH
        );

        Assertions.assertThat(bug.getAction(enemy)).isEqualTo(13);
        Assertions.assertThat(bug.getAction(ally)).isEqualTo(10);
        Assertions.assertThat(bug.getAction(null)).isEqualTo(10);
    }

    @Test
    public void getAction_handlesIfAlly() {
        Bug bug = new Bug(
                new Point(15, 15),
                0,
                new int[]{31, 5, 10, 35, 0, 13, 35, 0},
                Direction.SOUTH
        );
        Bug ally = new Bug(
                new Point(15, 16),
                0,
                new int[]{10, 11},
                Direction.SOUTH
        );
        Bug enemy = new Bug(
                new Point(15, 16),
                1,
                new int[]{10, 11},
                Direction.SOUTH
        );

        Assertions.assertThat(bug.getAction(ally)).isEqualTo(13);
        Assertions.assertThat(bug.getAction(enemy)).isEqualTo(10);
        Assertions.assertThat(bug.getAction(null)).isEqualTo(10);
    }

    @Test
    public void getAction_handlesIfFood() {
        Bug bug = new Bug(
                new Point(15, 15),
                0,
                new int[]{32, 3, 10, 13, 35, 0},
                Direction.SOUTH
        );

        Assertions.assertThat(bug.getAction(new Food())).isEqualTo(13);
        Assertions.assertThat(bug.getAction(null)).isEqualTo(10);
    }

    @Test
    public void getAction_handlesIfEmpty() {
        Bug bug = new Bug(
                new Point(15, 15),
                0,
                new int[]{33, 3, 10, 13, 35, 0},
                Direction.SOUTH
        );

        Assertions.assertThat(bug.getAction(null)).isEqualTo(13);
        Assertions.assertThat(bug.getAction(new Food())).isEqualTo(10);
    }

    @Test
    public void getAction_handlesIfWall() {
        Bug bug = new Bug(
                new Point(15, 15),
                0,
                new int[]{34, 3, 10, 13, 35, 0},
                Direction.SOUTH
        );

        Assertions.assertThat(bug.getAction(new Wall())).isEqualTo(13);
        Assertions.assertThat(bug.getAction(null)).isEqualTo(10);
    }

    @Test
    public void getAction_handlesIfGoto() {
        Bug bug = new Bug(
                new Point(15, 15),
                0,
                new int[]{35, 3, 10, 13, 35, 0},
                Direction.SOUTH
        );

        Assertions.assertThat(bug.getAction(null)).isEqualTo(13);
        Assertions.assertThat(bug.getAction(new Food())).isEqualTo(13);
        Assertions.assertThat(bug.getAction(new Wall())).isEqualTo(13);
        Assertions.assertThat(bug.getAction(new Bug())).isEqualTo(13);
    }

    @Test
    public void toString_throwsExceptionOnInvalidSwarm() {
        Bug bug = new Bug(
                new Point(15, 15),
                5,
                new int[]{10, 11},
                Direction.SOUTH
        );

        Assertions.assertThatThrownBy(bug::toString)
                .isInstanceOf(IllegalStateException.class);
    }


}
