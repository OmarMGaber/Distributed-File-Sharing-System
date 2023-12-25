package app;

import servers.ServerNode;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Testing Nodes 2!");

        ServerNode serverNode1 = new ServerNode();
        ServerNode serverNode2 = new ServerNode();
        ServerNode serverNode3 = new ServerNode();

        serverNode1.addPeer(serverNode2);
        serverNode1.addPeer(serverNode3);
        serverNode2.addPeer(serverNode1);
        serverNode2.addPeer(serverNode3);

        new Thread(serverNode1).start();
        new Thread(serverNode2).start();
        new Thread(serverNode3).start();

    }
}
