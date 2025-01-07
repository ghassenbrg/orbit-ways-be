package io.ghassen.orbitways.service;

import io.ghassen.orbitways.model.CellValue;
import io.ghassen.orbitways.model.Game;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GameService {
    private final Map<String, Game> rooms = new HashMap<>();

    public Game createGame(String roomId, String hostId, String hostColor) {
        Game game = new Game(roomId);
        // If host picks "B", we store host as Black.
        if (hostColor.equalsIgnoreCase("B")) {
            game.setPlayerBlack(hostId);
        } else {
            game.setPlayerWhite(hostId);
            // Also set currentPlayer to 'W' if host is White initially
            game.setCurrentPlayer(CellValue.W);
        }
        rooms.put(roomId, game);
        return game;
    }

    public Game getGame(String roomId) {
        return rooms.get(roomId);
    }

    /**
     * Join the game as either 'B' or 'W', if available
     */
    public Game joinGame(String roomId, String joinerId, String joinerColor) {
        // If room doesn't exist, return null or create a new one? Up to you.
        if (!rooms.containsKey(roomId)) {
            return null;
        }
        Game game = rooms.get(roomId);

        // If joiner wants black but black is already taken, or similarly for white, handle error or override
        if (joinerColor.equalsIgnoreCase("B")) {
            // If black slot is free
            if (game.getPlayerBlack() == null) {
                game.setPlayerBlack(joinerId);
            }
        } else {
            // If white slot is free
            if (game.getPlayerWhite() == null) {
                game.setPlayerWhite(joinerId);
            }
        }

        return game;
    }

    /**
     * This is the critical part: we only accept the move if:
     * 1) It's not gameOver
     * 2) The currentPlayer matches the color of the user making the request
     */
    public Game placeMarble(String roomId, String userId, int r, int c) {
        Game game = rooms.get(roomId);
        if (game == null || game.isGameOver()) {
            return game; // no changes
        }

        // Determine if user is black or white
        String blackId = game.getPlayerBlack();
        String whiteId = game.getPlayerWhite();

        if (blackId != null && blackId.equals(userId)) {
            // user is black
            if (game.getCurrentPlayer() != CellValue.B) {
                // Not black's turn, ignore
                return game;
            }
        } else if (whiteId != null && whiteId.equals(userId)) {
            // user is white
            if (game.getCurrentPlayer() != CellValue.W) {
                // Not white's turn, ignore
                return game;
            }
        } else {
            // user not recognized as black or white; ignore
            return game;
        }

        // If we get here, user is indeed the current player
        CellValue[][] board = game.getBoard();
        if (board[r][c] == CellValue.EMPTY) {
            board[r][c] = game.getCurrentPlayer();
            rotateRing(game);
            switchCurrentPlayer(game);
            checkWinner(game);
        }

        return game;
    }

    private void switchCurrentPlayer(Game game) {
        if (game.getCurrentPlayer() == CellValue.B) {
            game.setCurrentPlayer(CellValue.W);
        } else {
            game.setCurrentPlayer(CellValue.B);
        }
    }

    private void rotateRing(Game game) {
        rotateOuterRing(game);
        rotateInnerRing(game);
    }

    private void rotateOuterRing(Game game) {
        CellValue[][] board = game.getBoard();
        int[][] positions = {
                {0, 0}, {0, 1}, {0, 2}, {0, 3},
                {1, 3}, {2, 3}, {3, 3}, {3, 2},
                {3, 1}, {3, 0}, {2, 0}, {1, 0}
        };

        CellValue first = board[positions[0][0]][positions[0][1]];
        for (int i = 0; i < positions.length - 1; i++) {
            int curR = positions[i][0];
            int curC = positions[i][1];
            int nextR = positions[i + 1][0];
            int nextC = positions[i + 1][1];
            board[curR][curC] = board[nextR][nextC];
        }
        int lastR = positions[positions.length - 1][0];
        int lastC = positions[positions.length - 1][1];
        board[lastR][lastC] = first;
    }

    private void rotateInnerRing(Game game) {
        CellValue[][] board = game.getBoard();
        int[][] positions = {
                {1, 1}, {1, 2}, {2, 2}, {2, 1}
        };

        CellValue first = board[positions[0][0]][positions[0][1]];
        for (int i = 0; i < positions.length - 1; i++) {
            int curR = positions[i][0];
            int curC = positions[i][1];
            int nextR = positions[i + 1][0];
            int nextC = positions[i + 1][1];
            board[curR][curC] = board[nextR][nextC];
        }
        int lastR = positions[positions.length - 1][0];
        int lastC = positions[positions.length - 1][1];
        board[lastR][lastC] = first;
    }

    private void checkWinner(Game game) {
        CellValue[][] board = game.getBoard();

        // Check rows
        for (int r = 0; r < 4; r++) {
            if (board[r][0] != CellValue.EMPTY &&
                    board[r][0] == board[r][1] &&
                    board[r][1] == board[r][2] &&
                    board[r][2] == board[r][3]) {
                handleWin(game, board[r][0]);
                return;
            }
        }

        // Check columns
        for (int c = 0; c < 4; c++) {
            if (board[0][c] != CellValue.EMPTY &&
                    board[0][c] == board[1][c] &&
                    board[1][c] == board[2][c] &&
                    board[2][c] == board[3][c]) {
                handleWin(game, board[0][c]);
                return;
            }
        }

        // Check main diagonal
        if (board[0][0] != CellValue.EMPTY &&
                board[0][0] == board[1][1] &&
                board[1][1] == board[2][2] &&
                board[2][2] == board[3][3]) {
            handleWin(game, board[0][0]);
            return;
        }

        // Check anti-diagonal
        if (board[0][3] != CellValue.EMPTY &&
                board[0][3] == board[1][2] &&
                board[1][2] == board[2][1] &&
                board[2][1] == board[3][0]) {
            handleWin(game, board[0][3]);
            return;
        }

        // Check tie
        boolean isFull = true;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (board[r][c] == CellValue.EMPTY) {
                    isFull = false;
                    break;
                }
            }
            if (!isFull) break;
        }
        if (isFull) {
            game.setGameOver(true);
            game.setWinner(null); // TIE
        }
    }

    private void handleWin(Game game, CellValue winner) {
        game.setWinner(winner);
        game.setGameOver(true);
        if (winner == CellValue.B) {
            game.setBlackScore(game.getBlackScore() + 1);
        } else if (winner == CellValue.W) {
            game.setWhiteScore(game.getWhiteScore() + 1);
        }
    }

    public Game resetBoard(String roomId, boolean clearScore) {
        Game game = rooms.get(roomId);
        if (game == null) return null;
        game.resetBoard(clearScore);
        return game;
    }
}