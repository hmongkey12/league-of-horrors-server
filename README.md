# League of Horrors
League of Horrors is a 2D MOBA (Multiplayer Online Battle Arena) game.

## Java Game Server
This is a Java game server for League of Horrors. The server manages the game state, while the client is responsible for rendering the game to the player.

The game server uses UDP (User Datagram Protocol) for communication between the client and server over the network.

## Getting Started
### Prerequisites
To build and run the Java game server, you need to have the following software installed:

* Java Development Kit (JDK) version 8 or higher
* Gradle build tool version 6.0 or higher
### Building the Game Server
To build the Java game server, run the following command in your terminal:

bash
`gradle build`
This will compile the Java source code and generate an executable JAR file in the build/libs directory.

### Running the Game Server
To run the Java game server, navigate to the build/libs directory and run the following command in your terminal:

bash
`java -jar java-game-server.jar`
This will start the game server and it will be listening for UDP packets on the default port.

## Architecture
The Java game server follows a client-server architecture, where the client is a renderer and the server is the game state manager. The client sends user input to the server, and the server updates the game state and sends the updated state back to the client for rendering.

## Network Communication
The Java game server uses UDP for communication between the client and server over the network. UDP is a lightweight protocol that is well-suited for real-time games, as it has lower latency than TCP (Transmission Control Protocol).

## Game State Management
The Java game server is responsible for managing the game state, which includes the positions of all players and game objects, player health and mana, game timer, etc. The server updates the game state based on user input from the client and sends the updated state back to the client for rendering.

## Contributing
If you would like to contribute to the Java game server, please submit a pull request with your changes. Before submitting a pull request, please make sure to run the tests and verify that they pass.

## License
The Java game server is open source software released under the MIT License. See the LICENSE file for details.
