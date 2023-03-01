package com.example.udpserver.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
public class GameState {
    Map<String, HeroEntity> connectedPlayers;
    public GameState() {
       this.connectedPlayers = new HashMap<String, HeroEntity> ();
    }
}
