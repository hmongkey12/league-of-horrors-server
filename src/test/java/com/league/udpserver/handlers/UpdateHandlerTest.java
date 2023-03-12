package com.league.udpserver.handlers;
import com.serializers.SerializableAbilityEntity;
import com.serializers.SerializableGameState;
import com.serializers.SerializableHeroEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@RunWith(MockitoJUnitRunner.class)
public class UpdateHandlerTest {

    final int JUMP_CONSTANT = 10;
    final int MAX_JUMP_HEIGHT = 100;
    final int JUMPS_PER_SECOND = 2;
    final int ZERO = 0;
    final int FIRST_ENTITY_X_POS = 0;
    final int FIRST_ENTITY_Y_POS = 0;
    final int FIRST_ENTITY_WIDTH = 10;
    final int FIRST_ENTITY_HEIGHT = 10;
    final int SECOND_ENTITY_X_POS = 5;
    final int SECOND_ENTITY_Y_POS = 5;
    final int SECOND_ENTITY_WIDTH = 10;
    final int SECOND_ENTITY_HEIGHT = 10;
    final int SECOND_ENTITY_X_POS_OUT_OF_RANGE = 20;
    final long MAX_ATTACK_DURATION  = 6000000000L;
    final long MAX_MOVE_DURATION = 1000000000L;
    @Mock
    private SerializableGameState gameState;
    @Mock
    private SerializableHeroEntity playerEntity;
    private UpdateHandler updateHandler;

    private final String playerId = "player1";

    @Before
    public void setUp() {
        updateHandler = new UpdateHandler();
        Map<String, SerializableHeroEntity> connectedPlayers = new HashMap<>();
        connectedPlayers.put(playerId, playerEntity);
        when(gameState.getConnectedPlayers()).thenReturn(connectedPlayers);
    }

    @Test
    public void doEntitiesCollide_shouldReturnTrue_whenEntitiesOverlapAndFalseWhenTheyDont() throws Exception {
        SerializableHeroEntity firstEntity = mock(SerializableHeroEntity.class);
        SerializableAbilityEntity secondEntity = mock(SerializableAbilityEntity.class);

        // setup
        when(firstEntity.getXPos()).thenReturn(FIRST_ENTITY_X_POS);
        when(firstEntity.getYPos()).thenReturn(FIRST_ENTITY_Y_POS);
        when(firstEntity.getWidth()).thenReturn(FIRST_ENTITY_WIDTH);
        when(firstEntity.getHeight()).thenReturn(FIRST_ENTITY_HEIGHT);
        when(secondEntity.getXPos()).thenReturn(SECOND_ENTITY_X_POS);
        when(secondEntity.getYPos()).thenReturn(SECOND_ENTITY_Y_POS);
        when(secondEntity.getWidth()).thenReturn(SECOND_ENTITY_WIDTH);
        when(secondEntity.getHeight()).thenReturn(SECOND_ENTITY_HEIGHT);

        // trigger: doEntitiesCollide using reflection, special case, since doEntitiesCollide is private
        Method doEntitiesCollideMethod = UpdateHandler.class.getDeclaredMethod("doEntitiesCollide", SerializableHeroEntity.class, SerializableAbilityEntity.class);
        doEntitiesCollideMethod.setAccessible(true);
        boolean result = (boolean) doEntitiesCollideMethod.invoke(null, firstEntity, secondEntity);

        // verify: entities overlap
        assertTrue(result);

        // move secondEntity out of range and verify
        when(secondEntity.getXPos()).thenReturn(SECOND_ENTITY_X_POS_OUT_OF_RANGE);
        result = (boolean) doEntitiesCollideMethod.invoke(null, firstEntity, secondEntity);
        assertFalse(result);
    }

    @Test
    public void player_shouldStopAttacking_afterElapsedTimeHasPassed() {
        // Setup: player is attacking
        when(playerEntity.isAttacking()).thenReturn(true);
        when(playerEntity.getAttackStart()).thenReturn((float) (System.nanoTime() - MAX_ATTACK_DURATION));

        // Trigger: Call update handler method to simulate the player not attacking anymore
        updateHandler.handleUpdates(gameState, playerId);

        // Verify: that the player entity is no longer attacking
        verify(playerEntity).setAttacking(false);
        verify(playerEntity).setAttackStart(ZERO);
        verify(playerEntity).setAttackEnd(ZERO);
    }

    @Test
    public void player_ShouldStopMoving_afterElapsedTimeHasPassed() {

        // Setup: player is jumping
        when(playerEntity.isAttacking()).thenReturn(false);
        when(playerEntity.getAttackStart()).thenReturn((float) ZERO);
        when(playerEntity.isMoving()).thenReturn(true);
        when(playerEntity.getMovingStart()).thenReturn((float) (System.nanoTime() - MAX_MOVE_DURATION));

        // Trigger
        updateHandler.handleUpdates(gameState, playerId);

        // Verify: that the player entity is no longer moving
        verify(playerEntity).setMoving(false);
        verify(playerEntity).setMovingStart(ZERO);
        verify(playerEntity).setMovingEnd(ZERO);
    }


    @Test
    public void playerYPos_shouldIncrease_whenJumpingAndNotAboveMax() {
        // Setup: player is jumping and elapsed jumping time is greater than or equal to JUMPS_PER_SECOND
        when(playerEntity.isJumping()).thenReturn(true);
        when(playerEntity.getJumpStart()).thenReturn((float) ZERO);
        when(playerEntity.getYPos()).thenReturn(MAX_JUMP_HEIGHT - 1);

        // Trigger: Call update handler method to simulate the player jumping and updating their y position
        updateHandler.handleUpdates(gameState, playerId);

        // Verify: that the player entity's y position is updated by JUMP_CONSTANT
        verify(playerEntity).setYPos(MAX_JUMP_HEIGHT - 1 + JUMP_CONSTANT);
    }
}
