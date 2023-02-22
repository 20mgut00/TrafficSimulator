package simulator.control;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.factories.Factory;
import simulator.misc.SortedArrayList;
import simulator.model.Event;
import simulator.model.Observable;
import simulator.model.TrafficSimObserver;
import simulator.model.TrafficSimulator;
import simulator.model.exception.WrongValueException;

public class Controller implements Observable<TrafficSimObserver> {
	
	TrafficSimulator traffic_simulator;
	Factory<Event> events_factory;
	
	SortedArrayList<Event> eventIni;	//Extension
	
	public Controller(TrafficSimulator sim, Factory<Event> eventsFactory) {
		try {
			if(sim != null && eventsFactory != null) {
				traffic_simulator = sim;
				events_factory = eventsFactory;
			}
			else throw new WrongValueException("Parameters must not be null");
		}catch(WrongValueException e) {
			System.err.format(e + e.getMessage());
		}
	
	}
	
	public void loadEvents(InputStream in) {
		JSONObject jo = new JSONObject(new JSONTokener(in));
		JSONArray ja = jo.getJSONArray("events");
		for(int i = 0; i < ja.length(); i++) {
			addEvent(events_factory.createInstance(ja.getJSONObject(i)));
		}
		//copia de la lista de eventos inicial para usarla al resetear
		eventIni = (SortedArrayList<Event>) traffic_simulator.getEventos().clone();		//Extension
	}
	
	public void run(int n, OutputStream out) {
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		
		for(int i = n; i > 0; i--) {
			try {
				traffic_simulator.advance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			ja.put(traffic_simulator.report());
		}
		jo.put("states", ja);
		if(out != null) {
			PrintStream p = new PrintStream(out);
			p.print(jo.toString(3));
			p.close();
		}
	}
	
	public void reset() {		//Extension
		traffic_simulator.reset();
	}
	
	public void addEvent(Event e) {
		traffic_simulator.addEvent(e);
	}
	
	public SortedArrayList<Event> getEventIni(){	//Extension
		return eventIni;
	}
	public void setTime(int t) {
		traffic_simulator.setTime(t);
	}
	public JSONObject report() {
		return traffic_simulator.report();
	}
	public void setEventos(SortedArrayList<Event> eventos) {
		traffic_simulator.setEventos(eventos);
	}
	

	@Override
	public void addObserver(TrafficSimObserver o) {
		traffic_simulator.addObserver(o);
	}

	@Override
	public void removeObserver(TrafficSimObserver o) {
		traffic_simulator.removeObserver(o);
	}
	
}
