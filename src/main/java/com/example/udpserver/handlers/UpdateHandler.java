package com.example.udpserver.handlers;

import com.serializers.SerializableAbilityEntity;
import com.serializers.SerializableGameState;
import com.serializers.SerializableHeroEntity;


public class UpdateHandler {
    private static final int JUMP_CONSTANT = 10;
    private static final int MAX_JUMP_HEIGHT = 100;
    private static final int GROUND_Y_POSITION = 0;
    private static final int JUMPS_PER_SECOND = 2;

    public static void handleUpdates(SerializableGameState gameState, String playerId) {
        float attackStart = gameState.getConnectedPlayers().get(playerId).getAttackStart();
        float jumpStart = gameState.getConnectedPlayers().get(playerId).getJumpStart();
        float attackEnd = System.nanoTime();
        float movingEnd = attackEnd;
        float jumpEnd = System.nanoTime();
        float movingStart = gameState.getConnectedPlayers().get(playerId).getMovingStart();
        float elapsedAttackTime = (float) Math.floor((attackEnd - attackStart) / 1000);
        float elapsedMovingTime = (float) Math.floor((movingEnd - movingStart) / 1000);
        float elapsedJumpingTime = (float) Math.floor((jumpEnd - jumpStart) / 1000);


        if (gameState.getConnectedPlayers().get(playerId).isAttacking() && elapsedAttackTime >= 5) {
            gameState.getConnectedPlayers().get(playerId).setAttacking(false);
            gameState.getConnectedPlayers().get(playerId).setAttackStart(0);
            gameState.getConnectedPlayers().get(playerId).setAttackEnd(0);
        } else if (gameState.getConnectedPlayers().get(playerId).isAttacking()) {
            gameState.getConnectedPlayers().forEach(((s, serializableHeroEntity) -> {
                if (!serializableHeroEntity.getId().equals(playerId)) {
                    gameState.getConnectedPlayers().get(playerId).getAbilities().forEach(serializableAbilityEntity -> {
                        if (doEntitiesCollide(serializableHeroEntity, serializableAbilityEntity)) {
                            int health = serializableHeroEntity.getHealth();
                            health -= 1;
                            serializableHeroEntity.setHealth(health);
                        }
                    });
                }
            }));
        }

        if (gameState.getConnectedPlayers().get(playerId).isMoving() && elapsedMovingTime >= .5) {
            gameState.getConnectedPlayers().get(playerId).setMoving(false);
            gameState.getConnectedPlayers().get(playerId).setMovingStart(0);
            gameState.getConnectedPlayers().get(playerId).setMovingEnd(0);
        }

        if (elapsedJumpingTime >= JUMPS_PER_SECOND) {
            int yPos = gameState.getConnectedPlayers().get(playerId).getYPos();
            if (gameState.getConnectedPlayers().get(playerId).isJumping()) {
                if (gameState.getConnectedPlayers().get(playerId).getYPos() < MAX_JUMP_HEIGHT) {
                    yPos += JUMP_CONSTANT;
                    gameState.getConnectedPlayers().get(playerId).setYPos(yPos);
                } else {
                    gameState.getConnectedPlayers().get(playerId).setJumping(false);
                    gameState.getConnectedPlayers().get(playerId).setFalling(true);
                }
            } else if (gameState.getConnectedPlayers().get(playerId).isFalling()) {
                if (gameState.getConnectedPlayers().get(playerId).getYPos() > GROUND_Y_POSITION) {
                    yPos -= JUMP_CONSTANT;
                    gameState.getConnectedPlayers().get(playerId).setYPos(yPos);
                } else {
                    gameState.getConnectedPlayers().get(playerId).setYPos(GROUND_Y_POSITION);
                    gameState.getConnectedPlayers().get(playerId).setFalling(false);
                }
            }
        }
    }

    private static boolean doEntitiesCollide (SerializableHeroEntity firstEntity, SerializableAbilityEntity secondEntity) {
    int firstEntityXposEnd = firstEntity.getXPos() + firstEntity.getWidth();
    int secondEntityXposEnd = secondEntity.getXPos() + secondEntity.getWidth();
    int firstEntityYposEnd = firstEntity.getYPos() + firstEntity.getHeight();
    int secondEntityYposEnd = secondEntity.getYPos() + secondEntity.getHeight();
        if (secondEntity.getXPos() > firstEntityXposEnd) {
            return false;
        } else if (secondEntityXposEnd < firstEntity.getXPos()) {
            return false;
        } else if (firstEntityYposEnd < secondEntity.getYPos()){
            return false;
        } else if (firstEntity.getYPos() > secondEntityYposEnd) {
            return false;
        } else {
            return true;
        }
    }
}
