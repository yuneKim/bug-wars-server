# Bug Assembly Syntax Rules

## Example

```
:START 
    ifEnemy ATTACK  # this is a comment
    ifWall TURN_RIGHT
    goto START

:ATTACK
    att
    goto EAT

:EAT
    eat
    goto START
    
:TURN_RIGHT
    rotr
    goto START
```

## Grammar

---

### ACTION STATEMENT

An `ACTION STATEMENT` is the simplest command. It executes an action. It has no arguments. Each action takes 1 game tick
to execute.

```
att
mov
```

Available `ACTION STATEMENT`s are:

| Action | Description                                                                                                         |
|--------|---------------------------------------------------------------------------------------------------------------------|
| `noop` | Perform no action.                                                                                                  |
| `mov`  | Move a single grid square in the direction the bug is facing.                                                       |
| `rotr` | Rotate clockwise                                                                                                    |
| `rotl` | Rotate counterclockwise                                                                                             |
| `att`  | Attack in the direction the bug is facing. If an enemy is present, they will be destroyed and become food.          |
| `eat`  | Attempt to eat from the grid square the bug is facing. If this action is successful, a new friendly bug will spawn. |

---

### CONTROL STATEMENT

A `CONTROL STATEMENT` accepts a `LABEL` as a target. `goto` will always begin code execution at its `LABEL`. All other
control statements will conditionally jump to their `LABEL`.

```
ifEnemy SOME_LABEL
goto ANOTHER_LABEL
```

Available `CONTROL STATEMENT`s are:

| Control   | Description                                                                       |
|-----------|-----------------------------------------------------------------------------------|
| `ifEnemy` | Jump to target `LABEL` if an enemy is in the square directly in front of the bug. |
| `ifAlly`  | Jump to target `LABEL` if an ally is in the square directly in front of the bug.  |
| `ifFood`  | Jump to target `LABEL` if food is in the square directly in front of the bug.     |
| `ifEmpty` | Jump to target `LABEL` if the square directly in front of the bug is empty.       |
| `ifWall`  | Jump to target `LABEL` if there is a wall directly in front of the bug.           |
| `goto`    | Jump to target `LABEL`.                                                           |

---

### LABEL

A `LABEL` serves as a target for a `CONTROL STATEMENT`. Define a `LABEL` like

```
:MY_LABEL
```

And then use it as a target. This code will cause the bug to attack and move. It will then jump back to the `LABEL`,
attack again, and so on.

```
:MY_LABEL
    att
    mov
    goto MY_LABEL
```

A `LABEL` must be composed of capital letters, numbers, and/or underscores.

---

### COMMENT

Place `COMMENT`s in your code by using the `#` symbol.

```
:START
    att
    mov  # this is a comment and will not be executed
    goto START
```