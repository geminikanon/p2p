//package p2p;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Serverhandler extends Thread{
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
//      public static byte[] int2byte(int i) {    
//          return new byte[]{    
//                  (byte) ((i >> 24) & 0xFF),    
//                  (byte) ((i >> 16) & 0xFF),    
//                  (byte) ((i >> 8) & 0xFF),    
//                  (byte) (i & 0xFF)    
//          };    
//      }    
      public void run() 
      {
    	  File chunkfile = new File(chunk);
       try{
    	  out = new ObjectOutputStream(connection.getOutputStream());
		  out.flush();
		  in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(chunkfile)));
    	  int len = (int)chunkfile.length();
		  
		  //write file name in a chunkname[]
		  byte[] chunkname = chunkfile.getName().getBytes();
		  out.write(chunkname);
		  //write file content in a buffer[]
		  int bufferSize = 100*1024;
		  byte[] buf = new byte[bufferSize];
		  while(true)
		  {
			  int read = 0;
			  if(in !=null)
			  {
				  read = in.read(buf);
			  }
			  if(read == -1)
			  {
				  break;
			  }
			  out.write(buf,0,read);
		  }
       }
       catch(IOException ioException){
  		   System.out.println("Disconnect with Client " + cnum);
		}
       finally
    	  {
    		  try{
  				in.close();
  				out.close();
  				connection.close();
  			}
  			catch(IOException ioException){
  				System.out.println("Disconnect with Client " + cnum);
  			}
    	  }
      }
}
