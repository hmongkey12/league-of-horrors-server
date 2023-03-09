package com.example.udpserver.handlers;

import com.serializers.SerializableGameState;
import com.serializers.SerializableHeroEntity;

public class InputHandler {
    private static final int MOVESPEED = 5;
    private static final int BOUNDARY_LEFT = 0;
    private static final int BOUNDARY_RIGHT = 3600;

    public static void handleInput(SerializableGameState gameState, String[] args) {
        String playerId = args[1];
        String command = args[0];
        SerializableHeroEntity playerEntity = gameState.getConnectedPlayers().get(playerId);
        if (playerEntity.isAttacking()) {
            return;
        }
        switch (command) {
            case "left":
                playerEntity.setFacingDirection("left");
                int leftXPos = playerEntity.getXPos() - MOVESPEED;
                playerEntity.setXPos(leftXPos < BOUNDARY_LEFT ? BOUNDARY_LEFT : leftXPos);
                playerEntity.setMoving(true);
                playerEntity.setMovingStart(System.nanoTime());
                break;
            case "right":
                playerEntity.setFacingDirection("right");
                int rightXPos = playerEntity.getXPos() + MOVESPEED;
                playerEntity.setXPos(rightXPos > BOUNDARY_RIGHT ? BOUNDARY_RIGHT : rightXPos);
                playerEntity.setMoving(true);
                playerEntity.setMovingStart(System.nanoTime());
                break;
            case "up":
                if (!playerEntity.isJumping() && !playerEntity.isFalling()) {
                    playerEntity.setJumping(true);
                }
                break;
            case "skill1":
                int yPos = playerEntity.getYPos();
                int xPos = playerEntity.getXPos();
                playerEntity.setAttacking(true);
                playerEntity.setMoving(false);
                playerEntity.getAbilities().get(0).setYPos(yPos);
                if (playerEntity.getFacingDirection().equals("left")) {
                    playerEntity.getAbilities().get(0).setXPos(xPos - 200);
                } else if (playerEntity.getFacingDirection().equals("right")) {
                    playerEntity.getAbilities().get(0).setXPos(xPos + 200);
                }
                break;
        }
    }
}
