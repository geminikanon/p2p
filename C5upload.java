import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;



public class C5upload {

	public void C5upload(){}
	
	
	//function to get fileid in given folder
	static ArrayList<String> checkfilecompare(String path)
	{
		
		File file = new File(path); 
		File[] array = file.listFiles();
		ArrayList existfileid = new ArrayList();
		ArrayList filenameid = new ArrayList();
		String exchange = "";
		for(int i=0;i<array.length;i++)
		{   
			System.out.println(array.length);
			if(array[i].isFile())
			{ 
				// add existed file id to a dynamic array;
				String fileid = array[i].getName();
				System.out.println(fileid);
				String[] splitmsg = fileid.split("\\.");
				//String fileid = filename.substring(filename.length()-7, filename.length()-4);
				//find the number instead of string;
//				if(fileid != null && !"".equals(fileid))  //
//				{
//					for(int j=0;j<fileid.length();j++)
//					{
//						//System.out.println(message.charAt(i));
//					    if(fileid.charAt(j)>=48 && fileid.charAt(j)<=57)
//					    {
//					    	 
//					         filenameid.add(String.valueOf(fileid.charAt(j)));
//					         
//					    }
//					}	
//				}
//				for(int x =0;x<filenameid.size();x++)
//				{
//					exchange = exchange + filenameid.get(i);
//				}
				System.out.println(splitmsg[0]);
			   existfileid.add(splitmsg[0]); 
			}
		}
		return existfileid;
	}
	// get all chunks' number
	static int checkfilenum()
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
		return fileno-1;
	}
	
	//combine all trunks together to one big file;
		void combine(String path)
		{
			int num = checkfilenum();
			try{
			File file=new File(path + "data.pdf");
			ArrayList<FileInputStream> al=new ArrayList<FileInputStream>();  
			for (int i=1;i<=num ; i++)  
	        {  
	            al.add(new FileInputStream(path+i+".pdf"));  
	        }  
			
			final  ListIterator<FileInputStream> it=al.listIterator();  
	        Enumeration<FileInputStream> en=new Enumeration<FileInputStream>()  
	        {  
	            public boolean hasMoreElements()  
	            {  
	                return it.hasNext();  
	            }  
	  
	            public FileInputStream nextElement()  
	            {  
	                return it.next();  
	            }  
	        };  
	        
	        SequenceInputStream sis=new SequenceInputStream(en);
	        FileOutputStream fos=new FileOutputStream(file); 
	        byte[] buf=new byte[1024*10000];  
	        int len=0;  
	        while((len=sis.read(buf))!=-1)  
	        {  
	            fos.write(buf,0,len); 
	        }  
	  
	        //close 
	        fos.close();  
	        sis.close();  
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
		}
		
	//client1 send chunkinfo to client 2 and client 5;
	private static class C1 extends Thread
	{   
		private String host;
	    private int cport;   
      	private String path;
      	private ObjectOutputStream out;         //stream write to the socket
      	private ObjectInputStream in;          //stream read from the socket
      	private String message;      //message send to the server
      	private String response;
      	private Socket requestSocket;
    	
      	
		public C1(String host1, int port1)
		{
			this.host = host1;
			this.cport = port1;
		}
		
		// get the exist file id in local client
		ArrayList<String> checkfile(int clientNum)
		{
			String path = "e://2015fall/computer network/project/client"+ clientNum + "/data";
			//System.out.println("e://2015fall/computer network/project/client"+ clientNum + "/data");
			File file = new File(path); 
			File[] array = file.listFiles();
			ArrayList existfileid = new ArrayList();
			ArrayList filenameid = new ArrayList();
			String exchange = "";
			for(int i=0;i<array.length;i++)
			{   
				System.out.println(array.length);
				if(array[i].isFile())
				{ 
					// add existed file id to a dynamic array;
					String fileid = array[i].getName();
					System.out.println(fileid);
					String[] splitmsg = fileid.split("\\.");
					//String fileid = filename.substring(filename.length()-7, filename.length()-4);
					//find the number instead of string;
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
					System.out.println(splitmsg[0]);
				   existfileid.add(splitmsg[0]); 
				}
			}
			return existfileid;
		}
		
		// make msg that need to send
		String makeMsg(ArrayList filelist)
		{
			// use string to create msg;
			int size = checkfilenum();
			String[] alllist = new String[size];   // 7 need to change;
			String msg = "";
			for(int j=0; j<size;j++)
			{
				alllist[j] = String.valueOf(j+1);
			}
			// find different elements;
			String[] twoArray = new String[alllist.length+filelist.size()];  
	        System.arraycopy(alllist, 0, twoArray, 0, alllist.length);  
	        System.arraycopy(filelist.toArray(new String[0]), 0, twoArray, alllist.length, filelist.size());
	        filelist.retainAll(Arrays.asList(alllist));
	        List<String> twoList = new ArrayList<String>();  
	        twoList.addAll(Arrays.asList(twoArray));  
	        twoList.removeAll(filelist); 
	        
	        // turn list to array
	        for(String str : twoList) 
	        { 
	        	msg = msg + str + ","; 
	        }
			return msg;
		}
		
		//function to send msg
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
		

		//send request to client 2 and 5;
		void sendRequest(int cnum)
		{
		 try{
			requestSocket = new Socket(host, cport);
			System.out.println("Client5 has connected with client " +cnum + "in port "+ cport);
			
			System.out.println("run12");
			
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			ArrayList need = new ArrayList();
			
			 
			message = makeMsg(checkfile(cnum));
			System.out.println(message);
			sendMessage(message);
			response = (String)in.readObject(); //get response from server;
			System.out.println(response);
			if(response =="none")                 //There is no file to send;
			{
				System.out.println("Wait for response...");
				//sendRequest(cnum);
			}
			else
			{
				response = response.trim();                   // delete the space;
				// divide the msg into several msg by ,
				String[] splitmsg = response.split(",");
				for(int i=0;i<splitmsg.length;i++)
				{
				    need.add(splitmsg[i]+".pdf");	
				}
			}
			System.out.println("run11");
			
			//write file in client 2 or 5;
			for(int i=0;i< need.size();i++)                      //write file
			{
			  System.out.println("arraylist "+ need.get(i));
			  File inputfile = new File((String) ("e://2015fall/computer network/project/client5/data/"+need.get(i)));
			  InputStream reader = new FileInputStream(inputfile);
			  BufferedInputStream br = new BufferedInputStream(reader);
			  System.out.println("run2 ");
			  File outputfiledir = new File("e:/2015fall/computer network/project/client"+cnum+"/data");
              if(!outputfiledir.exists())
			  {
			      outputfiledir.mkdirs();
	          }
            // System.out.println("run3 ");
              File outputfile = new File("e://2015fall/computer network/project/client"+cnum+"/data/" + (String)(need.get(i)));
              outputfile.createNewFile();
	          OutputStream writer = new FileOutputStream(outputfile);
	          BufferedOutputStream bufwriter = new BufferedOutputStream(writer); 

	          while(true)
	          {  
	        	  int j = br.read();
	        	  if(j == -1)
	        	  {
	        		  break;
	        	  }
	        	  bufwriter.write(j);
	          }
	          System.out.println("run15");
	         // System.out.println("run5 ");
	          bufwriter.flush();
	          reader.close();
	          br.close();
	          writer.close();
	          bufwriter.close();
	          //System.out.println("run6 ");
			}
	       
			
		
			
			
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
			try{
			  Thread.sleep(10000);
			}
			catch(InterruptedException e){    
				System.out.println("Interrupted");    
				}
			
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
    }
	}
		// get number of chunks in client2 and 5;

		
		
		
		
  
	
	//define a timer;
//	public static class C2Cupload extends TimerTask
//	{
//		public void run() {
//			
//            	C1 c1to2up = new C1("localhost", 8012);
//			    c1to2up.sendRequest(2);
//	         
//			    C1 c1to5up = new C1("localhost", 8015);
//			    c1to5up.sendRequest(5);
//
//	    }
//	}
	
	public static void main(String args[]) throws Exception 
	{
		// define a new instance of client2 and 5
		C5upload client1 = new C5upload();
		int sizeall = client1.checkfilenum();
		
		//connect with Server1;
		Boolean flag = true;
		while(flag)
		{
		C1 c1to2 = new C1("localhost", 8050);
		c1to2.sendRequest(4);
		C1 c1to5 = new C1("localhost", 8050);
		c1to5.sendRequest(1);
		
		//check whether the chunk in the folder has all files
		String[] all = new String[sizeall];
		for(int i=0;i<sizeall;i++)
		{
			all[i] = "i+1";
		}
		System.out.println("c1 run2");
		
		//check all files now in different chunks;
		// in client2;
		//int size=client1.checkfilecompare("e://2015fall/computer network/project/client2/data").size();  
		
		String[] allin2=new String[client1.checkfilecompare("e://2015fall/computer network/project/client4/data").size()]; 
		for(int i=0;i<client1.checkfilecompare("e://2015fall/computer network/project/client4/data").size();i++)
		{     
			      System.out.println("c1 run5");
			      allin2[i]=(String)client1.checkfilecompare("e://2015fall/computer network/project/client4/data").get(i);  
		}  
		String[] allin5=new String[client1.checkfilecompare("e://2015fall/computer network/project/client1/data").size()]; 
		for(int i=0;i<client1.checkfilecompare("e://2015fall/computer network/project/client1/data").size();i++)
		{     
				      System.out.println("c1 run5");
				      allin5[i]=(String)client1.checkfilecompare("e://2015fall/computer network/project/client1/data").get(i);  
		}  
		 if(Arrays.equals(all,allin2) && Arrays.equals(all,allin5))
		 {
			 flag = false;
			 
			 client1.combine("e://2015fall/computer network/project/client2/data/");
			 client1.combine("e://2015fall/computer network/project/client5/data/");
		 }
		 
		 // send request to 5
		 
			
			//check whether the chunk in the folder has all files
	
		
		 
		 
		}
//	    // Create a timer;
//		Timer timer = new Timer();
//		timer.schedule(new C2Cupload(),1000, 70000);  //delay 1s, period 60s;
	 
		
	

//	    // open client5 thread
//	 //if client2 has full chunks,stop;
//		 int size=client1.checkfile("e://2015fall/computer network/project/client2/data").size();   
//		 String[] allin2=new String[size]; 
//		 for(int i=0;i<client1.checkfile("e://2015fall/computer network/project/client2/data").size();i++)
//			{     
//			      System.out.println("c1 run5");
//			      allin2[i]=(String)client1.checkfile("e://2015fall/computer network/project/client2/data").get(i);  
//			}  
//		 if(Arrays.equals(all,allin2))
//		 {
//			 flagequal = false;
//		 }
//	}
//	
//	flagequal = true;
//	while(flagequal)
//	{
//	   C1 c1to5up = new C1("localhost", 8015);
//	   c1to5up.sendRequest(5);
//	   int size=client1.checkfile("e://2015fall/computer network/project/client5/data").size();   
//		 String[] allin5=new String[size]; 
//		 for(int i=0;i<client1.checkfile("e://2015fall/computer network/project/client5/data").size();i++)
//			{  
//			      System.out.println("c1 run8");
//			      allin5[i]=(String)client1.checkfile("e://2015fall/computer network/project/client5/data").get(i);  
//			}  
//		 if(Arrays.equals(all,allin5))
//		 {
//			 System.out.println("c1 run9");
//			 flagequal = false;
//		 }
//	}
	}
}

