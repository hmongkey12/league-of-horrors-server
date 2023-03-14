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
        float attackEnd = currentTime;
        float movingEnd = currentTime;
        float jumpEnd = currentTime;
        float movingStart = playerEntity.getMovingStart();
        float elapsedAttackTime = (attackEnd - attackStart) / CONVERT_TO_SECONDS_CONSTANT;
        float elapsedMovingTime = (movingEnd - movingStart) / CONVERT_TO_SECONDS_CONSTANT;
        float elapsedJumpingTime = (jumpEnd - jumpStart) / CONVERT_TO_SECONDS_CONSTANT;

        if (playerEntity.isAttacking() && elapsedAttackTime >= MAX_ATTACK_DURATION) {
            playerEntity.setAttacking(false);
            playerEntity.setAttackStart(0);
            playerEntity.setAttackEnd(0);
        } else if (playerEntity.isAttacking()) {
            gameState.getConnectedPlayers().forEach(((s, serializableHeroEntity) -> {
                if (!serializableHeroEntity.getId().equals(playerId)) {
                    playerEntity.getAbilities().forEach(serializableAbilityEntity -> {
                        if (doEntitiesCollide(serializableHeroEntity, serializableAbilityEntity)) {
                            int health = serializableHeroEntity.getHealth();
                            health -= 1;
                            serializableHeroEntity.setHealth(health);
                        }
                    });
                }
            }));
        }

        if (playerEntity.isMoving() && elapsedMovingTime >= MAX_MOVEMENT_DURATION) {
            playerEntity.setMoving(false);
            playerEntity.setMovingStart(0);
            playerEntity.setMovingEnd(0);
        }

        if (elapsedJumpingTime >= JUMPS_PER_SECOND) {
            int yPos = playerEntity.getYPos();
            if (playerEntity.isJumping()) {
                if (yPos < MAX_JUMP_HEIGHT) {
                    yPos += JUMP_CONSTANT;
                    playerEntity.setYPos(yPos);
                } else {
                    playerEntity.setJumping(false);
                    playerEntity.setFalling(true);
                }
            } else if (playerEntity.isFalling()) {
                if (yPos > GROUND_Y_POSITION) {
                    yPos -= JUMP_CONSTANT;
                    playerEntity.setYPos(yPos);
                } else {
                    playerEntity.setYPos(GROUND_Y_POSITION);
                    playerEntity.setFalling(false);
                }
            }
        }
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
}
