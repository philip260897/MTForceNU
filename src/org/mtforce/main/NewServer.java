package org.mtforce.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class NewServer implements Runnable
{
	private ServerSocket serverSocket;
	private Socket clientSocket;
	
	private BufferedWriter oo;
	private BufferedReader oi;
	private int port;
	private Thread thread;
	
	private CmdReceivedEvent event;
	private boolean running = false;
	
	public NewServer(int port)
	{
		this.port = port;
	}
	
	public void setCmdReceivedEvent(CmdReceivedEvent event) {
		this.event = event;
	}
	
	public void startServer()
	{
	    try {
		      serverSocket = new ServerSocket(port);
		      clientSocket = serverSocket.accept();
		      System.out.println("[Server] Client connected " + clientSocket.getInetAddress());
		      oo = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		      oi = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		      running = true;
		      thread = new Thread(this);
		      thread.start();
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
		running = false;
		thread.stop();
		}catch(Exception ex) {ex.printStackTrace();}
	}
	
	public void writeLine(String line)
	{
		try{
			oo.write(line+"\n");
			oo.flush();
		}catch(Exception ex){ex.printStackTrace(); close();}
	}
	
	public String read() throws Exception
	{
		return oi.readLine();
	}
	
	public void sendStatus(SensorEnum sensor, SensorStatus status)
	{
		writeLine("STATUS "+sensor.name()+" "+status.name());
	}
	
	public void sendValue(SensorEnum sensor, String valueType, String value)
	{
		writeLine("VALUE "+sensor.name()+" "+valueType+" "+value);
	}

	public boolean isRunning() {
		return running;
	}
	
	@Override
	public void run() 
	{
		try
		{
			while(true)
			{
				String[] line = read().split(" ");
				if(line[0].equals("STATUS"))
				{
					SensorEnum sensor = SensorEnum.valueOf(line[1]);
					if(event != null) event.statusRequestCommand(sensor);
				}
				if(line[0].equals("VALUE"))
				{
					SensorEnum sensor = SensorEnum.valueOf(line[1]);
					if(event != null) event.valueRequestCommand(sensor);
				}
				if(line[0].equals("INIT"))
				{
					SensorEnum sensor = SensorEnum.valueOf(line[1]);
					if(event != null) event.initializeSensorCommand(sensor);
				}
				if(line[0].equals("INIT_ALL"))
				{
					if(event != null) event.initializeAll();
				}
				if(line[0].equals("LEDS_FILL"))
				{
					if(event != null) event.fillLedsCommand();
				}
				if(line[0].equals("LEDS_CLEAR"))
				{
					if(event != null) event.clearLedsCommand();
				}
				if(line[0].equals("LEDS_WRITE"))
				{
					if(event != null) event.writeLedCommand(line[1]);
				}
				if(line[0].equals("LEDS_BRIGHTNESS"))
				{
					if(event != null) event.setBrightnessCommand(Integer.parseInt(line[1]));
				}
				if(line[0].equals("LEDS_COLOR"))
				{
					if(event != null) event.setLedsColor(line[1]);
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println("[Server] Client disconnected!");
			ex.printStackTrace();
			close();
		}
	}
}
