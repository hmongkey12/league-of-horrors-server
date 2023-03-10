package com.league.udpserver.handlers;

import com.serializers.SerializableAbilityEntity;
import com.serializers.SerializableGameState;
import com.serializers.SerializableHeroEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreationHandler {

    private static final String REAPER_NAME = "reaper";
    private static final String PUMPKIN_NAME = "pumpkin";
    private static final String ABILITY_ONE_SUFFIX = "_1";
    private static final String ABILITY_TWO_SUFFIX = "_2";

    public static void handleCreation(SerializableGameState gameState, String playerId, String heroName, Map<String, String> args) {
        SerializableHeroEntity playerEntity = gameState.getConnectedPlayers().getOrDefault(playerId, null);
        if (playerEntity == null) {
            playerEntity = SerializableHeroEntity.builder()
                    .id(playerId)
                    .heroName(heroName)
                    .health(1000)
                    .xPos(0)
                    .yPos(0)
                    .width(100)
                    .height(200)
                    .isMoving(false)
                    .isAttacking(false)
                    .facingDirection("none")
                    .movingStart(0)
                    .movingEnd(0)
                    .isFalling(false)
                    .isJumping(false)
                    .abilities(createAbilities(heroName))
                    .build();
            gameState.getConnectedPlayers().put(playerId, playerEntity);
        }
    }

    private static List<SerializableAbilityEntity> createAbilities(String abilityName) {
        List<SerializableAbilityEntity> abilityEntities = new ArrayList<>();
        SerializableAbilityEntity abilityEntity;
        if(abilityName.equals(REAPER_NAME)) {
            abilityEntity = SerializableAbilityEntity.builder().abilityName(REAPER_NAME + ABILITY_ONE_SUFFIX)
                    .abilityStart(0).abilityEnd(0).cooldownEnd(4).cooldownStart(0)
                    .xPos(0).yPos(0).width(100).height(200).damage(10).build();
            abilityEntities.add(abilityEntity);
            abilityEntity = SerializableAbilityEntity.builder().abilityName(REAPER_NAME + ABILITY_TWO_SUFFIX)
                    .abilityStart(0).abilityEnd(0).cooldownEnd(10).cooldownStart(0)
                    .xPos(0).yPos(0).width(100).height(200).damage(50).build();
            abilityEntities.add(abilityEntity);
        } else if(abilityName.equals(PUMPKIN_NAME)) {
            abilityEntity = SerializableAbilityEntity.builder().abilityName(PUMPKIN_NAME + ABILITY_ONE_SUFFIX)
                    .abilityStart(0).abilityEnd(0).cooldownEnd(4).cooldownStart(0)
                    .xPos(0).yPos(0).width(100).height(200).damage(10).build();
            abilityEntities.add(abilityEntity);
            abilityEntity = SerializableAbilityEntity.builder().abilityName(PUMPKIN_NAME + ABILITY_TWO_SUFFIX)
                    .abilityStart(0).abilityEnd(0).cooldownEnd(10).cooldownStart(0)
                    .xPos(0).yPos(0).width(100).height(200).damage(50).build();
            abilityEntities.add(abilityEntity);
        }
        return abilityEntities;
    }
}
