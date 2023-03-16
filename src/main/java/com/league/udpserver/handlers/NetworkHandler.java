package com.league.udpserver.handlers;

import com.serializers.BasicSerializer;
import com.serializers.SerializableGameStateDecorator;
import com.serializers.SerializableHeroEntity;
import lombok.Data;

import com.serializers.SerializableGameState;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.context.ApplicationContext;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

@Data
public class NetworkHandler {
   private DatagramSocket serverSocket;
   private DatagramPacket incomingDatagramPacket;
   private DatagramPacket outgoingDatagramPacket;
   private InetAddress serverIpAddress;
   private static final double TIME_THRESHOLD_SECONDS = 10.0;


   private byte[] incomingDatagramPacketBuffer = new byte[16000];
   private byte[] outgoingDatagramPacketBuffer = new byte[16000];

   public static final int CLIENT_PORT = 8085;
   public static final int SERVER_PORT = 8086;

   private Map<String, String> mappedJsonString;
   private SerializableGameState gameState;

   private HashMap<String, Long> connectedPlayers;

   private ApplicationContext applicationContext;
   private JSONParser jsonParser;
   public NetworkHandler (SerializableGameState gameState) {
       this.gameState = gameState;
       connectedPlayers = new HashMap<>();
       try {
           serverSocket = new DatagramSocket(SERVER_PORT);
           serverIpAddress = InetAddress.getByName("127.0.0.1");
           incomingDatagramPacket = new DatagramPacket(incomingDatagramPacketBuffer, incomingDatagramPacketBuffer.length);
           outgoingDatagramPacket = new DatagramPacket(outgoingDatagramPacketBuffer, outgoingDatagramPacketBuffer.length);
           outgoingDatagramPacketBuffer = "received".getBytes();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

   public void listen() {
       String incomingString;
       while (true) {
           try {
               serverSocket.receive(incomingDatagramPacket);
               incomingString = new String(incomingDatagramPacket.getData(), 0, incomingDatagramPacket.getLength());
               jsonParser = new JSONParser(incomingString);
               mappedJsonString = (Map<String, String>) jsonParser.parse();
               if (mappedJsonString.containsKey("command")) {
                   String[] args = mappedJsonString.get("command").split("_");
                   InputHandler.handleInput(gameState, args);
               } else if(mappedJsonString.containsKey("createHero")) {
                   String[] args = mappedJsonString.get("createHero").split("_");
                   String playerId = args[1];
                   String heroName = args[0];
                   CreationHandler.handleCreation(gameState, playerId, heroName);
                   connectedPlayers.put(playerId, System.nanoTime());
                   SerializableGameStateDecorator serializableGameStateDecorator = new SerializableGameStateDecorator(new BasicSerializer());
//                   byte[] serializedData = serializableGameStateDecorator.serialize(gameState);
//                   byte[] compressedData = DatagramCompressor.compress(serializedData);
//                   outgoingDatagramPacketBuffer = compressedData;
                   outgoingDatagramPacketBuffer = serializableGameStateDecorator.serialize(gameState);
                   serverSocket.send(new DatagramPacket(outgoingDatagramPacketBuffer, outgoingDatagramPacketBuffer.length,
                       incomingDatagramPacket.getAddress(), incomingDatagramPacket.getPort()));
               }  else if (mappedJsonString.containsKey("getUpdate") && connectedPlayers.containsKey(mappedJsonString.get("getUpdate"))) {
                   UpdateHandler.handleUpdates(gameState, mappedJsonString.get("getUpdate"));
                   connectedPlayers.put(mappedJsonString.get("getUpdate"), System.nanoTime());
                   SerializableGameStateDecorator serializableGameStateDecorator = new SerializableGameStateDecorator(new BasicSerializer());
                   outgoingDatagramPacketBuffer = serializableGameStateDecorator.serialize(gameState);
                   serverSocket.send(new DatagramPacket(outgoingDatagramPacketBuffer, outgoingDatagramPacketBuffer.length,
                           incomingDatagramPacket.getAddress(), incomingDatagramPacket.getPort()));
               } else {
                   System.out.println("it is not a command");
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
   }

    private void useHeartBeatToRemoveDisconnectedPlayers(SerializableGameState gameState) {
        if (connectedPlayers != null) {
            long currentTime = System.currentTimeMillis();
            connectedPlayers.forEach((playerId, timeToLive) -> {
                double deltaTime = (double)(currentTime - timeToLive) / 1000.0;
                System.out.println(deltaTime);
                if (deltaTime > TIME_THRESHOLD_SECONDS) {
                    System.out.println("Player " + playerId + " disconnected.");
                    gameState.getConnectedPlayers().remove(playerId);
                }
            });
        }
    }
}
