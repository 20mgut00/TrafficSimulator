package simulator.model;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.exception.WrongValueException;
import simulator.model.exception.WrongObjectException;
import simulator.model.exception.WrongStrategyException;

public class Junction extends SimulatedObject {

	List<Road> in_road;
	Map<Junction, Road> out_road;
	List<List<Vehicle>> colas;
	Map<Road, List<Vehicle>> carretera_cola;
	int i_green, last_change = 0, x, y;
	LightSwitchingStrategy cambioSemaforo;
	DequeuingStrategy extraerVehiculo;
	
	protected Junction(String id, LightSwitchingStrategy lsStrategy, DequeuingStrategy dqStrategy, int xCoor, int yCoor) throws Exception {
		super(id);
		in_road = new LinkedList<Road>();
		out_road = new HashMap<Junction, Road>();
		colas = new LinkedList<List<Vehicle>>();
		carretera_cola = new HashMap<Road, List<Vehicle>>();
		try {
			if(lsStrategy != null && dqStrategy != null) {
				cambioSemaforo = lsStrategy;
				extraerVehiculo = dqStrategy;
			}
			else throw new WrongStrategyException("[Junction] lsStrategy and dqStrategy must have a value\n");
			if(xCoor >= 0 && yCoor >= 0) {
				x = xCoor;
				y = yCoor;
			}
			else throw new WrongValueException ("[Junction] xCoor and yCoor must be >= 0\n");
		}catch(WrongStrategyException | WrongValueException e) {
			throw new Exception(e.getMessage());
		}
		
	}
	
	void addIncommingRoad(Road r) throws WrongObjectException {
		try {
			if(r.getDest() == this) {
				List<Vehicle> q = new LinkedList<Vehicle>();
				in_road.add(r);
				colas.add(q);
				carretera_cola.put(r, q);
			}
			else throw new WrongObjectException();
		}catch(WrongObjectException e) {
			throw new WrongObjectException("[Junction] getDest must return this Junction\n");
		}
	}
	
	void addOutGoingRoad(Road r) throws WrongObjectException {
		try {
			if(!out_road.containsValue(r)) {
				out_road.put(this, r);
			}
			else throw new WrongObjectException();
		}catch(WrongObjectException e) {
			throw new WrongObjectException("[Junction] The out road must contain this road\n");
		}
	}
	
	void enter(Vehicle v) {
		List<Vehicle> aux = new ArrayList<Vehicle>();
		for(Vehicle i : v.getRoad().getVehicle()) {
			if(i.getLocation() == i.getRoad().getLength()) aux.add(i);
		}
		carretera_cola.put(v.getRoad(), aux);
		colas.set(in_road.indexOf(v.getRoad()), aux);
	}
	
	Road roadTo(Junction j) {
		if(j.getInRoad().contains(out_road.get(this))) return out_road.get(this);
		else return null;
	}
	
	void advance(int time) throws Exception {
		List<Vehicle> aux = new LinkedList<Vehicle>();
		for(int i = 0; i < in_road.size(); i++) {
			if(i_green == i && !colas.get(i).isEmpty()) {
				aux = extraerVehiculo.dequeue(colas.get(i));
				for(int j = 0; j < aux.size(); j++) {
					aux.get(j).moveToNextRoad();
					if(!colas.get(i).isEmpty()) {
						colas.get(i).remove(j);
					}
				}
			}
		}
		int x = cambioSemaforo.chooseNextGreen(in_road, colas, i_green, last_change, time);
		if(x != i_green) {
			i_green = x;
			last_change = time - 1;
		}
	}

	public JSONObject report() {
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		jo.put("id", _id);
		if(i_green == -1) jo.put("green", "none");
		else jo.put("green", in_road.get(i_green).getId());
		if(!colas.isEmpty()) {
			for(int i = 0; i < colas.size(); i++) {
				JSONObject jo2 = new JSONObject();
				JSONArray ja2 = new JSONArray();
				jo2.put("road", in_road.get(i).getId());
				for(Vehicle j : colas.get(i)) {
					ja2.put(j.getId());
				}
				jo2.put("vehicles", ja2);
				ja.put(jo2);
			}
			jo.put("queues", ja);
		}
		else jo.put("queues", new JSONArray());
		return jo;
	}
	
	public String getColas(){
		String aux = "";
		for(int i = 0; i < colas.size(); i++) {
			aux += in_road.get(i).toString() +":"+ colas.get(i).toString() + " ";
		}
		return aux;
	}
	public List<Road> getInRoad(){
		return Collections.unmodifiableList(in_road);
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getGreenLightIndex() {
		return i_green;
	}
	public void setGreen(int i) {
		i_green = i;
	}
	
}
