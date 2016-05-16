//package p2p;
import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class p2pClient4 {

		public void p2pClient4() {}
		
		// check fileid in given folder;                                       do the change
		ArrayList<String> checkfile(String path)
		{
			
			File file = new File(path); 
			File[] array = file.listFiles();
			ArrayList existfileid = new ArrayList();
			ArrayList filenameid = new ArrayList();
			String exchange = "";
			for(int i=0;i<array.length;i++)
			{
				if(array[i].isFile())
				{ 
					// add existed file id to a dynamic array;
					String filename = array[i].getName();
					String[] splitmsg = filename.split("\\.");
//					for(int i=0;i<splitmsg.length;i++)
//					{
//					    need.add(splitmsg[i]);	
//					}
					//get the number of chunks in client1;
//					String fileid = filename.substring(0, filename.length()-4);   //change 7 to 6
//					//find the number instead of string;
//					if(fileid != null && !"".equals(fileid))  //
//					{
//						for(int j=0;j<fileid.length();j++)
//						{
//							//System.out.println(message.charAt(i));
//						    if(fileid.charAt(j)>=48 && fileid.charAt(j)<=57)
//						    {
//						    	 
//						         filenameid.add(String.valueOf(fileid.charAt(j)));
//						         
//						    }
//						}	
//					}
//					for(int x =0;x<filenameid.size();x++)
//					{
//						exchange = exchange + filenameid.get(i);
//					}
				   
				   existfileid.add(splitmsg[0]); 
				}
			};
			return existfileid;
		}
		
		//check total number of chunks in server folder
		int checkfilenum()
		{
			int fileno=0;
			String path = "e://2015fall/computer network/project/server";
			File file = new File(path); 
			File[] array = file.listFiles();
			ArrayList existfileid = new ArrayList();
			for(int i=0;i<array.length;i++)
			{
				if(array[i].isFile())
				{ 
					// add existed file id to a dynamic array;
					fileno+=1;
				}
			}
			return fileno-1;                                  // there is one data file    change
		}
		
		//thread1: get chunks from Server;
		private static class CtoSrequest extends Thread
		{
			private String host;
		    private int port;   
          	private String path;
          	private ObjectOutputStream out;         //stream write to the socket
          	private ObjectInputStream in;          //stream read from the socket
        	private String message;      //message send to the server
        	private String response;
        	private Socket requestSocket;
        	
			public CtoSrequest(String host1, int port1)
			{
				
				this.host = host1;
				this.port = port1;
			}
			public void run()
			{
				try{
					//create a socket to connect to the server
					requestSocket = new Socket(host, port);
					System.out.println("Connected to localhost in port"+ port);
					
					//send request to server;
					out = new ObjectOutputStream(requestSocket.getOutputStream());
					out.flush();
					in = new ObjectInputStream(requestSocket.getInputStream());
					
					message = "send me data.pdf";
					sendMessage(message);
					System.out.println("Send msg: "+ message+ " to Client via port number " + port);
					
					//get response from server;
					response = (String)in.readObject();
					String chunknameid = response.substring(response.length()-7, response.length()-4); 
					// get the chunkname's id number
					ArrayList exchange = new ArrayList();
					ArrayList chunk = new ArrayList();
					String chunkname = "";
					if(chunknameid != null && !"".equals(chunknameid))  //
					{
						for(int j=0;j<chunknameid.length();j++)
						{
							//System.out.println(message.charAt(i));
						    if(chunknameid.charAt(j)>=48 && chunknameid.charAt(j)<=57)
						    {
						    	 
						         exchange.add(String.valueOf(chunknameid.charAt(j)));
						         
						    }
						}	
					}
					for(int i =0;i<exchange.size();i++)
					{
						chunkname = chunkname + exchange.get(i);
					}
				    //System.out.println(chunkname);
				    chunk.add(chunkname); 
					System.out.println("Client1 has received chunk number " + chunkname);
					
					            // store the name of chunk receive from client;
					// read a file and write into a new file
					System.out.println("run1 ");
					File inputfile = new File(response);
				    InputStream reader = new FileInputStream(inputfile);
					BufferedInputStream br = new BufferedInputStream(reader);
					System.out.println("run2 ");
					File outputfiledir = new File("e:/2015fall/computer network/project/client4/data");
                    if(!outputfiledir.exists())
					{
					      outputfiledir.mkdirs();
			        }
                    // System.out.println("run3 ");
                    File outputfile = new File("e://2015fall/computer network/project/client4/data/" + chunkname +".pdf");
                    outputfile.createNewFile();
			        OutputStream writer = new FileOutputStream(outputfile);
			        BufferedOutputStream bufwriter = new BufferedOutputStream(writer); 
			        //System.out.println("run4 ");
			        //byte[] newfile = new byte[br.available()];
			        while(true)
			        {  
			        	  int i = br.read();
			        	  if(i == -1)
			        	  {
			        		  break;
			        	  }
			        	  bufwriter.write(i);
			        }
			        // System.out.println("run5 ");
			        bufwriter.flush();
			        reader.close();
			        br.close();
			        writer.close();
			        bufwriter.close();
			        //System.out.println("run6 ");
					 
			       }
				catch (ConnectException e) {
	    			System.err.println("Connection refused. You need to initiate a server first.");
			    } 
			    catch(UnknownHostException unknownHost){
				System.err.println("You are trying to connect to an unknown host!");
			    }
				catch ( ClassNotFoundException e ) {
            		System.err.println("Class not found");
        	    } 
			    catch(IOException ioException){
				ioException.printStackTrace();
			    }
			finally{
				//Close connections
				try{
					in.close();
					out.close();
					requestSocket.close();
				}
				catch(IOException ioException){
					ioException.printStackTrace();
				}
			}
	         }
			void sendMessage(String msg)
			{
				try{
					//stream write the message
					out.writeObject(msg);
					out.flush();
				}
				catch(IOException ioException){
					ioException.printStackTrace();
				}
			}
		}

		//class1: deal with socket
		static class Task implements Runnable {  
			   
		      private Socket socket; 
		      private int port;
		      private int cnum;

		      public Task(Socket socket, int port, int cnum) {  
		         this.socket = socket;  
		         this.port = port;
		         this.cnum = cnum;
		       //  this.fromcnum = fromcnum;
		      }  
		        
		      public void run() {  
		  
		         try {  
		        	 C1download download  = new C1download(port); 
		        	 System.out.println("c1 run16");
		             download.sendRequest(cnum,socket);
		             System.out.println("c1 run17");
		         } catch (Exception e) {  
		             e.printStackTrace();  
		         }  
		      }  
		 }
        // thread 2: client1 as a server, wait for request from client 2 and client 5;
		private static class C1download extends Thread
		{   
			                         //define the client port;
			private int port;
			private String message;    //message received from the client
			private String response;    //uppercase message send to the client
			private Socket connection;
			private String chunk;
	        private ObjectInputStream in;	//stream read from the socket
	        private ObjectOutputStream out;    //stream write to the socket
			private int num;		//The index number of the client
			ArrayList ask = new ArrayList();
			
			public C1download(int cport)	
			{
				this.port = cport;
			}
			
			void sendRequest(int clientNum, Socket connection)
			{
	               try{
//	            	    ServerSocket listener = new ServerSocket(port);
//	            	    connection = listener.accept();
						//initialize Input and Output streams
	            	    System.out.println("c1 run11");
						out = new ObjectOutputStream(connection.getOutputStream());
						out.flush();
						in = new ObjectInputStream(connection.getInputStream());
						
						try{
							while(true)
							{
								//receive the message sent from the client
								message = (String)in.readObject();
								System.out.println("Client "+clientNum+" need  "+ message);
								message = message.trim();  // delete the space;
								//System.out.println(message.length());
								
//								String chunkname = "";
//								for(int i=chunknameid.length();--i>=0;)
//								{
//								      int chr=chunknameid.charAt(i);
//								      if(chr>=48 || chr<=57)
//								      {
//								    	  chunkname = chunkname + (chr -48);
//								      }
								
								String pass = "";                       //changed
								// divide the msg into several msg by ,
								String[] splitmsg = message.split(",");
								for(int i=0;i<splitmsg.length;i++)
								{
								    ask.add(splitmsg[i]);	
								}
//								if(message != null && !"".equals(message))  // need consideration!!!
//								{
//									for(int i=0;i<message.length();i++)
//									{
//										//System.out.println(message.charAt(i));
//									    if(message.charAt(i)>=48 && message.charAt(i)<=57)
//									    {
//									    	pass = pass +(message.charAt(i)-48);
//									         ask.add(String.valueOf(message.charAt(i)-48));
//									         
//									    }
//									}
//									
//								}
								
								// transmit a dynamic array to a static array
								int size=ask.size();  
							    String[] array=new String[size];  
							    for(int i=0;i<ask.size();i++)
							    {  
							        array[i]=(String)ask.get(i);  
							    }  
							    
							    message = makeMsg(checkfile(clientNum), array);
								sendMessage(message);	
								System.out.println("Client4 has sent chunks  " + message + "to Client "+ clientNum);
							}
						   }
						catch(ClassNotFoundException classnot){
							System.err.println("Data received in unknown format");
						}
			 		}
					catch(IOException ioException){
							System.out.println("Disconnect with Client " + clientNum);
					}
					finally{
							//Close connections
						try{
								in.close();
								out.close();
								connection.close();
								//Thread.sleep(30000);
								System.out.println("run2 ");
						}
						catch(IOException ioException){
								System.out.println("Disconnect with Client " + clientNum);
						}
					}
				} 
				
				// get exist file from peer client
				ArrayList<String> checkfile(int clientNum)
				{
					String path = "e://2015fall/computer network/project/client"+ clientNum + "/data";
					File file = new File(path); 
					File[] array = file.listFiles();
					ArrayList existfileid = new ArrayList();
					ArrayList filenameid = new ArrayList();
					String exchange = "";
					for(int i=0;i<array.length;i++)
					{
						if(array[i].isFile())
						{ 
							// add existed file id to a dynamic array;
							String filename = array[i].getName();
							String[] splitmsg = filename.split("\\.");
//							for(int i=0;i<splitmsg.length;i++)
//							{
//							    need.add(splitmsg[i]);	
//							}
							//get the number of chunks in client1;
//							String fileid = filename.substring(0, filename.length()-4);   //change 7 to 6
//							//find the number instead of string;
//							if(fileid != null && !"".equals(fileid))  //
//							{
//								for(int j=0;j<fileid.length();j++)
//								{
//									//System.out.println(message.charAt(i));
//								    if(fileid.charAt(j)>=48 && fileid.charAt(j)<=57)
//								    {
//								    	 
//								         filenameid.add(String.valueOf(fileid.charAt(j)));
//								         
//								    }
//								}	
//							}
//							for(int x =0;x<filenameid.size();x++)
//							{
//								exchange = exchange + filenameid.get(i);
//							}
						   
						   existfileid.add(splitmsg[0]); 
						}
					}
					return existfileid;
				}
				
				
			     // function: send msg
				public void sendMessage(String msg)
				{
					try{
						out.writeObject(msg);
						out.flush();
						//System.out.println("Send message: " + msg + " to Client ");
					}
					catch(IOException ioException){
						ioException.printStackTrace();
					}
				}	
				
				//function: make msg;
				String makeMsg(ArrayList filelist, String[] array)
				{
					// use string to create msg;
					String msg = "";
					int len = array.length;
					String[] twoArray = new String[len+filelist.size()];  
			        System.arraycopy(array, 0, twoArray, 0, array.length);  
			        System.arraycopy(filelist.toArray(new String[0]), 0, twoArray, array.length, filelist.size());
			        filelist.retainAll(Arrays.asList(array));
			        for(int i=0;i<filelist.size();i++)
			        {
			        	msg  = msg + filelist.get(i) + ",";
			        }
					if(msg == "")
					{
						msg = "none";
					}
					System.out.println("The final message is " + msg);
					return msg;
				}

		}	
		
		
		
		// create a new timer task
//		public static class C2Cconnection extends TimerTask
//		{
//			public void run() {
//				
//				p2pClient1 client1 = new p2pClient1();
//				int sizeall = client1.checkfilenum();
//				System.out.println("c1 run3");
//				//initialize as a server, which connects with Client2 in port 8012;
//				 C1download c1to2down = new C1download(8012);
//				 System.out.println("c1 run12");
//				 
//				 c1to2down.sendRequest(1);
//				 System.out.println("c1 run4");
//				// As a Client, client2 receive msg;
//		        
//
//		    }
//		}
		
		public static void main(String args[]) throws Exception 
		{
			// thread1 to receive all chunks sent from server.
			int c = 4;   // define this is client1;
			p2pClient4 client1 = new p2pClient4();
			CtoSrequest ctos1 = new CtoSrequest("localhost", 8084);
			int n = (client1.checkfilenum()) / 5;     // in eg, n=17
			int m = (client1.checkfilenum()) % 5;     // in eg, m=3
			int sizeall = client1.checkfilenum();
			//System.out.println("size is  " +client1.checkfilenum());
			if(m>=c)
			{
			    int j = 1;
			    while(j<=n+1)
			    {
			    	ctos1.run();
			    	j++;
			    };
			}
			else
			{
				int j = 1;
			    while(j<=n)
			    {
			    	ctos1.run();
			    	j++;
			    }
			}	
			System.out.println("c1 run1");
			
			
			// handle new socket from client 2, which port number is 8010
			ServerSocket server = new ServerSocket(8040);  // listen to port 8012
			while (true) {  
		         //accept  
		         Socket socket = server.accept();  
		         //every time recevie a new socket;  
		         System.out.println("c1 run2");
		         new Thread(new Task(socket,8040,4)).start();  
		         System.out.println("c1 run3");
		      }  
			// set a timer to repeat the schedule
//			Timer timer = new Timer();
//			timer.schedule(new C2Cconnection(),1000, 60000);  //delay 1s, period 60s;
			
			 //if client2 has full chunks,stop;
//			 String[] all = new String[sizeall];
//				for(int i=0;i<sizeall;i++)
//				{
//					all[i] = String.valueOf(i);
//				}
//				System.out.println("c1 run2");
//				
//			 int size=client1.checkfile("e://2015fall/computer network/project/client2/data").size();   
//			 String[] allin2=new String[size]; 
//			 for(int i=0;i<client1.checkfile("e://2015fall/computer network/project/client2/data").size();i++)
//				{     
//				      System.out.println("c1 run5");
//				      allin2[i]=(String)client1.checkfile("e://2015fall/computer network/project/client2/data").get(i);  
//				}  
//			 if(Arrays.equals(all,allin2))
//			 {
//				 timer.cancel();
//			 }
//			//check if Client1 has all chunks;  try do-while
//			String[] all = new String[sizeall];
//			for(int i=0;i<sizeall;i++)
//			{
//				all[i] = String.valueOf(i);
//			}
//			System.out.println("c1 run2");
			
			//Client 1 first initialized as a server;
//			Boolean flagequal = true;
//			 while(flagequal)
//			 {  
//				System.out.println("c1 run3");
//				//initialize as a server, which connects with Client2 in port 8012;
//				 C1download c1to2down = new C1download(8012);
//				 System.out.println("c1 run12");
//				 
//				 c1to2down.sendRequest(1);
//				 System.out.println("c1 run4");
//				// As a Client, client2 receive msg;
//				
//				 
//				 
//				 //if client2 has full chunks,stop;
//				 int size=client1.checkfile("e://2015fall/computer network/project/client2/data").size();   
//				 String[] allin2=new String[size]; 
//				 for(int i=0;i<client1.checkfile("e://2015fall/computer network/project/client2/data").size();i++)
//					{     
//					      System.out.println("c1 run5");
//					      allin2[i]=(String)client1.checkfile("e://2015fall/computer network/project/client2/data").get(i);  
//					}  
//				 if(Arrays.equals(all,allin2))
//				 {
//					 flagequal = false;
//				 }
				 
				 
//			flagequal = true;
//			while(flagequal)
//			{
//				 
//				 // initialize as a server, which connects with Client5 in port 8015;
//				 C1download c1to5down = new C1download(8015);
//				 c1to5down.sendRequest(1);
//				 System.out.println("c1 run6");
//				// As a Client, client2 receive msg;
//				 
//				 System.out.println("c1 run7");
//				//if client2 has full chunks,stop;
//				 int size=client1.checkfile("e://2015fall/computer network/project/client5/data").size();   
//				 String[] allin5=new String[size]; 
//				 for(int i=0;i<client1.checkfile("e://2015fall/computer network/project/client5/data").size();i++)
//					{  
//					      System.out.println("c1 run8");
//					      allin5[i]=(String)client1.checkfile("e://2015fall/computer network/project/client5/data").get(i);  
//					}  
//				 if(Arrays.equals(all,allin5))
//				 {
//					 System.out.println("c1 run9");
//					 flagequal = false;
//				 }
//			}
			
		}

		}


