//package p2p;
import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {

	private static final int sPort = 8005;   //The server will be listening on this port number
    private static Socket connection;
    private static int cnum;
    private String message;
    
    public void Server(){}
   

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
    	System.out.println("number "+ num );
    	//byte[] chunkarray = new byte[(int) (size)];
    	String[] chunkName = new String[num];  // array to store the chunks name eg: 1.txt
    	
    	FileInputStream filein = new FileInputStream(file); 
    	
    	long end = 0;
    	int start = 0;             //input the index of file start and end;
    	//divide into chunks;
    	for (int i = 0; i < num; i++) {
    	   File chunk = new File(parentFile, i+1 + SUFFIX);
    	  // System.out.println("The new file name is "+(i+1)+ SUFFIX );
    	   FileOutputStream fileout = new FileOutputStream(chunk);   //outstream;
    	   //ByteArrayOutputStream bytefileout = new ByteArrayOutputStream(); // byte out stream;
    	   end+=size;
    	   end = (end > fileLength) ? fileLength : end;  //define the end of the file;
    	   for (; start < end; start++) { 
               fileout.write(filein.read());
           } 
           fileout.close(); 
           chunkName[i] = chunk.getAbsolutePath(); //the array stores the absolute path;
          // System.out.println("tell me the chunk name" + chunkName[0]);
           } 
        filein.close(); 
        
        return chunkName; 
    
    }
    
	public static void main(String[] args) throws Exception {
		
		// create a new listener, bind to a port
		System.out.println("The server is now running");
		ServerSocket listener = new ServerSocket(sPort);   
		Server server = new Server();
		
		//get the chunklist that server has;
		int chunkno = server.divide("e://2015fall/computer network/project/server/data.pdf",1024*100).length;
		String[] chunklist = new String[chunkno];
		chunklist = server.divide("e://2015fall/computer network/project/server/data.pdf",1024*100);
//		String newchunkname = chunklist[1].substring(chunklist[1].length()-5, chunklist[1].length());
//		int namelen =  newchunkname.getBytes().length;
        //System.out.println("tell me the chunk name" + chunklist[1]);
		//send the client their chunks. client1:1,6;client2:2,7; client3: 3;client4:4;client5:5;
		int clientNum = 1;
		try
		{
				do
				{				
				   Serverhandler handler = new Serverhandler(listener.accept(),clientNum,chunklist[clientNum-1]);
				//Serverhandler handler = new Serverhandler(listener.accept(),clientNum,"e://2015fall/computer network/project/server/data.pdf");
				   handler.start();     // start the connection with one client;
				   System.out.println("Client "  + clientNum + " is connected!");
				   //handler.run();
				   clientNum++;         //start the conncetion with other clients;
				}while(clientNum <= 5);
				do
				{
					Serverhandler handler = new Serverhandler(listener.accept(),clientNum-5,chunklist[clientNum-1]);
					//Serverhandler handler = new Serverhandler(listener.accept(),clientNum-5,"e://2015fall/computer network/project/server/data.pdf");
					handler.start();    
					//handler.run();
					//System.out.println("Client "  + (clientNum-5)+ " is connected!");
					clientNum++;
				}while(clientNum <=7);
		
        }   finally {
		        listener.close();
                }
		
				
	}
	
	private static class Serverhandler extends Thread{
	        private String ACK;   // get the ack from client
	        private String chunk;   // send the chunk to the client;
	        private Socket connection;
	    	private ObjectInputStream in;	//stream read from the socket
	    	private ObjectOutputStream out;    //stream write to the socket
	  	    private int cnum;		//The index number of the client

	        public Serverhandler(Socket connection, int cnum, String chunk)
	        {
	      	  this.connection = connection;
	      	  this.cnum = cnum;
	      	  this.chunk = chunk;
	        }
//	        public static byte[] int2byte(int i) {    
//	            return new byte[]{    
//	                    (byte) ((i >> 24) & 0xFF),    
//	                    (byte) ((i >> 16) & 0xFF),    
//	                    (byte) ((i >> 8) & 0xFF),    
//	                    (byte) (i & 0xFF)    
//	            };    
//	        }    
	        public static int b2i(byte[] b) {    
	            int value = 0;    
	            for (int i = 0; i < 4; i++) {    
	                int shift = (4 - 1 - i) * 8;    
	                value += (b[i] & 0x000000FF) << shift;    
	            }    
	            return value;    
	        }    
	        public void run() 
	        {
	      	  File chunkfile = new File(chunk);
	         try{
	        	 while(true){
	        		 
	      	  out = new ObjectOutputStream(connection.FileOutputStream());
	  		 // out.flush();
	  		  in = new ObjectInputStream(new FileInputStream(chunkfile));
	      	  int len = (int)chunkfile.length();
	      	  System.out.println("the content is  " + in);
	  		  //write file name in a chunkname[]
	  		  byte[] chunkname = chunkfile.getName().getBytes();
	  		  out.write(b2i(chunkname));
//	  		  System.out.println("printout the content " + out);
	  		  //write file content in a buffer[]
	  		  int bufferSize = 100*1024;
	  		  byte[] buf = new byte[bufferSize];
//	  		  while(true)
//	  		  {
//	  			  int read = 0;
//	  			  if(in !=null)
//	  			  {
//	  				  read = in.read(buf);
//	  			  }
//	  			  if(read == -1)
//	  			  {
//	  				  break;
//	  			  }
//	  			  out.write(buf,0,read);
//	  		  }
	  		  int count = -1;
	  		  while((count = in.read(buf,0,bufferSize)) != -1)  
	            out.write(b2i(buf));
	  		  
	         }
	         }
	         catch(IOException ioException){
	    		   System.out.println("Disconnect with Client " + cnum);
	  		}
	         finally
	      	  {
	      		  try{
	         		//	in.close();
	    				out.close();
	    				connection.close();
	    			}
	    			catch(IOException ioException){
	    				System.out.println("Disconnect with Client " + cnum);
	    			}
	      	  }
	        }
	  }
}