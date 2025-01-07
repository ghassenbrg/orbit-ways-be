package io.ghassen.orbitways.service;

import io.ghassen.orbitways.model.CellValue;
import io.ghassen.orbitways.model.Game;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GameService {
    private final Map<String, Game> rooms = new HashMap<>();

    public Game createGame(String roomId, String hostId, String hostColor, int maxScore) {
        Game game = new Game(roomId);
        game.setMaxScore(maxScore);  // set how many rounds to win

        // If host picks "B", we store host as Black.
        if (hostColor.equalsIgnoreCase("B")) {
            game.setPlayerBlack(hostId);
            game.setCurrentPlayer(CellValue.B);
        } else {
            game.setPlayerWhite(hostId);
            game.setCurrentPlayer(CellValue.W);
        }
        rooms.put(roomId, game);
        return game;
    }

    public Game getGame(String roomId) {
        return rooms.get(roomId);
    }

    /**
     * Join the game as either 'B' or 'W', if available.
     * If the chosen color is already taken, we try to assign the other color.
     * If both are taken, return null.
     */
    public Game joinGame(String roomId, String joinerId, String joinerColor) {
        if (!rooms.containsKey(roomId)) {
            return null; // or create a new one, up to you
        }
        Game game = rooms.get(roomId);

        // If already have 2 players, disallow further joining
        if (game.getPlayerBlack() != null && game.getPlayerWhite() != null) {
            return null; // room full
        }

        // If user wants black but black is taken
        if (joinerColor.equalsIgnoreCase("B")) {
            if (game.getPlayerBlack() != null) {
                // black is taken, so let's try white
                if (game.getPlayerWhite() == null) {
                    game.setPlayerWhite(joinerId);
                } else {
                    return null; // both taken
                }
            } else {
                // black was free
                game.setPlayerBlack(joinerId);
            }
        } else {
            // user wants white
            if (game.getPlayerWhite() != null) {
                // white taken => try black
                if (game.getPlayerBlack() == null) {
                    game.setPlayerBlack(joinerId);
                } else {
                    return null; // both taken
                }
            } else {
                game.setPlayerWhite(joinerId);
            }
        }

        return game;
    }

    /**
     * Join the game as either 'B' or 'W', if available.
     * If the chosen color is already taken, we try to assign the other color.
     * If both are taken, return null.
     */
    public Game getGame(String roomId, String joinerId) {
        if (!rooms.containsKey(roomId)) {
            return null;
        }
        Game game = rooms.get(roomId);

        String joinerColor = joinerId.equals(game.getPlayerBlack()) ? "B" : "W";

        return game;
    }

    public Game placeMarble(String roomId, String userId, int r, int c) {
        Game game = rooms.get(roomId);
        if (game == null || game.isMatchDone() || game.isGameOver()) {
            return game; // no changes if match is done or round is over
        }

        // Determine if user is black or white
        String blackId = game.getPlayerBlack();
        String whiteId = game.getPlayerWhite();

        CellValue playerColor = null;
        if (blackId != null && blackId.equals(userId)) {
            playerColor = CellValue.B;
        } else if (whiteId != null && whiteId.equals(userId)) {
            playerColor = CellValue.W;
        }
        if (playerColor == null) {
            return game; // user not recognized
        }
        if (playerColor != game.getCurrentPlayer()) {
            return game; // not your turn
        }

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
        if (game.isMatchDone()) {
            return; // do nothing if match is already done
        }

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

        // Check if we've hit maxScore => match is done
        if (game.getBlackScore() >= game.getMaxScore()) {
            game.setMatchDone(true);
            game.setFinalWinner(CellValue.B);
        } else if (game.getWhiteScore() >= game.getMaxScore()) {
            game.setMatchDone(true);
            game.setFinalWinner(CellValue.W);
        }
    }

    public Game resetBoard(String roomId, boolean clearScore) {
        Game game = rooms.get(roomId);
        if (game == null) return null;
        game.resetBoard(clearScore);
        return game;
    }

    public Game resetGame(String roomId, boolean fullReset) {
        Game game = rooms.get(roomId);
        if (game == null) return null;

        game.resetBoard(fullReset);

        return game;
    }
}
