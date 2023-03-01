package com.example.udpserver.handlers;

import com.serializers.SerializableGameState;


public class InputHandler {
    private final int JUMP_CONSTANT = 10;
    private final int MAX_JUMP_HEIGHT = 100;
    private final int GROUND_Y_POSITION = 0;
    private final int JUMPS_PER_SECOND = 2;

    private static final int MOVESPEED = 5;

    public static void handleInput(SerializableGameState gameState, String[] args) {
        String playerId = args[1];
        String command = args[0];
        if (command.equals("left") && !gameState.getConnectedPlayers().get(playerId).isAttacking()) {
            gameState.getConnectedPlayers().get(playerId).setFacingDirection("left");
            int xPos = gameState.getConnectedPlayers().get(playerId).getXPos();
            if (xPos > 0) {
               xPos -= MOVESPEED;
               gameState.getConnectedPlayers().get(playerId).setXPos(xPos);
            } else {
                gameState.getConnectedPlayers().get(playerId).setXPos(0);
            }
            gameState.getConnectedPlayers().get(playerId).setMoving(true);
            gameState.getConnectedPlayers().get(playerId).setMovingStart(System.nanoTime());
        } else if (command.equals("right") && !gameState.getConnectedPlayers().get(playerId).isAttacking()) {
            gameState.getConnectedPlayers().get(playerId).setFacingDirection("right");
            int xPos = gameState.getConnectedPlayers().get(playerId).getXPos();
            if (xPos < 3600) {
                xPos += MOVESPEED;
                gameState.getConnectedPlayers().get(playerId).setXPos(xPos);
            } else {
                gameState.getConnectedPlayers().get(playerId).setXPos(3600);
            }
            gameState.getConnectedPlayers().get(playerId).setMoving(true);
            gameState.getConnectedPlayers().get(playerId).setMovingStart(System.nanoTime());
        } else if (command.equals("up") && !gameState.getConnectedPlayers().get(playerId).isAttacking() &&
        !gameState.getConnectedPlayers().get(playerId).isJumping() && !gameState.getConnectedPlayers().get(playerId).isFalling()) {
            gameState.getConnectedPlayers().get(playerId).setJumping(true);
        } else if (command.equals("skill1")) {
            System.out.println("is attacking");
            int yPos = gameState.getConnectedPlayers().get(playerId).getYPos();
            int xPos = gameState.getConnectedPlayers().get(playerId).getXPos();
            gameState.getConnectedPlayers().get(playerId).setAttacking(true);
            gameState.getConnectedPlayers().get(playerId).setMoving(false);
            gameState.getConnectedPlayers().get(playerId).getAbilities().get(0).setYPos(yPos);
            if (gameState.getConnectedPlayers().get(playerId).getFacingDirection().equals("left")) {
                gameState.getConnectedPlayers().get(playerId).getAbilities().get(0).setXPos(xPos - 200);
            } else if (gameState.getConnectedPlayers().get(playerId).getFacingDirection().equals("right")) {
                gameState.getConnectedPlayers().get(playerId).getAbilities().get(0).setXPos(xPos + 200);
            }
        }
    }



}
