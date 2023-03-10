package com.league.udpserver;

import com.league.udpserver.handlers.NetworkHandler;
import com.serializers.SerializableGameState;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.league.udpserver", "com.serializers"})
public class UdpServerApplication {
    private static SerializableGameState gameState;
    public static void main(String[] args) {
        SpringApplication.run(UdpServerApplication.class, args);
        gameState = new SerializableGameState();
        NetworkHandler networkHandler = new NetworkHandler(gameState);
        networkHandler.listen();
    }
}
