package com.serializers;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Component
public class SerializableGameState implements Serializable {
    Map<String, SerializableHeroEntity> connectedPlayers;
    public SerializableGameState() {
       this.connectedPlayers = new HashMap<String, SerializableHeroEntity> ();
    }
}
