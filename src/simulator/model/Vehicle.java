package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import simulator.model.exception.WrongValueException;

public class Vehicle extends SimulatedObject {
	private List<Junction> itinerario;
	private int v_max, v_now, localizacion = 0, g_contam, t_contam, dist_recorrida = 0;
	private VehicleStatus estado;
	private Road carretera;
	
	protected Vehicle(String id, int maxSpeed, int contClass, List<Junction> itinerary) throws WrongValueException {
		super(id);
		estado = VehicleStatus.PENDING;
		try {
			if(maxSpeed > 0 && contClass >= 0 && contClass <= 10 && itinerary.size() >= 2) {
				itinerario = Collections.unmodifiableList(new ArrayList<>(itinerary));
				v_max = maxSpeed;
				g_contam = contClass;
			}
			else throw new WrongValueException();
		}catch(WrongValueException e) {
			throw new WrongValueException("[Vehicle] MaxSpeed must be > 0, ContClass must be between 0 and 10, itinerary's size must be >= 2\\n");
		}
	}
	
	@Override
	void advance(int time) throws Exception {
		int l_ini = localizacion, c;
		if (estado.equals(VehicleStatus.TRAVELING)) {
			localizacion += v_now;
			if(localizacion > carretera.getLength()) localizacion = carretera.getLength();
			dist_recorrida += localizacion - l_ini;
			int mov = localizacion - l_ini;
			c = g_contam * mov;
			t_contam += c;
			carretera.addContamination(c);
			
			if(localizacion == carretera.getLength()) {
				estado = VehicleStatus.WAITING;
				setSpeed(0);
				carretera.getDest().enter(this);
			}
		}
	}

	void moveToNextRoad() throws Exception {
		try {
			if(estado.equals(VehicleStatus.PENDING) || estado.equals(VehicleStatus.WAITING)) {
				if (estado.equals(VehicleStatus.PENDING)) {
					carretera = itinerario.get(0).roadTo(itinerario.get(1));
					localizacion = 0;
					estado = VehicleStatus.TRAVELING;
					carretera.enter(this);
				}
				else {
					int i =	itinerario.indexOf(carretera.getDest());
					carretera.exit(this);
					if(i < itinerario.size() - 1) {
						carretera = itinerario.get(i).roadTo(itinerario.get(i+1));
						localizacion = 0;
						v_now = 0;
						estado = VehicleStatus.TRAVELING;
						carretera.enter(this);
					}
					else estado = VehicleStatus.ARRIVED;
				}
			}
			else throw new WrongValueException("[Vehicle] State must be Pending or Waiting\\n");
		}catch(WrongValueException e) {
			throw new WrongValueException(e.getMessage());
		}		
	}
	
	public JSONObject report() {
		JSONObject jo = new JSONObject();
		jo.put("id", super._id);
		jo.put("speed", v_now);
		jo.put("distance", dist_recorrida);
		jo.put("co2", t_contam);
		jo.put("class", g_contam);
		jo.put("status", estado);
		if(estado.equals(VehicleStatus.TRAVELING) || estado.equals(VehicleStatus.WAITING)){
			jo.put("road", carretera);
			jo.put("location", localizacion);
		}
		return jo;
	}
	
	public int getDistance() {
		return dist_recorrida;
	}
	public void setDistance(int d) {
		dist_recorrida = d;
	}
	
	public int getMaxSpeed() {
		return v_max;
	}
	public int getTotalContamination() {
		return t_contam;
	}
	public void setTotalContamination(int c) {
		t_contam = c;
	}
	public VehicleStatus getStatus() {
		return estado;
	}
	public void setStatus(VehicleStatus s) {
		estado = s;
	}
	public void setSpeed(int s) throws WrongValueException {
		try {
			if(s >= 0){
				if(s > v_max) v_now = v_max;
				else v_now = s;
			}
			else throw new WrongValueException();
		}catch (WrongValueException e) {
			throw new WrongValueException("[Vehicle] Speed must be > 0\\n");
		}
	}
	public int getSpeed() {
		return v_now;
	}
	
	public int getLocation() {
		return localizacion;
	}
	public void setLocation(int l) {
		localizacion = l;
	}
	
	public List<Junction> getItinerario(){
		return Collections.unmodifiableList(itinerario);
	}
	public void setContaminationClass(int c) throws WrongValueException {
		try {
			if(c >= 0 && c <= 10) {
				g_contam = c;				
			}
			else throw new WrongValueException();
		} catch (WrongValueException e) {
			throw new WrongValueException("[Vehicle] Contamination mus be between 0 and 10\n");
		}
	}
	
	public int getContaminationClass() {
		return g_contam;
	}
	
	public Road getRoad() {
		return carretera;
	}
	public void setRoad(Road r) {
		carretera = r;
	}
	
	
}
