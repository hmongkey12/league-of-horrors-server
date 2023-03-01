package com.example.udpserver.handlers;

import com.serializers.SerializableGameState;

import java.util.Map;

public class InputHandler {
    private final int JUMP_CONSTANT = 10;
    private final int MAX_JUMP_HEIGHT = 100;
    private final int GROUND_Y_POSITION = 0;
    private final int JUMPS_PER_SECOND = 2;

    private final int MOVESPEED = 5;

    public static void handleInput(SerializableGameState gameState, String playerId, Map<String, String> command) {
        if (command.values().contains("left")) {
            System.out.println(gameState.getConnectedPlayers().get(playerId).getHeroName());
        } else if (command.values().contains("right")) {
            System.out.println(gameState.getConnectedPlayers().get(playerId).getHeroName());
        } else if (command.values().contains("up")) {
            System.out.println(gameState.getConnectedPlayers().get(playerId).getHeroName());
        } else if (command.values().contains("down")) {
            System.out.println(gameState.getConnectedPlayers().get(playerId).getHeroName());
        } else if (command.values().contains("skill_1")) {
            System.out.println("skill_1");
        }
    }

}
