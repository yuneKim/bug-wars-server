package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/game")
public class GameController {
    @Autowired
    Game game;

    @GetMapping
    public void play() {
        game.play();
    }
}
