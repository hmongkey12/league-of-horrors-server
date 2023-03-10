package com.league.udpserver.handlers;
import com.serializers.SerializableAbilityEntity;
import com.serializers.SerializableHeroEntity;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.lang.reflect.Method;

public class UpdateHandlerTest {

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

        // invoke doEntitiesCollide using reflection
        Method doEntitiesCollideMethod = UpdateHandler.class.getDeclaredMethod("doEntitiesCollide", SerializableHeroEntity.class, SerializableAbilityEntity.class);
        doEntitiesCollideMethod.setAccessible(true);
        boolean result = (boolean) doEntitiesCollideMethod.invoke(null, firstEntity, secondEntity);

        // test entities that overlap
        assertTrue(result);

        // move secondEntity out of range and test again
        when(secondEntity.getXPos()).thenReturn(20);
        result = (boolean) doEntitiesCollideMethod.invoke(null, firstEntity, secondEntity);
        assertFalse(result);
    }
}
