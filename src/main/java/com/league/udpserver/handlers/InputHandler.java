package com.league.udpserver.handlers;

import com.serializers.SerializableGameState;
import com.serializers.SerializableHeroEntity;

/**
 The InputHandler class handles the input received from the player and updates the game state accordingly.
 */
public class InputHandler {
    private static final int MOVESPEED = 5;
    private static final int BOUNDARY_LEFT = 0;
    private static final int BOUNDARY_RIGHT = 3600;

    /**
     Handles the input received from the player and updates the game state accordingly.
     @param gameState SerializableGameState Object that has the entire gamestate, can be retrieved via playerId which is inside the args array.
     @param args The input received from the player, such as moving and attacking commands.
     */
    public static void handleInput(SerializableGameState gameState, String[] args) {
        String playerId = args[1];
        String command = args[0];
        SerializableHeroEntity playerEntity = gameState.getConnectedPlayers().get(playerId);
        if (playerEntity.isAttacking()) {
            return;
        }
        switch (command) {
            case "left":
                movePlayerLeft(playerEntity);
                break;
            case "right":
                movePlayerRight(playerEntity);
                break;
            case "up":
                initiateJump(playerEntity);
                break;
            case "skill1":
                useSkill1(playerEntity);
                break;
        }
    }

    /**
     * Moves the player to the left, updating the player's position and facing direction.
     * The player's position will not go beyond the left boundary.
     * @param playerEntity represents the shared serializable player state object that can be passed between server and client.
     */
    private static void movePlayerLeft(SerializableHeroEntity playerEntity) {
        playerEntity.setFacingDirection("left");
        int newXPos = playerEntity.getXPos() - MOVESPEED;
        playerEntity.setXPos(Math.max(BOUNDARY_LEFT, newXPos));
        playerEntity.setMoving(true);
        playerEntity.setMovingStart(System.nanoTime());
    }

    /**
     * Moves the player to the right, updating the player's position and facing direction.
     * The player's position will not go beyond the right boundary.
     * @param playerEntity represents the shared serializable player state object that can be passed between server and client.
     */
    private static void movePlayerRight(SerializableHeroEntity playerEntity) {
        playerEntity.setFacingDirection("right");
        int newXPos = playerEntity.getXPos() + MOVESPEED;
        playerEntity.setXPos(Math.min(BOUNDARY_RIGHT, newXPos));
        playerEntity.setMoving(true);
        playerEntity.setMovingStart(System.nanoTime());
    }

    /**
     * Initiates a jump for the player if they are not already jumping or falling.
     * @param playerEntity represents the shared serializable player state object that can be passed between server and client.
     */
    private static void initiateJump(SerializableHeroEntity playerEntity) {
        if (!playerEntity.isJumping() && !playerEntity.isFalling()) {
            playerEntity.setJumping(true);
        }
    }

    /**
     * Executes the player's first skill, updating their attacking state and setting the position of the ability.
     * The skill will only be executed if the player is not already attacking.
     * @param playerEntity represents the shared serializable player state object that can be passed between server and client.
     */
    private static void useSkill1(SerializableHeroEntity playerEntity) {
        if (!playerEntity.isAttacking()) {
            int yPos = playerEntity.getYPos();
            int xPos = playerEntity.getXPos();
            playerEntity.setAttacking(true);
            playerEntity.setMoving(false);
            playerEntity.getAbilities().get(0).setYPos(yPos);
            playerEntity.setAttackStart(System.nanoTime());

            if (playerEntity.getFacingDirection().equals("left")) {
                playerEntity.getAbilities().get(0).setXPos(xPos - 200);
            } else if (playerEntity.getFacingDirection().equals("right")) {
                playerEntity.getAbilities().get(0).setXPos(xPos + 200);
            }
        }
    }
}
