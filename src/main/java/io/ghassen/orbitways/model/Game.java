package io.ghassen.orbitways.model;

import lombok.Data;

@Data
public class Game {
    private String roomId;
    private CellValue[][] board;
    private CellValue currentPlayer;
    private boolean gameOver;
    private CellValue winner;
    private int blackScore;
    private int whiteScore;

    // NEW: Track which user is “Black” and which user is “White”.
    // In a real system, you might store actual user IDs (like "user123"),
    // but we’ll store simple identifiers or nicknames for this demo.
    private String playerBlack;
    private String playerWhite;

    public Game(String roomId) {
        this.roomId = roomId;
        this.board = new CellValue[4][4];
        resetBoard(true);
    }

    public void resetBoard(boolean clearScore) {
        this.gameOver = false;
        this.winner = null;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                board[r][c] = CellValue.EMPTY;
            }
        }
        this.currentPlayer = CellValue.B;
        if (clearScore) {
            this.blackScore = 0;
            this.whiteScore = 0;
        }
    }

}