package com.league.udpserver.handlers;

import com.serializers.SerializableGameState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@RunWith(PowerMockRunner.class)
@PrepareForTest(InputHandler.class)
public class NetworkHandlerTest {
    private static final String SERVER_IP_ADDRESS = "127.0.0.1";
    private static final int CLIENT_PORT = 8085;
    private static final int SERVER_PORT = 8086;
    private static final int WAIT_TIME_BEFORE_SEND = 1000;
    private static final int WAIT_TIME_AFTER_SEND = 2000;

    private NetworkHandler networkHandler;
    private DatagramSocket clientSocket;
    private InetAddress serverIpAddress;

    @Before
    public void setUp() throws IOException {
        SerializableGameState gameState = new SerializableGameState();
        networkHandler = new NetworkHandler(gameState);
        serverIpAddress = InetAddress.getByName(SERVER_IP_ADDRESS);
        clientSocket = new DatagramSocket(CLIENT_PORT);
    }

    @Test
    public void inputHandler_shouldBeCalled_whenClientSendsCommandPacket() throws IOException {
        // Setup
        PowerMockito.mockStatic(InputHandler.class);
        String commandMessage = "{\"command\":\"left_playerid\"}";
        DatagramPacket outgoingPacket = new DatagramPacket(commandMessage.getBytes(), commandMessage.length(), serverIpAddress, SERVER_PORT);

        // start listening for the response
        Thread listeningThread = new Thread(() -> networkHandler.listen());
        listeningThread.start();

        // wait for the server to start listening
        try {
            Thread.sleep(WAIT_TIME_BEFORE_SEND);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Trigger: send the command to the server
        clientSocket.send(outgoingPacket);

        // wait for the server to process the command
        try {
            Thread.sleep(WAIT_TIME_AFTER_SEND);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // stop the listening thread
        listeningThread.interrupt();

        // Verify: check if InputHandler.handleInput is called with the correct args
        PowerMockito.verifyStatic(InputHandler.class, Mockito.times(1));
        InputHandler.handleInput(Mockito.any(SerializableGameState.class), Mockito.eq(new String[]{"left", "playerid"}));
    }
}
