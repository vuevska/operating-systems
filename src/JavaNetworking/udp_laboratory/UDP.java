package JavaNetworking.udp_laboratory;

import java.io.IOException;
import java.net.*;

public class UDP {
    public static void main(String[] args) {
        UDPClient client = new UDPClient(ConstantClass.SERVER_ADDRESS, ConstantClass.SERVER_PORT);
        client.start();
    }
}

class UDPClient extends Thread {
    private final int serverPort;
    private DatagramSocket socket;
    private InetAddress address;

    public UDPClient(String serverName, int serverPort) {
        this.serverPort = serverPort;
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(serverName);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer = ConstantClass.MESSAGE.getBytes();
        byte[] receiveBuffer = new byte[256];
        DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, address, serverPort);
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length, address, serverPort);
        try {
            socket.send(sendPacket);
            System.out.println("Sent:" + new String(sendPacket.getData(), 0, sendPacket.getLength()));
            socket.receive(receivePacket);
            System.out.println(new String(receivePacket.getData(), 0, receivePacket.getLength()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!socket.isClosed()) {
                socket.close();
            }
        }
    }
}

class ConstantClass {
    public static final String SERVER_ADDRESS = "194.149.135.49";
    public static final int SERVER_PORT = 9753;

    public static final String MESSAGE = "203007";
}