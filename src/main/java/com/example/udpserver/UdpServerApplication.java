package com.example.udpserver;

import com.example.udpserver.config.GameConfig;
import com.example.udpserver.handlers.NetworkHandler;
import com.example.udpserver.models.GameState;
import com.example.udpserver.models.HeroEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class UdpServerApplication {
    private static GameState gameState;
    public static void main(String[] args) {
        SpringApplication.run(UdpServerApplication.class, args);
        gameState = new GameState();
        gameState.getConnectedPlayers().put("1", HeroEntity.builder().heroName("pumpkin").build());
        gameState.getConnectedPlayers().put("2", HeroEntity.builder().heroName("reaper").build());
        NetworkHandler networkHandler = new NetworkHandler(gameState);
        networkHandler.listen();
    }
}
