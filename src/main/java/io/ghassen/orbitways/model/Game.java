package io.ghassen.orbitways.model;

import io.ghassen.orbitways.dto.PlaceMarbleMessage;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Game {
    private final LocalDateTime creationDate;
    private String roomId;
    private CellValue[][] board;
    private CellValue currentPlayer;
    private boolean gameOver;
    private CellValue winner;
    private int blackScore;
    private int whiteScore;
    private PlaceMarbleMessage marbleMessage;

    // Track which user is “Black” and which user is “White”.
    private String playerBlack;
    private String playerWhite;

    // For a “best of X” scenario:
    private int maxScore;        // e.g. 3
    private boolean matchDone;   // if true, entire match is finished
    private CellValue finalWinner; // B or W if match is concluded
    private CellValue playerToStart;

    public Game(String roomId) {
        creationDate = LocalDateTime.now();
        this.roomId = roomId;
        this.board = new CellValue[4][4];
        this.maxScore = 3;
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

        if (clearScore) {
            this.blackScore = 0;
            this.whiteScore = 0;
            this.matchDone = false;
            this.finalWinner = null;
            this.playerToStart = CellValue.B;
        } else {
            this.playerToStart = CellValue.B.equals(this.playerToStart) ? CellValue.W : CellValue.B;
        }

        this.currentPlayer = this.playerToStart;

    }
}
