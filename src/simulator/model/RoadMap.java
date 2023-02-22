package simulator.model;


import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.exception.WrongObjectException;

public class RoadMap {
	
	private List<Junction> cruces;
	private List<Road> carreteras;
	private List<Vehicle> vehiculos;
	private Map<String, Junction> mapCruces;
	private Map<String, Road> mapCarreteras;
	private Map<String, Vehicle> mapVehiculos;
	
	public RoadMap() {
		cruces = new LinkedList<Junction>();
		carreteras = new LinkedList<Road>();
		vehiculos = new LinkedList<Vehicle>();
		mapCruces = new HashMap<String, Junction>();
		mapCarreteras = new HashMap<String, Road>();
		mapVehiculos = new HashMap<String, Vehicle>();
	}
	
	void addJunction(Junction j) {
		if(!cruces.contains(j)) {
			cruces.add(j);
			mapCruces.put(j.getId(), j);
		}
	}
	
	void addRoad(Road r) throws WrongObjectException {
		try {
			if(!carreteras.contains(r)) {
				if(mapCruces.containsValue(r.cruce_ini) && mapCruces.containsValue(r.cruce_fin)) {
					r.getSrc().addOutGoingRoad(r);
					r.getDest().addIncommingRoad(r);
					carreteras.add(r);
					mapCarreteras.put(r.getId(), r);
				}
				else throw new WrongObjectException("[RoadMap] These junctions doesn't exist\n");
			}
			else throw new WrongObjectException("[RoadMap] This road already exists\n");
		}catch (WrongObjectException e) {
			throw new WrongObjectException(e.getMessage());
		}
	}
	
	void addVehicle(Vehicle v) throws WrongObjectException {
		int i = 0;
		boolean encontrado = false;
		
		try {
			if(!vehiculos.contains(v)) {
				while(v.getItinerario().size() > i && !encontrado) {
					if(!carreteras.containsAll(v.getItinerario().get(i).in_road)) encontrado = true;
					i++;
				}
				if(!encontrado) {
					vehiculos.add(v);
					mapVehiculos.put(v.getId(), v);
				}
				else throw new WrongObjectException("This itineray is wrong\n");
			}
			else throw new WrongObjectException("This vehicle already exists\n");
			
		}catch (WrongObjectException e) {
			throw new WrongObjectException(e.getMessage());
		}
	}
	
	public Junction getJunction(String id) {
		if(mapCruces.containsKey(id)) return mapCruces.get(id);
		else return null;
	}
	
	public Road getRoad(String id) {
		if(mapCarreteras.containsKey(id)) return mapCarreteras.get(id);
		else return null;
	}
	
	public Vehicle getVehicle(String id) {
		if(mapVehiculos.containsKey(id)) return mapVehiculos.get(id);
		else return null;
	}
	
	public List<Junction> getJunctions(){
		return Collections.unmodifiableList(cruces);
	}
	
	public List<Road> getRoads(){
		return Collections.unmodifiableList(carreteras);
	}
	
	public List<Vehicle> getVehicles(){
		return Collections.unmodifiableList(vehiculos);
	}
	
	void reset() {
		cruces.clear();
		carreteras.clear();
		vehiculos.clear();
		mapCruces.clear();
		mapCarreteras.clear();
		mapVehiculos.clear();
	}
	
	public JSONObject report() {
		JSONObject jo = new JSONObject();
		JSONArray ja1 = new JSONArray();
		JSONArray ja2 = new JSONArray();
		JSONArray ja3 = new JSONArray();
		for(Junction i : cruces) {
			ja1.put(i.report());
		}
		jo.put("junctions", ja1);
		for(Road i : carreteras) {
			ja2.put(i.report());
		}
		jo.put("road", ja2);
		for(Vehicle i : vehiculos) {
			ja3.put(i.report());
		}
		jo.put("vehicles", ja3);
		return jo;
	}
}
