package io.ghassen.orbitways.dto;

import lombok.Data;

@Data
public class PlaceMarbleMessage {
    private String boardclientId;
    private String roomId;
    private String userId;
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
