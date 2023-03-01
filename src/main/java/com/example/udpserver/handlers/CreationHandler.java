package com.example.udpserver.handlers;

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
        if (!gameState.getConnectedPlayers().containsKey(playerId)) {
            SerializableHeroEntity newHero = SerializableHeroEntity.builder().id(playerId).heroName(heroName).build();
            newHero.setHealth(1000);
            newHero.setXPos(0);
            newHero.setYPos(0);
            newHero.setWidth(100);
            newHero.setHeight(200);
            newHero.setMoving(false);
            newHero.setAttacking(false);
            newHero.setFacingDirection("none");
            newHero.setMovingEnd(0);
            newHero.setFalling(false);
            newHero.setJumping(false);
            newHero.setMovingStart(0);
            newHero.setMovingEnd(0);
            if (heroName.equals(PUMPKIN_NAME)) {
                newHero.setAbilities(createAbilities(PUMPKIN_NAME));
            } else if (heroName.equals(REAPER_NAME)) {
                newHero.setAbilities(createAbilities(REAPER_NAME));
            }
            gameState.getConnectedPlayers().put(playerId, newHero);
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
