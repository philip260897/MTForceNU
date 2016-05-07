package org.mtforce.main;

import java.awt.EventQueue;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Random;

import org.mtforce.impatouch.LedColor;
import org.mtforce.impatouch.LedDriver;
import org.mtforce.interfaces.SPIManager;
import org.mtforce.network.CmdPackage;
import org.mtforce.network.InfoPackage;
import org.mtforce.network.Server;
import org.mtforce.sensors.ADC;
import org.mtforce.sensors.Barometer;
import org.mtforce.sensors.DOF9;
import org.mtforce.sensors.DistanceSensor;
import org.mtforce.sensors.HumiditySensor;
import org.mtforce.sensors.LightSensor;
import org.mtforce.sensors.Sensor;
import org.mtforce.sensors.Thermometer;
import org.mtforce.sensors.Sensors;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.system.NetworkInfo;
import com.pi4j.system.SystemInfo;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.I2C;
import com.pi4j.wiringpi.Spi;

public class Main 
{
	private static NewServer server;
	private static boolean enabled = false;
	private static Random random = new Random();
	
	private static LedDriver driver;
	
	public static void main(String[] args) 
	{	
		try
		{
			driver = LedDriver.getInstance();
			while(true)
			{

				
				server = new NewServer(25565);
				server.setCmdReceivedEvent(new CmdReceivedEvent(){
					
					@Override
					public void statusRequestCommand(SensorEnum sensor) {
						//System.out.println("[Server] Status request: "+sensor.name());
						/*if(enabled)
						{
							server.sendStatus(sensor, SensorStatus.RUNNING);
						}
						else
						{
							server.sendStatus(sensor, SensorStatus.NOT_INITIALIZED);
						}*/
						if(Sensors.isEnabled())
						{
							if(sensor == SensorEnum.ADC)
								if(Sensors.getAdc().isEnabled()) server.sendStatus(sensor, SensorStatus.RUNNING); else server.sendStatus(sensor, SensorStatus.INITIALIZATION_FAILED);
							if(sensor == SensorEnum.DOF9)
								if(Sensors.getDof9().isEnabled()) server.sendStatus(sensor, SensorStatus.RUNNING); else server.sendStatus(sensor, SensorStatus.INITIALIZATION_FAILED);
							if(sensor == SensorEnum.BAROMETER)
								if(Sensors.getBarometer().isEnabled()) server.sendStatus(sensor, SensorStatus.RUNNING); else server.sendStatus(sensor, SensorStatus.INITIALIZATION_FAILED);
							if(sensor == SensorEnum.DISTANCE_SENSOR)
								if(Sensors.getDistanceSensor().isEnabled()) server.sendStatus(sensor, SensorStatus.RUNNING); else server.sendStatus(sensor, SensorStatus.INITIALIZATION_FAILED);
							if(sensor == SensorEnum.LIGHT_SENSOR)
								if(Sensors.getLightSensor().isEnabled()) server.sendStatus(sensor, SensorStatus.RUNNING); else server.sendStatus(sensor, SensorStatus.INITIALIZATION_FAILED);
							if(sensor == SensorEnum.THERMOMETER)
								if(Sensors.getThermometer().isEnabled()) server.sendStatus(sensor, SensorStatus.RUNNING); else server.sendStatus(sensor, SensorStatus.INITIALIZATION_FAILED);
							if(sensor == SensorEnum.HUMIDITY_SENSOR)
								if(Sensors.getHumidity().isEnabled()) server.sendStatus(sensor, SensorStatus.RUNNING); else server.sendStatus(sensor, SensorStatus.INITIALIZATION_FAILED);
														
						}
						else
						{
							server.sendStatus(sensor, SensorStatus.NOT_INITIALIZED);
						}
					}
	
					@Override
					public void valueRequestCommand(SensorEnum sensor) {
						//System.out.println("[Server] Value request: "+sensor.name());
						//server.sendValue(sensor, "Random", (random.nextDouble()*100)+"");
						try{
						//server.sendValue(sensor, "Temperature", "25.8");
						if(sensor == SensorEnum.ADC)
						{
							ADC adc = Sensors.getAdc();
							if(adc.isEnabled())
							{
								adc.selectChannel(ADC.kgsCONF_SELECT_CH1);
								adc.startConversion();
								Thread.sleep(100);
								server.sendValue(sensor, "CHANNEL_1", round(adc.getVoltage(),3)+"V");
								adc.selectChannel(ADC.kgsCONF_SELECT_CH2);
								adc.startConversion();
								Thread.sleep(100);
								server.sendValue(sensor, "CHANNEL_2", round(adc.getVoltage(),3)+"V");
							}
						}
						if(sensor == SensorEnum.BAROMETER)
						{
							Barometer barometer = Sensors.getBarometer();
							if(barometer.isEnabled())
							{
								server.sendValue(sensor, "Temperatur", round(barometer.getTemperature(),2)+"");
								server.sendValue(sensor, "Druck", barometer.getPressure()+"");
							}
						}
						if(sensor == SensorEnum.DISTANCE_SENSOR)
						{
							DistanceSensor dist = Sensors.getDistanceSensor();
							if(dist.isEnabled())
							{
								server.sendValue(sensor, "Distanz", round(dist.getDistance(),2)+"m");
							}
						}
						if(sensor == SensorEnum.DOF9)
						{
							DOF9 dof = Sensors.getDof9();
							if(dof.isEnabled())
							{
								server.sendValue(sensor, "Gyro_X", dof.getGYRO_XOUT()+"");
								server.sendValue(sensor, "Gyro_Y", dof.getGYRO_YOUT()+"");
								server.sendValue(sensor, "Gyro_Z", dof.getGYRO_ZOUT()+"");
								server.sendValue(sensor, "Accel_X", dof.getACCEL_XOUT()+"");
								server.sendValue(sensor, "Accel_Y", dof.getACCEL_YOUT()+"");
								server.sendValue(sensor, "Accel_Z", dof.getACCEL_ZOUT()+"");
								
							}
						}
						if(sensor == SensorEnum.LIGHT_SENSOR)
						{
							LightSensor light = Sensors.getLightSensor();
							if(light.isEnabled())
							{
								server.sendValue(sensor, "Helligkeit", light.getBrightness());
							}
						}
						if(sensor == SensorEnum.THERMOMETER)
						{
							Thermometer therm = Sensors.getThermometer();
							if(therm.isEnabled())
							{
								server.sendValue(sensor, "Temperatur", therm.getTemperature()+"");
							}
						}
						if(sensor == SensorEnum.HUMIDITY_SENSOR)
						{
							HumiditySensor h = Sensors.getHumidity();
							if(h.isEnabled())
							{
								server.sendValue(sensor, "Luftfeuchtigkeit", round(h.getHumidityHoldMasterMode(),2)+"");
							}
						}
						}
						catch(Exception ex) {ex.printStackTrace();}
					}
	
					@Override
					public void initializeSensorCommand(SensorEnum sensor) {
						// TODO Auto-generated method stub
						System.out.println("[Server] Initializing Sensor: "+sensor.name());
					}
	
					@Override
					public void initializeAll() {
						try {
							Sensors.initialize();
							driver.initialize();
							driver.setShutdownModeAll(LedDriver.kgsSHDM_NORM_UC_FEAT);
							driver.setScanLimitAll(LedDriver.kgsSCAN_LIMIT_7);
							enabled = true;
							System.out.println("[Server] Initializing Sensors...");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void writeLedCommand(String text) {
						// TODO Auto-generated method stub
						driver.setAllLedsOnAll(false);
						driver.writeString(text);
						System.out.println("[Server] Writing to Leds: " +text);
					}

					@Override
					public void setBrightnessCommand(int brightness) {
						// TODO Auto-generated method stub
						driver.setGlobalIntensityAll(brightness);
						System.out.println("[Server] Setting brightness: "+brightness);
					}

					@Override
					public void fillLedsCommand() {
						// TODO Auto-generated method stub
						driver.setAllLedsOnAll(true);
						System.out.println("[Server] Setting all LEDs on");
					}

					@Override
					public void clearLedsCommand() {
						// TODO Auto-generated method stub
						driver.setAllLedsOnAll(false);
						System.out.println("[Server] Setting all LEDs off");
					}

					@Override
					public void setLedsColor(String color) {
						// TODO Auto-generated method stub
						LedColor co = LedColor.valueOf(color);
						driver.setGlobalColor(co);
						System.out.println("[Server] Setting LEDs color: "+color);
					}
					
				});
				System.out.println("[Server] Starting and waiting for client...");
				server.startServer();
				while(server.isRunning()) {
					Thread.sleep(1000);
				}
				if(enabled)
				{
				
					driver.setAllLedsOnAll(false);
					driver.setGlobalColor(LedColor.RED);
				}
				System.out.println("[Server] Restarting...");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private static void updateInfo() throws Exception
	{
		/*InfoPackage info = new InfoPackage();
		for(Sensor sensor : Sensors.getSensors())
		{
			if(!sensor.isEnabled())
				continue;
			String s = sensor.getClass().getSimpleName();
			if(sensor instanceof Ser7Seg)
			{
				info.addInfo(s, "Number", ((Ser7Seg) sensor).getNumber());
			}
			else if(sensor instanceof IOExpander)
			{
				info.addInfo(s, "Led", ((IOExpander) sensor).getLedOn() ? 1.0 : 0.0);
				info.addInfo("", "Button", ((IOExpander) sensor).getButtonState() ? 1.0 : 0.0);
			}
			else
			{
				info.addInfo(s, "Default", 0);
			}
			
			
		}*/
		//server.write(info);
	}

	private static void printInfo() throws IOException, InterruptedException, ParseException
	{
		 System.out.println("----------------------------------------------------");
	        System.out.println("HARDWARE INFO");
	        System.out.println("----------------------------------------------------");
	        System.out.println("Serial Number     :  " + SystemInfo.getSerial());
	        System.out.println("CPU Revision      :  " + SystemInfo.getCpuRevision());
	        System.out.println("CPU Architecture  :  " + SystemInfo.getCpuArchitecture());
	        System.out.println("CPU Part          :  " + SystemInfo.getCpuPart());
	        System.out.println("CPU Temperature   :  " + SystemInfo.getCpuTemperature());
	        System.out.println("CPU Core Voltage  :  " + SystemInfo.getCpuVoltage());
	        System.out.println("CPU Model Name    :  " + SystemInfo.getModelName());
	        System.out.println("Processor         :  " + SystemInfo.getProcessor());
	        System.out.println("Hardware Revision :  " + SystemInfo.getRevision());
	        System.out.println("Is Hard Float ABI :  " + SystemInfo.isHardFloatAbi());
	        System.out.println("Board Type        :  " + SystemInfo.getBoardType().name());
	        
	        System.out.println("----------------------------------------------------");
	        System.out.println("MEMORY INFO");
	        System.out.println("----------------------------------------------------");
	        System.out.println("Total Memory      :  " + SystemInfo.getMemoryTotal());
	        System.out.println("Used Memory       :  " + SystemInfo.getMemoryUsed());
	        System.out.println("Free Memory       :  " + SystemInfo.getMemoryFree());
	        System.out.println("Shared Memory     :  " + SystemInfo.getMemoryShared());
	        System.out.println("Memory Buffers    :  " + SystemInfo.getMemoryBuffers());
	        System.out.println("Cached Memory     :  " + SystemInfo.getMemoryCached());
	        System.out.println("SDRAM_C Voltage   :  " + SystemInfo.getMemoryVoltageSDRam_C());
	        System.out.println("SDRAM_I Voltage   :  " + SystemInfo.getMemoryVoltageSDRam_I());
	        System.out.println("SDRAM_P Voltage   :  " + SystemInfo.getMemoryVoltageSDRam_P());

	        System.out.println("----------------------------------------------------");
	        System.out.println("OPERATING SYSTEM INFO");
	        System.out.println("----------------------------------------------------");
	        System.out.println("OS Name           :  " + SystemInfo.getOsName());
	        System.out.println("OS Version        :  " + SystemInfo.getOsVersion());
	        System.out.println("OS Architecture   :  " + SystemInfo.getOsArch());
	        System.out.println("OS Firmware Build :  " + SystemInfo.getOsFirmwareBuild());
	        System.out.println("OS Firmware Date  :  " + SystemInfo.getOsFirmwareDate());
	        
	        System.out.println("----------------------------------------------------");
	        System.out.println("JAVA ENVIRONMENT INFO");
	        System.out.println("----------------------------------------------------");
	        System.out.println("Java Vendor       :  " + SystemInfo.getJavaVendor());
	        System.out.println("Java Vendor URL   :  " + SystemInfo.getJavaVendorUrl());
	        System.out.println("Java Version      :  " + SystemInfo.getJavaVersion());
	        System.out.println("Java VM           :  " + SystemInfo.getJavaVirtualMachine());
	        System.out.println("Java Runtime      :  " + SystemInfo.getJavaRuntime());
	     
	        System.out.println("----------------------------------------------------");
	        System.out.println("NETWORK INFO");
	        System.out.println("----------------------------------------------------");
	        
	        // display some of the network information
	        System.out.println("Hostname          :  " + NetworkInfo.getHostname());
	        for (String ipAddress : NetworkInfo.getIPAddresses())
	            System.out.println("IP Addresses      :  " + ipAddress);
	        for (String fqdn : NetworkInfo.getFQDNs())
	            System.out.println("FQDN              :  " + fqdn);
	        for (String nameserver : NetworkInfo.getNameservers())
	            System.out.println("Nameserver        :  " + nameserver);
	        
	        System.out.println("----------------------------------------------------");
	        System.out.println("CODEC INFO");
	        System.out.println("----------------------------------------------------");
	        System.out.println("H264 Codec Enabled:  " + SystemInfo.getCodecH264Enabled());
	        System.out.println("MPG2 Codec Enabled:  " + SystemInfo.getCodecMPG2Enabled());
	        System.out.println("WVC1 Codec Enabled:  " + SystemInfo.getCodecWVC1Enabled());

	        System.out.println("----------------------------------------------------");
	        System.out.println("CLOCK INFO");
	        System.out.println("----------------------------------------------------");
	        System.out.println("ARM Frequency     :  " + SystemInfo.getClockFrequencyArm());
	        System.out.println("CORE Frequency    :  " + SystemInfo.getClockFrequencyCore());
	        System.out.println("H264 Frequency    :  " + SystemInfo.getClockFrequencyH264());
	        System.out.println("ISP Frequency     :  " + SystemInfo.getClockFrequencyISP());
	        System.out.println("V3D Frequency     :  " + SystemInfo.getClockFrequencyV3D());
	        System.out.println("UART Frequency    :  " + SystemInfo.getClockFrequencyUART());
	        System.out.println("PWM Frequency     :  " + SystemInfo.getClockFrequencyPWM());
	        System.out.println("EMMC Frequency    :  " + SystemInfo.getClockFrequencyEMMC());
	        System.out.println("Pixel Frequency   :  " + SystemInfo.getClockFrequencyPixel());
	        System.out.println("VEC Frequency     :  " + SystemInfo.getClockFrequencyVEC());
	        System.out.println("HDMI Frequency    :  " + SystemInfo.getClockFrequencyHDMI());
	        System.out.println("DPI Frequency     :  " + SystemInfo.getClockFrequencyDPI());
	        
	            
	        System.out.println();
	        System.out.println();
	}
	
	public static double round(double value, int places) {
		return (double) Math.round(value * 100) / 100;
	}
}
