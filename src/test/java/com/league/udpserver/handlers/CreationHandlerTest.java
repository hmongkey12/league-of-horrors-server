package com.league.udpserver.handlers;


import static org.junit.Assert.*;
import com.serializers.SerializableGameState;
import com.serializers.SerializableHeroEntity;
import org.junit.Test;

public class CreationHandlerTest {

    @Test
    public void handleCreation_shouldCreateSerializableHeroEntity_whenHandleCreationIsCalled() {
        // Setup
        String playerId = "player1";
        String heroName = "reaper";
        SerializableGameState gameState = new SerializableGameState();

        // Trigger: call the method
        CreationHandler.handleCreation(gameState, playerId, heroName);

        // Verify: that gameState now has a player
        SerializableHeroEntity playerEntity = gameState.getConnectedPlayers().get(playerId);
        assertNotNull(playerEntity);

        // Verify: that the created SerializableHeroEntity has the correct properties
        assertEquals(playerId, playerEntity.getId());
        assertEquals(heroName, playerEntity.getHeroName());
        assertEquals(1000, playerEntity.getHealth());
        assertEquals(0, playerEntity.getXPos());
        assertEquals(0, playerEntity.getYPos());
        assertEquals(100, playerEntity.getWidth());
        assertEquals(200, playerEntity.getHeight());
        assertFalse(playerEntity.isMoving());
        assertFalse(playerEntity.isAttacking());
        assertEquals("none", playerEntity.getFacingDirection());
        assertFalse(playerEntity.isFalling());
        assertFalse(playerEntity.isJumping());
        assertEquals(2, playerEntity.getAbilities().size());

        // Verify: that the createAbilities() method was called
        assertEquals(2, playerEntity.getAbilities().size());
        assertEquals(heroName + "_1", playerEntity.getAbilities().get(0).getAbilityName());
        assertEquals(heroName + "_2", playerEntity.getAbilities().get(1).getAbilityName());
    }
}
