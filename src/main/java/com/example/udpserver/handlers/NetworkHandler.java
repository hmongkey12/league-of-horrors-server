package com.example.udpserver.handlers;

import com.example.udpserver.models.GameState;
import com.serializers.SerializedHero;
import lombok.Data;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.context.ApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

@Data
public class NetworkHandler {
   private DatagramSocket serverSocket;
   private DatagramPacket incomingDatagramPacket;
   private DatagramPacket outgoingDatagramPacket;
   private InetAddress serverIpAddress;

    private byte[] incomingDatagramPacketBuffer = new byte[1024];
   private byte[] outgoingDatagramPacketBuffer = new byte[1024];

   public static final int CLIENT_PORT = 8085;
   public static final int SERVER_PORT = 8086;

   private Map<String, String> mappedJsonString;
   private GameState gameState;

   private ApplicationContext applicationContext;
   private JSONParser jsonParser;
   public NetworkHandler (GameState gameState) {
       this.gameState = gameState;
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
                   InputHandler.handleInput(gameState, "1", mappedJsonString);
               } else if(mappedJsonString.containsKey("createHero")) {
                   String playerId = String.valueOf(incomingDatagramPacket.getPort());
                   CreationHandler.handleCreation(gameState, playerId, mappedJsonString);
                   SerializedHero outGoingSerializedHero = new SerializedHero( 12 ,"bob");
                   ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                   ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                   objectOutputStream.writeObject(outGoingSerializedHero);
                   outgoingDatagramPacketBuffer = byteArrayOutputStream.toByteArray();
                   objectOutputStream.close();
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
}
