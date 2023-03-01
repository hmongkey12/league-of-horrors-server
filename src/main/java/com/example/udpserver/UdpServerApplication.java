package com.example.udpserver;

import com.example.udpserver.handlers.NetworkHandler;
import com.serializers.SerializableGameState;
import com.serializers.SerializableHeroEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UdpServerApplication {
    private static SerializableGameState gameState;
    public static void main(String[] args) {
        SpringApplication.run(UdpServerApplication.class, args);
        gameState = new SerializableGameState();
        gameState.getConnectedPlayers().put("1", SerializableHeroEntity.builder().heroName("pumpkin").build());
        gameState.getConnectedPlayers().put("2", SerializableHeroEntity.builder().heroName("reaper").build());
        NetworkHandler networkHandler = new NetworkHandler(gameState);
        networkHandler.listen();
    }
}
