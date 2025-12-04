package dev.ashu16.oakcrates.models;

import java.util.UUID;

public class KeyData {

    private UUID playerUuid;
    private String crateId;
    private int amount;

    public KeyData(UUID playerUuid, String crateId, int amount) {
        this.playerUuid = playerUuid;
        this.crateId = crateId;
        this.amount = amount;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public String getCrateId() {
        return crateId;
    }

    public void setCrateId(String crateId) {
        this.crateId = crateId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(0, amount);
    }

    public void addKeys(int amount) {
        this.amount = Math.max(0, this.amount + amount);
    }

    public boolean takeKeys(int amount) {
        if (this.amount >= amount) {
            this.amount -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "KeyData{" +
                "playerUuid=" + playerUuid +
                ", crateId='" + crateId + '\'' +
                ", amount=" + amount +
                '}';
    }
}
