package io.ghassen.orbitways.controller;

import io.ghassen.orbitways.dto.PlaceMarbleMessage;
import io.ghassen.orbitways.dto.ResetGameMessage;
import io.ghassen.orbitways.model.Game;
import io.ghassen.orbitways.service.GameService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameWebSocketController(GameService gameService,
                                   SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/placeMarble")
    public void placeMarble(PlaceMarbleMessage message) {
        Game updatedGame = gameService.placeMarble(
                message.getRoomId(),
                message.getUserId(),
                message.getRow(),
                message.getCol()
        );
        if (updatedGame != null) {
            updatedGame.setMarbleMessage(message);
            messagingTemplate.convertAndSend(
                    "/topic/room/" + message.getRoomId() + "/placeMarble",
                    updatedGame
            );
        }
    }

    @MessageMapping("/resetGame")
    public void resetGame(ResetGameMessage message) {
        Game updatedGame = gameService.resetGame(
                message.getRoomId(),
                message.isFullReset()
        );
        if (updatedGame != null) {
            messagingTemplate.convertAndSend(
                    "/topic/room/" + message.getRoomId(),
                    updatedGame
            );
        }
    }
}
