package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.exception.WrongObjectException;
import simulator.model.exception.WrongValueException;


public abstract class Road extends SimulatedObject {
	Junction cruce_ini, cruce_fin;
	int longitud, v_max, v_limite, l_contam, t_contam;
	Weather clima;
	List<Vehicle> vehiculo = new ArrayList<Vehicle>();
	
	protected Road(String id, Junction srcJunc, Junction destJunc, int maxSpeed, int contLimit, int length, Weather weather) throws WrongObjectException{
		super(id);
		try {
			if(srcJunc != null && destJunc != null && weather != null) {
				cruce_ini = srcJunc;
				cruce_fin = destJunc;
				clima = weather;
			}
			else throw new WrongObjectException("Parameters must not be null\n");
			
			if(maxSpeed > 0 && contLimit >= 0 && length > 0) {
				v_max = maxSpeed;
				l_contam = contLimit;
				longitud = length;
			}
			else throw new WrongObjectException("max_speed and length must be > 0, contLimit must be >= 0\n");		
		}catch(WrongObjectException e) {
			throw new WrongObjectException(e.getMessage());
		}
	}
	
	void enter(Vehicle v) throws WrongValueException {
		try {
			if(v.getLocation() == 0 && v.getSpeed() == 0) {
				vehiculo.add(v);
			}
			else throw new WrongValueException();
		}catch(WrongValueException e) {
			throw new WrongValueException("[Road] Location and speed must be 0\\n");
		}
	}
	
	void exit(Vehicle v) {
		vehiculo.remove(v);
	}
	
	@Override
	void advance(int time) throws Exception {
		reduceTotalContamination();
		updateSpeedLimit();
		for(Vehicle i : vehiculo) {
			i.setSpeed(calculateVehicleSpeed(i));
			i.advance(time);
		}
		Collections.sort(vehiculo, new Comparator<Vehicle>() {
			@Override
			public int compare(Vehicle v1, Vehicle v2) {
				if(v1.getLocation() > v2.getLocation()) return 1;
				else if(v1.getLocation() < v2.getLocation()) return -1;
				else {
					return v1.getId().compareToIgnoreCase(v2.getId());
				}
			}
		});
	}

	@Override
	public JSONObject report() {
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		jo.put("id", super._id);
		jo.put("speedlimit", v_limite);
		jo.put("weather", clima);
		jo.put("co2", t_contam);
		for(Vehicle i : vehiculo) {
			ja.put(i);
		}
		jo.put("vehicles", ja);
		return jo;
	}
	
	void addContamination(int c) {
		try {
			if(c >= 0) {
				t_contam += c;
			}
			else throw new WrongValueException("contamination must be >= 0\n");
		}catch(WrongValueException e) {
			System.err.format(e + e.getMessage());
		}
	}
	
	
	public double getTotalCO2() {
		return t_contam;
	}
	public void setT_contam(int t_contam) {
		this.t_contam = t_contam;
	}
	
	public int getMaxSpeed() {
		return v_max;
	}
	public void setV_max(int v) {
		v_max = v;
	}
	
	public List<Vehicle> getVehicle(){
		return Collections.unmodifiableList(vehiculo);
	}
	public int getSpeedLimit() {
		return v_limite;
	}
	public void setWeather(Weather w) throws WrongObjectException {
		try {
			if(w != null) {
				clima = w;
			}
			else throw new WrongObjectException();
		}catch(WrongObjectException e) {
			throw new WrongObjectException("[Road] Weather must not be null\n");
		}
	}
	public Weather getClima() {
		return clima;
	}
	public int getLength() {
		return longitud;
	}
	
	public Junction getDest() {
		return cruce_fin;
	}
	
	public Junction getSrc() {
		return cruce_ini;
	}
	public double getCO2Limit() {
		return l_contam;
	}
	abstract void reduceTotalContamination();
	abstract void updateSpeedLimit();
	abstract int calculateVehicleSpeed(Vehicle v);
}
