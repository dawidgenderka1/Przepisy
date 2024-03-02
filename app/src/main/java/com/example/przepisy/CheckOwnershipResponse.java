package com.example.przepisy;

public class CheckOwnershipResponse {
    private boolean belongsToUser;

    public CheckOwnershipResponse() {
    }

    public CheckOwnershipResponse(boolean belongsToUser) {
        this.belongsToUser = belongsToUser;
    }

    public boolean isBelongsToUser() {
        return belongsToUser;
    }

    public void setBelongsToUser(boolean belongsToUser) {
        this.belongsToUser = belongsToUser;
    }
}

