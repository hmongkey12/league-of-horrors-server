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
    public void doEntitiesCollide_shouldReturnTrue_ifEntitiesOverlapAndFalseIfNot() throws Exception {
        SerializableHeroEntity firstEntity = mock(SerializableHeroEntity.class);
        SerializableAbilityEntity secondEntity = mock(SerializableAbilityEntity.class);

        // set up
        when(firstEntity.getXPos()).thenReturn(0);
        when(firstEntity.getYPos()).thenReturn(0);
        when(firstEntity.getWidth()).thenReturn(10);
        when(firstEntity.getHeight()).thenReturn(10);
        when(secondEntity.getXPos()).thenReturn(5);
        when(secondEntity.getYPos()).thenReturn(5);
        when(secondEntity.getWidth()).thenReturn(10);
        when(secondEntity.getHeight()).thenReturn(10);

        // execute doEntitiesCollide using reflection
        Method doEntitiesCollideMethod = UpdateHandler.class.getDeclaredMethod("doEntitiesCollide", SerializableHeroEntity.class, SerializableAbilityEntity.class);
        doEntitiesCollideMethod.setAccessible(true);
        boolean result = (boolean) doEntitiesCollideMethod.invoke(null, firstEntity, secondEntity);

        // verify entities overlap
        assertTrue(result);

        // move secondEntity out of range and verify
        when(secondEntity.getXPos()).thenReturn(20);
        result = (boolean) doEntitiesCollideMethod.invoke(null, firstEntity, secondEntity);
        assertFalse(result);
    }

    @Test
    public void playerShouldStopAttacking_afterACertainTimeHasPassed() {
        // Set up the player entity to be attacking
        when(playerEntity.isAttacking()).thenReturn(true);
        when(playerEntity.getAttackStart()).thenReturn((float) (System.nanoTime() - 6000000000L));

        // Call the update handler method to simulate the player not attacking anymore
        updateHandler.handleUpdates(gameState, playerId);

        // Verify that the player entity is no longer attacking
        verify(playerEntity).setAttacking(false);
        verify(playerEntity).setAttackStart(0);
        verify(playerEntity).setAttackEnd(0);
    }
}
