package com.league.udpserver.handlers;

import com.serializers.SerializableAbilityEntity;
import com.serializers.SerializableGameState;
import com.serializers.SerializableHeroEntity;


public class UpdateHandler {
    private static final int JUMP_CONSTANT = 10;
    private static final int MAX_JUMP_HEIGHT = 100;
    private static final int GROUND_Y_POSITION = 0;
    private static final float JUMPS_PER_SECOND = 2.0f;
    private static final float MAX_ATTACK_DURATION = 2.0f;
    private static final float MAX_MOVEMENT_DURATION = 0.5f;
    private static final float CONVERT_TO_SECONDS_CONSTANT = 1000000000.0f;

    /**
     Updates the game state based on the actions and events happening in the game for a specific player identified by the given playerId. The method updates the player's state including their attacking, moving, and jumping properties based on the elapsed time since their last action. It also checks for collisions with other players' abilities and decrements health points accordingly.
     @param gameState the current state of the game represented as a SerializableGameState object.
     @param playerId the ID of the player whose state is being updated.
     @throws NullPointerException if the gameState is null or if the playerId is not present in the gameState's connected players map.
     */
    public static void handleUpdates(SerializableGameState gameState, String playerId) {
        SerializableHeroEntity playerEntity = gameState.getConnectedPlayers().get(playerId);
        float currentTime = System.nanoTime();
        float attackStart = playerEntity.getAttackStart();
        float jumpStart = playerEntity.getJumpStart();
        float movingStart = playerEntity.getMovingStart();
        float elapsedAttackTime = (currentTime - attackStart) / CONVERT_TO_SECONDS_CONSTANT;
        float elapsedMovingTime = (currentTime - movingStart) / CONVERT_TO_SECONDS_CONSTANT;
        float elapsedJumpingTime = (currentTime - jumpStart) / CONVERT_TO_SECONDS_CONSTANT;

        handleAttackLogic(gameState, playerEntity, elapsedAttackTime);
        handleMovementLogic(playerEntity, elapsedMovingTime);
        updatePlayerEntityPosition(playerEntity, elapsedJumpingTime);
    }


    /**
     Checks whether two entities collide or not based on their position and size.
     The method compares the X and Y coordinates of the two entities, as well as
     their width and height to see if they overlap or not.
     @param firstEntity the first entity to be compared
     @param secondEntity the second entity to be compared
     @return true if the entities overlap, false otherwise
     */
    private static boolean doEntitiesCollide(SerializableHeroEntity firstEntity, SerializableAbilityEntity secondEntity) {
        int firstEntityXPosEnd = firstEntity.getXPos() + firstEntity.getWidth();
        int secondEntityXPosEnd = secondEntity.getXPos() + secondEntity.getWidth();
        int firstEntityYPosEnd = firstEntity.getYPos() + firstEntity.getHeight();
        int secondEntityYPosEnd = secondEntity.getYPos() + secondEntity.getHeight();
        return !(secondEntity.getXPos() > firstEntityXPosEnd ||
                secondEntityXPosEnd < firstEntity.getXPos() ||
                firstEntityYPosEnd < secondEntity.getYPos() ||
                firstEntity.getYPos() > secondEntityYPosEnd);
    }

    /**
     * Updates the player's position based on their current jumping or falling state.
     * If the player is jumping, they will reach a certain height where they can no longer go beyond.
     * If they are falling, they will reach a certain ground level that they can no longer go below.
     * @param playerEntity represents the shared serializable player state object that can be passed between server and client
     * @param elapsedJumpingTime the duration of the player's jump in seconds
     */
    private static void updatePlayerEntityPosition(SerializableHeroEntity playerEntity, double elapsedJumpingTime) {
        if (elapsedJumpingTime >= JUMPS_PER_SECOND) {
            if (playerEntity.isJumping()) {
                handleJumping(playerEntity);
            } else if (playerEntity.isFalling()) {
                handleFalling(playerEntity);
            }
        }
    }

    /**
     * Handles the player's jumping state and updates their vertical position accordingly.
     * When the player reaches the maximum jump height, they will stop jumping and start falling.
     * @param playerEntity represents the shared serializable player state object that can be passed between server and client
     */
    private static void handleJumping(SerializableHeroEntity playerEntity) {
        int yPos = playerEntity.getYPos();
        yPos = (yPos < MAX_JUMP_HEIGHT) ? yPos + JUMP_CONSTANT : yPos;
        playerEntity.setYPos(yPos);
        if (yPos >= MAX_JUMP_HEIGHT) {
            playerEntity.setJumping(false);
            playerEntity.setFalling(true);
        }
    }

    /**
     * Handles the player's falling state and updates their vertical position accordingly.
     * When the player reaches the ground level, they will stop falling.
     * @param playerEntity represents the shared serializable player state object that can be passed between server and client
     */
    private static void handleFalling(SerializableHeroEntity playerEntity) {
        int yPos = playerEntity.getYPos();
        yPos = (yPos > GROUND_Y_POSITION) ? yPos - JUMP_CONSTANT : GROUND_Y_POSITION;
        playerEntity.setYPos(yPos);
        playerEntity.setFalling(yPos != GROUND_Y_POSITION);
    }

    /**
     * Handles the player's attack state and updates their attack state.
     * If the player is attacking, it checks for collisions with other player's abilities and reduces health points
     * @param gameState the current state of the game represented as a SerializableGameState object
     * @param playerEntity represents the shared serializable player state object that can be passed between server and client
     * @param elapsedAttackTime the duration of the player's attack in seconds
     */
    private static void handleAttackLogic(SerializableGameState gameState, SerializableHeroEntity playerEntity, float elapsedAttackTime) {
        if (playerEntity.isAttacking() && elapsedAttackTime >= MAX_ATTACK_DURATION) {
            playerEntity.setAttacking(false);
            playerEntity.setAttackStart(0);
            playerEntity.setAttackEnd(0);
        } else if (playerEntity.isAttacking()) {
            checkForCollision(gameState, playerEntity);
        }
    }

    /**
     * Checks for collisions with other players' abilities and decrements health points accordingly.
     * @param gameState the current state of the game represented as a SerializableGameState object
     * @param playerEntity represents the shared serializable player state object that can be passed between server and client
     */
    private static void checkForCollision(SerializableGameState gameState, SerializableHeroEntity playerEntity) {
        gameState.getConnectedPlayers().values().stream()
                .filter(serializableHeroEntity -> !serializableHeroEntity.getId().equals(playerEntity.getId()))
                .forEach(serializableHeroEntity -> {
                    playerEntity.getAbilities().forEach(serializableAbilityEntity -> {
                        if (doEntitiesCollide(serializableHeroEntity, serializableAbilityEntity)) {
                            int health = serializableHeroEntity.getHealth();
                            health -= 1;
                            serializableHeroEntity.setHealth(health);
                        }
                    });
                });
    }

    /**
     * Handles the player's movement state and updates their position
     * @param playerEntity represents the shared serializable player state object that can be passed between server and client
     * @param elapsedMovingTime the duration of the player's movement in seconds
     */
    private static void handleMovementLogic(SerializableHeroEntity playerEntity, float elapsedMovingTime) {
        if (playerEntity.isMoving() && elapsedMovingTime >= MAX_MOVEMENT_DURATION) {
            playerEntity.setMoving(false);
            playerEntity.setMovingStart(0);
            playerEntity.setMovingEnd(0);
        }
    }
}
