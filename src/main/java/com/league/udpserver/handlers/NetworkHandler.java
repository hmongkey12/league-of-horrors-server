package com.league.udpserver.handlers;

import com.serializers.BasicSerializer;
import com.serializers.SerializableGameStateDecorator;
import lombok.Data;

import com.serializers.SerializableGameState;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Data
public class NetworkHandler {
    private static final int CLIENT_PORT = 8085;
    private static final int SERVER_PORT = 8086;
    public static final double TIME_THRESHOLD_SECONDS = 10.0;
    private static final int INCOMING_BUFFER_SIZE = 16000;
    private static final int OUTGOING_BUFFER_SIZE = 16000;
    private static final String LOCALHOST_ADDRESS = "127.0.0.1";
    private static final String RECEIVED_MESSAGE = "received";

    private DatagramSocket serverSocket;
    private DatagramPacket incomingDatagramPacket;
    private DatagramPacket outgoingDatagramPacket;
    private InetAddress serverIpAddress;
    private ScheduledExecutorService heartbeatExecutor;

    private byte[] incomingDatagramPacketBuffer = new byte[INCOMING_BUFFER_SIZE];
    private byte[] outgoingDatagramPacketBuffer = new byte[OUTGOING_BUFFER_SIZE];

    private Map<String, String> mappedJsonString;
    private SerializableGameState gameState;

    private ConcurrentHashMap<String, Long> connectedPlayers;

    private ApplicationContext applicationContext;
    private JSONParser jsonParser;

    /**
     * NetworkHandler constructor initializes the game state, connected players, and sets up necessary socket communication.
     * @param gameState the current state of the game represented as a SerializableGameState object.
     */
    public NetworkHandler(SerializableGameState gameState) {
        this.gameState = gameState;
        connectedPlayers = new ConcurrentHashMap<>();
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        try {
            serverSocket = new DatagramSocket(SERVER_PORT);
            serverIpAddress = InetAddress.getByName(LOCALHOST_ADDRESS);
            incomingDatagramPacket = new DatagramPacket(incomingDatagramPacketBuffer, incomingDatagramPacketBuffer.length);
            outgoingDatagramPacket = new DatagramPacket(outgoingDatagramPacketBuffer, outgoingDatagramPacketBuffer.length);
            outgoingDatagramPacketBuffer = RECEIVED_MESSAGE.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Listens for incoming packets, schedules the heartbeat task, and processes the received data.
     */
    public void listen() {
        scheduleHeartbeatTask();
        while (true) {
            try {
                receiveIncomingPacket();
                parseIncomingPacket();

                if (mappedJsonString.containsKey("command")) {
                    handleCommand();
                } else if (mappedJsonString.containsKey("createHero")) {
                    handleCreateHero();
                } else if (isGetUpdate()) {
                    handleGetUpdate();
                } else {
                    System.out.println("it is not a command");
                }
            } catch (SocketException e) {
                e.printStackTrace();
                break;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * Removes disconnected players from the game state based on their last received heartbeat timestamp.
     * @param gameState the current state of the game represented as a SerializableGameState object.
     */
    private void useHeartBeatToRemoveDisconnectedPlayers(SerializableGameState gameState) {
        if (connectedPlayers != null) {
            ConcurrentHashMap<String, Long> connectedPlayersCopy = new ConcurrentHashMap<>(connectedPlayers);
            long currentTime = System.currentTimeMillis();
            connectedPlayersCopy.forEach((playerId, timeToLive) -> {
                double deltaTime = (double)(currentTime - timeToLive) / 1000.0;
                if (deltaTime > TIME_THRESHOLD_SECONDS) {
                    gameState.getConnectedPlayers().remove(playerId);
                    connectedPlayers.remove(playerId);
                }
            });
        }
    }

    /**
     * Closes the server socket to stop listening for incoming packets.
     */
    public void closeServerSocket() {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    /**
     * Schedules the heartbeat task to run periodically and remove disconnected players.
     */
    private void scheduleHeartbeatTask() {
        heartbeatExecutor.scheduleAtFixedRate(() -> useHeartBeatToRemoveDisconnectedPlayers(gameState),
                0, TimeUnit.SECONDS.toMillis((long) TIME_THRESHOLD_SECONDS), TimeUnit.MILLISECONDS);
    }

    /**
     * Receives the incoming datagram packet from clients.
     * @throws IOException if there is an error receiving the datagram packet.
     */
    private void receiveIncomingPacket() throws IOException {
        serverSocket.receive(incomingDatagramPacket);
    }

    /**
     * Parses the incoming packet and stores the result in a mappedJsonString object.
     * @throws ParseException if there is an error parsing the JSON string.
     */
    private void parseIncomingPacket() throws ParseException {
        String incomingString = new String(incomingDatagramPacket.getData(), 0, incomingDatagramPacket.getLength());
        jsonParser = new JSONParser(incomingString);
        mappedJsonString = (Map<String, String>) jsonParser.parse();
    }

    /**
     * Handles the command received in the packet and calls the appropriate method based on the command type.
     */
    private void handleCommand() {
        String[] args = mappedJsonString.get("command").split("_");
        InputHandler.handleInput(gameState, args);
    }

    /**
     * Handles the createHero command, creates a new hero for the player, and sends the updated game state.
     * @throws IOException if there is an error sending the updated game state.
     */
    private void handleCreateHero() throws IOException {
        String[] args = mappedJsonString.get("createHero").split("_");
        String playerId = args[1];
        String heroName = args[0];
        CreationHandler.handleCreation(gameState, playerId, heroName);
        connectedPlayers.put(playerId, System.currentTimeMillis());
        sendSerializedGameState();
    }

    /**
     * Checks if the received packet is a getUpdate command and if the player is connected.
     * @return true if it's a getUpdate command and the player is connected, false otherwise.
     */
    private boolean isGetUpdate() {
        return mappedJsonString.containsKey("getUpdate") && connectedPlayers.containsKey(mappedJsonString.get("getUpdate"));
    }

    /**
     * Handles the getUpdate command, updates the game state, and sends the updated game state to the client.
     * @throws IOException if there is an error sending the updated game state.
     */
    private void handleGetUpdate() throws IOException {
        UpdateHandler.handleUpdates(gameState, mappedJsonString.get("getUpdate"));
        connectedPlayers.put(mappedJsonString.get("getUpdate"), System.currentTimeMillis());
        sendSerializedGameState();
    }

    /**
     * Sends the serialized game state to the client.
     * @throws IOException if there is an error sending the serialized game state.
     */
    private void sendSerializedGameState() throws IOException {
        SerializableGameStateDecorator serializableGameStateDecorator = new SerializableGameStateDecorator(new BasicSerializer());
        outgoingDatagramPacketBuffer = serializableGameStateDecorator.serialize(gameState);
        serverSocket.send(new DatagramPacket(outgoingDatagramPacketBuffer, outgoingDatagramPacketBuffer.length,
                incomingDatagramPacket.getAddress(), incomingDatagramPacket.getPort()));
    }
}
