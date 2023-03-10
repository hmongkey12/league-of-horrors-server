package com.league.udpserver.enums;

public enum FacingDirection {
    LEFT("left"), RIGHT("right"), NONE("none");

    private String direction;

    FacingDirection(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }
}
