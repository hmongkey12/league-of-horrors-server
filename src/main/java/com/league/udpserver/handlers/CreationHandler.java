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

    public static void handleCreation(SerializableGameState gameState, String playerId, String heroName) {
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

    /**
     * Creates a list of abilities for the specified hero.
     * Right now, it only supports the "reaper" and "pumpkin" heroes, each with two abilities.
     * Each ability has an associated suffix, either "_1" or "_2".
     * @param heroName The name of the hero for which the abilities are being created.
     * @return A list of SerializableAbilityEntity objects representing the hero's abilities.
     */
    private static List<SerializableAbilityEntity> createAbilities(String heroName) {
        List<SerializableAbilityEntity> abilityEntities = new ArrayList<>();

        if (REAPER_NAME.equals(heroName) || PUMPKIN_NAME.equals(heroName)) {
            for (int i = 1; i <= 2; i++) {
                String abilitySuffix = i == 1 ? ABILITY_ONE_SUFFIX : ABILITY_TWO_SUFFIX;
                abilityEntities.add(createAbility(heroName, abilitySuffix));
            }
        }

        return abilityEntities;
    }

    /**
     * Creates a single ability with the specified hero name and ability suffix.
     * @param heroName The name of the hero for which the ability is being created.
     * @param abilitySuffix The suffix of the ability being created, either "_1" or "_2".
     * @return A SerializableAbilityEntity object representing the created ability.
     */
    private static SerializableAbilityEntity createAbility(String heroName, String abilitySuffix) {
        int cooldownEnd = abilitySuffix.equals(ABILITY_ONE_SUFFIX) ? 4 : 10;
        int damage = abilitySuffix.equals(ABILITY_ONE_SUFFIX) ? 10 : 50;

        return SerializableAbilityEntity.builder()
                .abilityName(heroName + abilitySuffix)
                .abilityStart(0)
                .abilityEnd(0)
                .cooldownEnd(cooldownEnd)
                .cooldownStart(0)
                .xPos(0)
                .yPos(0)
                .width(100)
                .height(200)
                .damage(damage)
                .build();
    }
}
