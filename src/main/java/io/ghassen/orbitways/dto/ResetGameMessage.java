package io.ghassen.orbitways.dto;

import lombok.Data;

@Data
public class ResetGameMessage {
    private String roomId;
    private boolean fullReset;

    public ResetGameMessage() {
    }

    public ResetGameMessage(String roomId, boolean fullReset) {
        this.roomId = roomId;
        this.fullReset = fullReset;
    }
}
