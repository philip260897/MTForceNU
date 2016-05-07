package org.mtforce.network;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InfoPackage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2156667136801207074L;

	private List<String> sensors = new ArrayList<String>();
	private List<String> names = new ArrayList<String>();
	private List<Double> values = new ArrayList<Double>();
	
	public InfoPackage() {
		super();
	}
	
	public void addInfo(String sensor, String name, double value)
	{
		sensors.add(sensor);
		names.add(name);
		values.add(value);
	}

	public List<String> getSensors() {
		return sensors;
	}

	public List<String> getNames() {
		return names;
	}

	public List<Double> getValues() {
		return values;
	}
	
	
}
