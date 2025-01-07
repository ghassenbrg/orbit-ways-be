package io.ghassen.orbitways.controller;

import io.ghassen.orbitways.model.Game;
import io.ghassen.orbitways.service.GameService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameRestController {

    private final GameService gameService;

    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public Game createGame(
            @RequestParam String roomId,
            @RequestParam String hostId,
            @RequestParam String hostColor // "B" or "W"
    ) {
        return gameService.createGame(roomId, hostId, hostColor);
    }

    @GetMapping("/join")
    public Game joinGame(
            @RequestParam String roomId,
            @RequestParam String joinerId,
            @RequestParam String joinerColor // "B" or "W"
    ) {
        return gameService.joinGame(roomId, joinerId, joinerColor);
    }

    @PostMapping("/reset")
    public Game resetBoard(
            @RequestParam String roomId,
            @RequestParam boolean clearScore
    ) {
        return gameService.resetBoard(roomId, clearScore);
    }
}