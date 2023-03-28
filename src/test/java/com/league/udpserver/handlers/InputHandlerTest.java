package com.league.udpserver.handlers;

import com.serializers.SerializableGameState;
import com.serializers.SerializableHeroEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InputHandlerTest {
    @Mock
    private SerializableGameState gameState;
    @Mock
    private SerializableHeroEntity playerEntity;

    private final String PLAYERID = "player1";

    @Before
    public void setUp() {
        Map<String, SerializableHeroEntity> connectedPlayers = new HashMap<>();
        connectedPlayers.put(PLAYERID, playerEntity);
        when(gameState.getConnectedPlayers()).thenReturn(connectedPlayers);
    }


    @Test
    public void handleInput_shouldSetLeftDirectionAndMove_whenLeftCommandReceived() {
        // Setup
        String[] args = {"left", PLAYERID};

        // Trigger
        InputHandler.handleInput(gameState, args);

        // Verify
        verify(playerEntity).setFacingDirection("left");
        verify(playerEntity).setXPos(anyInt());
        verify(playerEntity).setMoving(true);

        ArgumentCaptor<Float> floatCaptor = ArgumentCaptor.forClass(Float.class);
        verify(playerEntity).setMovingStart(floatCaptor.capture());
        assertTrue(floatCaptor.getValue() > 0);
    }

    @Test
    public void handleInput_shouldNotUpdatePlayer_whenPlayerIsAttacking() {
        // Setup
        String[] args = {"left", PLAYERID};
        when(playerEntity.isAttacking()).thenReturn(true);

        // Trigger
        InputHandler.handleInput(gameState, args);

        // Verify
        verify(playerEntity, never()).setFacingDirection(anyString());
        verify(playerEntity, never()).setXPos(anyInt());
        verify(playerEntity, never()).setMoving(anyBoolean());
    }
}
