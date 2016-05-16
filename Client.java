//package p2p;
import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Client {
	private static final int sPort = 8005;
	Socket requestSocket; 
	ObjectOutputStream out;         //stream write to the socket
 	ObjectInputStream in;          //stream read from the socket
 	private static String path = "e://2015fall/computer network/project/client1/";//the path to store new chunk;
 	private static String newchunkname;  // new chunk name;
	public void Client() {}


    
	void receiveChunk()
	{
		try {
			 requestSocket = new Socket("localhost", sPort);
			 System.out.println("Connected to localhost in port 8080");
			 in = new ObjectInputStream(requestSocket.getInputStream());
			 out = new ObjectOutputStream(requestSocket.getOutputStream());
			 
	            
			 byte[] chunkname = new byte[100]; //length of file name is 5
//			 int buf = 0;
//			 buf = in.readInt();
			String namestr = Arrays.toString(chunkname);
			 System.out.println("namestr is" + namestr);
		} 
		catch (ConnectException e) {
			System.err.println("Connection refused. You need to initiate a server first.");
	    } 
	    catch(UnknownHostException unknownHost){
		System.err.println("You are trying to connect to an unknown host!");
	     }
	     catch(IOException ioException){
		ioException.printStackTrace();
	    }
	finally{
		//Close connections
		try{
			in.close();
			//out.close();
			requestSocket.close();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	}

	public static void main(String args[])
	{
		Client client = new Client();
		client.receiveChunk();
	}

}