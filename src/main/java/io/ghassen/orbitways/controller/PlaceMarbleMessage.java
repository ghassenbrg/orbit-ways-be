package io.ghassen.orbitways.controller;

import lombok.Data;

@Data
public class PlaceMarbleMessage {
    private String roomId;
    private String userId; // <--- new
    private int row;
    private int col;

    public PlaceMarbleMessage() {
    }

    public PlaceMarbleMessage(String roomId, String userId, int row, int col) {
        this.roomId = roomId;
        this.userId = userId;
        this.row = row;
        this.col = col;
    }
}