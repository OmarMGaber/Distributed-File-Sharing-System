package app;

import server.ServerNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Creating a constant number of nodes and connecting them randomly for testing

        List<ServerNode> serverNodes = new ArrayList<>();

        final int numberOfNodes = 10;
        for (int i = 0; i < numberOfNodes; i++) {
            ServerNode serverNode = new ServerNode();
            serverNodes.add(serverNode);
        }

        connectNodesRandomly(serverNodes);

        for (ServerNode serverNode : serverNodes) {
            new Thread(serverNode).start();
        }
    }

    private static void connectNodesRandomly(List<ServerNode> serverNodes) {
        Random random = new Random();

        for (ServerNode serverNode : serverNodes) {
            int numberOfPeers = random.nextInt(serverNodes.size() - 1) + 1;
            System.out.println("Server " + serverNode.NODE_ID + " will connect to " + numberOfPeers + " peers");

            for (int i = 0; i < numberOfPeers; i++) {
                int randomPeerIndex = random.nextInt(serverNodes.size());
                ServerNode randomPeer = serverNodes.get(randomPeerIndex);
                System.out.println("Server " + serverNode.NODE_ID + " will connect to server " + randomPeer.NODE_ID);

                // Avoid connecting a node to itself
                if (randomPeer != serverNode && !serverNode.addPeer(randomPeer)) {
                    i--; // Try again
                }
            }
        }
    }
}
