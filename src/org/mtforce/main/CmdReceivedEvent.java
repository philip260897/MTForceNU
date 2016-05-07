package org.mtforce.main;

public interface CmdReceivedEvent 
{
	public void initializeSensorCommand(SensorEnum sensor);
	
	public void initializeAll();
	
	public void statusRequestCommand(SensorEnum sensor);
	
	public void valueRequestCommand(SensorEnum sensor);
	
	public void writeLedCommand(String text);
	
	public void setBrightnessCommand(int brightness);
	
	public void fillLedsCommand();
	
	public void clearLedsCommand();
	
	public void setLedsColor(String color);
}
