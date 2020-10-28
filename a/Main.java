import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;


public class Main {
	
	private static int UDP_PORT_NUM = 8866;
	private static int DNS_SERVER_PORT = 53;

	public static void main(String[] args) throws IOException {
		
	    Scanner scanner = new Scanner(System.in);  // Create a Scanner object
	    System.out.println("Enter a domain (e.g. facebook.com)");
	    String domain = scanner.nextLine();
	    scanner.close();
	    
		//Get IP Address of user provided info
		InetAddress net = InetAddress.getByName("8.8.8.8");

		//UDP Client creation
		DatagramSocket socket = new DatagramSocket(UDP_PORT_NUM);
		socket.setSoTimeout(10000);
		//Package creation
		
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
		
		dos.writeShort(0xFADE); // Field Name: ID
		dos.writeShort(0x0001); // Field Name: Query Flags
		dos.writeShort(0x0001); // Field Name: Number of Questions
        dos.writeShort(0x0000); // Field Name: Answer Record Count
        dos.writeShort(0x0000); // Field Name: Authority Record Count
        dos.writeShort(0x0000); // Field Name: Additional Record Count

        String[] domainParts = domain.split("\\.");
        
        for (int i = 0; i<domainParts.length; i++) {
            byte[] domainBytes = domainParts[i].getBytes("UTF-8");
            dos.writeByte(domainBytes.length);
            dos.write(domainBytes);
        }
		
        dos.writeByte(0x00);

        dos.writeShort(0x0001);

        dos.writeShort(0x0001);

        byte[] dnsFrame = baos.toByteArray();

        System.out.println("Sending: " + dnsFrame.length + " bytes");
		
		DatagramPacket payload = new DatagramPacket(dnsFrame, dnsFrame.length, net, DNS_SERVER_PORT);
		socket.send(payload);
		
		//Response creation
		byte[] response_buffer = new byte[1024];
		DatagramPacket response = new DatagramPacket(response_buffer, response_buffer.length);

		socket.receive(response); //Blocks until a response is received

        System.out.println("\n\nReceived: " + response.getLength() + " bytes");

        System.out.println("\n");

        DataInputStream din = new DataInputStream(new ByteArrayInputStream(response_buffer));
        System.out.println("Transaction ID: 0x" + String.format("%x", din.readShort()));
        System.out.println("Flags: 0x" + String.format("%x", din.readShort()));
        System.out.println("Questions: 0x" + String.format("%x", din.readShort()));
        System.out.println("Answers RRs: 0x" + String.format("%x", din.readShort()));
        System.out.println("Authority RRs: 0x" + String.format("%x", din.readShort()));
        System.out.println("Additional RRs: 0x" + String.format("%x", din.readShort()));
		
        int recLen = 0;
        while ((recLen = din.readByte()) > 0) {
            byte[] record = new byte[recLen];

            for (int i = 0; i < recLen; i++) {
                record[i] = din.readByte();
            }

        }


        for(int i = 0; i < 7; i++) {
            din.readShort();
        }

        short addrLen = din.readShort();

        System.out.print("Address: ");
        for (int i = 0; i < addrLen; i++ ) {
            System.out.print("" + String.format("%d", (din.readByte() & 0xFF)) + ".");
        }
        
		socket.close();
		
	}

}

