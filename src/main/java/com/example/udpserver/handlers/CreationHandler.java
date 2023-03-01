package com.example.udpserver.handlers;

import com.example.udpserver.enums.FacingDirection;
import com.example.udpserver.models.GameState;
import com.example.udpserver.models.HeroEntity;

import java.util.ArrayList;
import java.util.Map;

public class CreationHandler {
    public static void handleCreation(GameState gameState, String playerId, Map<String, String> args) {
        if (!gameState.getConnectedPlayers().containsKey(playerId)) {
            args.get("createHero");
            HeroEntity newHero = HeroEntity.builder().id(playerId).heroName(args.get("createHero")).build();
            newHero.setHealth(1000);
            newHero.setXPos(0);
            newHero.setYPos(0);
            newHero.setWidth(100);
            newHero.setHeight(200);
            newHero.setMoving(false);
            newHero.setAttacking(false);
            newHero.setFacingDirection(FacingDirection.NONE);
            newHero.setMovingEnd(0);
            newHero.setFalling(false);
            newHero.setJumping(false);
            newHero.setMovingStart(0);
            newHero.setMovingEnd(0);
            if (args.get("createHero").equals("pumpkin")) {
                newHero.setAbilities(new ArrayList<>());
            } else if (args.get("createHero").equals("reaper")) {
                newHero.setAbilities(new ArrayList<>());
            }
            gameState.getConnectedPlayers().put(playerId, newHero);
        }
    }
}
