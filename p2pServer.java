//package p2p;
import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

//import p2p.Server.Serverhandler;

public class p2pServer {
    
	private static int sPort = 8081;   //The server will be listening on this port number
	
	
	public void p2pServer()	{}
	
	// divide the big file into small chunks;
	String[] divide(String name, long size) throws Exception
    {
    	final String SUFFIX = ".pdf";
    	File file = new File(name);     //create a new object of file;
    	File parentFile = file.getParentFile(); // get parent file path; store the chunks into it.
   
    	long fileLength = file.length();   // get the file length;
    	int num;                           // get the number of chunks;
    	if(fileLength % size !=0)
    	{
    		num = (int) (fileLength / size + 1);
    	}
    	else num = (int) (fileLength / size);
    	//System.out.println("number "+ num );
    	String[] chunkName = new String[num];  // array to store the chunks name eg: 1.txt
    	
    	FileInputStream filein = new FileInputStream(file); 
    	
    	long end = 0;
    	int start = 0;             //input the index of file start and end;
    	for (int i = 0; i < num; i++) 
    	{
    	   File chunk = new File(parentFile, i+1 + SUFFIX);
    	  // System.out.println("The new file name is "+(i+1)+ SUFFIX );
    	   FileOutputStream fileout = new FileOutputStream(chunk);   //outstream;
    	   end+=size;
    	   end = (end > fileLength) ? fileLength : end;  //define the end of the file;
    	   for (; start < end; start++) 
    	   { 
               fileout.write(filein.read());
           } 
           fileout.close(); 
           chunkName[i] = chunk.getAbsolutePath(); //the array stores the absolute path;
          // System.out.println("tell me the chunk name" + chunkName[0]);
        } 
        filein.close(); 
        
        return chunkName; 
    
    }
	private static class Serverhandler extends Thread
	{
		private String message;    //message received from the client
		private String response;    //uppercase message send to the client
		private Socket connection;
		private String chunk;
        private ObjectInputStream in;	//stream read from the socket
        private ObjectOutputStream out;    //stream write to the socket
		private int num;		//The index number of the client
		
		public Serverhandler(Socket connection, int num)
		{
    		this.connection = connection;
		    this.num = num;
	    }
		public void run(String chunk) {
	 		try{
				//initialize Input and Output streams
				out = new ObjectOutputStream(connection.getOutputStream());
				out.flush();
				in = new ObjectInputStream(connection.getInputStream());
				try{
					while(true)
					{
						//receive the message sent from the client
						message = (String)in.readObject();
						System.out.println("print out msg: "+ message);
						if(message =="send me data.pdf")
						{
							response = chunk; 
						}
						sendMessage(chunk);	
						//System.out.println("run1 ");
					}
				   }
				catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}
	 		}
			catch(IOException ioException){
					System.out.println("Disconnect with Client " + num);
			}
			finally{
					//Close connections
				try{
						//in.close();
						out.close();
						connection.close();
						System.out.println("run2 ");
				}
				catch(IOException ioException){
						System.out.println("Disconnect with Client " + num);
				}
			}
	} 
	     // function: send msg
		public void sendMessage(String msg)
		{
			try{
				out.writeObject(msg);
				out.flush();
				System.out.println("Send message: " + msg + " to Client " + num);
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}	
	}	
	public static void main(String[] args) throws Exception 
	{
		System.out.println("The server is running."); 
    	
    	p2pServer server = new p2pServer();
    	
    	int chunkno = server.divide("e://2015fall/computer network/project/server/data.pdf",1024*100).length;
		String[] chunklist = new String[chunkno];
		chunklist = server.divide("e://2015fall/computer network/project/server/data.pdf",1024*100);
		System.out.println("Chunknumber is  "  + chunkno );
		//send the client their chunks. client1:1,6;client2:2,7; client3: 3;client4:4;client5:5;
		int clientNum = 1;
		int cn = 0;
		int n = chunkno % 5;  // n=3
		int m = chunkno / 5; //  m=17
		Boolean flag = true;
		
		   System.out.println(chunklist[1]);
			//give each client one port to transmit data;   
			if(clientNum ==1)
			  {
				ServerSocket listener= new ServerSocket(8081);
				while(flag)	
				 { 
				   System.out.println("run 11");
				   Serverhandler handler1 = new Serverhandler(listener.accept(),clientNum);
				   System.out.println("Client "  + clientNum + " is connected!");
				   cn+=1;
				   handler1.run("e://2015fall/computer network/project/server/"+(clientNum+5*(cn-1))+".pdf");
				  // clientNum++;         //start the conncetion with other clients;
				   if(n>=clientNum)
				   {
				     if(cn >= m+1)
				     {
					   flag = false;
				     }
			       }
				   else
				   {
					   if(cn>= m)
					   {
						   flag = false;
					   }
				   }
			   }
			    cn=0;
			    flag = true;
			    listener.close();
			  }
			System.out.println("Clientnum "  + clientNum);
			 clientNum++;
			if(clientNum ==2)
			  {
				ServerSocket listener= new ServerSocket(8082);
				while(flag)	
				 {  
				   Serverhandler handler1 = new Serverhandler(listener.accept(),clientNum);
				   System.out.println("Client "  + clientNum + " is connected!");
				   cn+=1;
				   handler1.run("e://2015fall/computer network/project/server/"+(clientNum+5*(cn-1))+".pdf");
				  // clientNum++;         //start the conncetion with other clients;
				   if(n>=clientNum)
				   {
				     if(cn >= m+1)
				     {
					   flag = false;
				     }
			       }
				   else
				   {
					   if(cn>= m)
					   {
						   flag = false;
					   }
				   }
				 }
			    cn=0;
			    flag = true;
			    listener.close();
				 }
			  
			  
			System.out.println("Clientnum "  + clientNum);
			 clientNum++;
			if(clientNum ==3)
			  {
				ServerSocket listener= new ServerSocket(8083);
				while(flag)	
				 {  
				   Serverhandler handler1 = new Serverhandler(listener.accept(),clientNum);
				   System.out.println("Client "  + clientNum + " is connected!");
				   cn+=1;
				   System.out.println("run 4");
				   handler1.run("e://2015fall/computer network/project/server/"+(clientNum+5*(cn-1))+".pdf");
				  // clientNum++;         //start the conncetion with other clients;
				   System.out.println("run 3");
				   if(n>=clientNum)
				   {
				     if(cn >= m+1)
				     {
					   flag = false;
				     }
			       }
				   else
				   {
					   if(cn>= m)
					   {
						   flag = false;
					   }
				   }
			   }
			    cn=0;
			    flag = true;
			    listener.close();
			  }
			System.out.println("Clientnum "  + clientNum );
			 clientNum++;
			if(clientNum == 4)
			  {
				ServerSocket listener= new ServerSocket(8084);
				while(flag)	
				 {  
				   Serverhandler handler1 = new Serverhandler(listener.accept(),clientNum);
				   System.out.println("Client "  + clientNum + " is connected!");
				   cn+=1;
				   handler1.run("e://2015fall/computer network/project/server/"+(clientNum+5*(cn-1))+".pdf");
				  // clientNum++;         //start the conncetion with other clients;
				   if(n>=clientNum)
				   {
				     if(cn >= m+1)
				     {
					   flag = false;
				     }
			       }
				   else
				   {
					   if(cn>= m)
					   {
						   flag = false;
					   }
				   }
			   }
			    cn=0;
			    flag = true;
			    listener.close();
			  }
			System.out.println("Clientnum "  + clientNum );
			 clientNum++;
			 
			if(clientNum ==5)
			  {
				ServerSocket listener= new ServerSocket(8085);
				while(flag)	
				 {  
				   Serverhandler handler1 = new Serverhandler(listener.accept(),clientNum);
				   System.out.println("Client "  + clientNum + " is connected!");
				   cn+=1;
				   handler1.run("e://2015fall/computer network/project/server/"+(clientNum+5*(cn-1))+".pdf");
				  // clientNum++;         //start the conncetion with other clients;
				   if(n>=clientNum)
				   {
				     if(cn > m+1)
				     {
					   flag = false;
				     }
			       }
				   else
				   {
					   if(cn>= m)
					   {
						   flag = false;
					   }
				   }
			   }
			    cn=0;
			    flag = true;
			    listener.close();
			  }
			System.out.println("Clientnum "  + clientNum );
			clientNum++;
		  }
	  }
	
	

