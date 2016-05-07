package org.mtforce.network;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server 
{
	private ServerSocket serverSocket;
	private Socket clientSocket;
	
	private ObjectOutputStream oo;
	private ObjectInputStream oi;
	
	public Server(int port)
	{
	    try {
	      serverSocket = new ServerSocket(port);
	      clientSocket = serverSocket.accept();
	      System.out.println("Client " + clientSocket.getInetAddress());
	      oo = new ObjectOutputStream(clientSocket.getOutputStream());
	      oi = new ObjectInputStream(clientSocket.getInputStream());

	      
	    } 
	    catch (IOException ioe) 
	    {
	      System.out.println(ioe);
	    }
	}
	
	public Socket getClient()
	{
		return clientSocket;
	}
	
	public void close()
	{
		try{
		clientSocket.close();
		serverSocket.close();
		}catch(Exception ex) {ex.printStackTrace();}
	}
	
	public void write(InfoPackage pkg) throws Exception
	{
		oo.writeObject(pkg);
		oo.flush();
	}
	
	public CmdPackage read() throws Exception
	{
		CmdPackage dd = (CmdPackage) oi.readObject();
		return dd;
	}
}
