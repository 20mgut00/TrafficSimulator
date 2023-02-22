package simulator.model;

import java.util.ArrayList;

import org.json.JSONObject;

import simulator.misc.SortedArrayList;

public class TrafficSimulator implements Observable<TrafficSimObserver>{
	private RoadMap map_road;
	private SortedArrayList<Event> eventos;
	private int time;
	private ArrayList<TrafficSimObserver> observers;
	
	public TrafficSimulator() {
		map_road = new RoadMap();
		eventos = new SortedArrayList<Event>();
		observers = new ArrayList<TrafficSimObserver>();
		time = 0;
	}
	
	public void addEvent(Event e) {
		eventos.add(e);
		for(TrafficSimObserver ob : observers) ob.onEventAdded(map_road, eventos, e, time);
	}
	
	public void advance() throws Exception  {
		try {
			time++;
			for(TrafficSimObserver ob : observers) ob.onAdvanceStart(map_road, eventos, time);
			//Ejecuta eventos y los elimina
			while (!eventos.isEmpty() && eventos.get(0).getTime() == time) {
		           eventos.get(0).execute(map_road);
		           eventos.remove(0);
		    }

			//Advance cruces
			for(Junction i : map_road.getJunctions()) {
				i.advance(time);
			}
			
			//Advance carreteras
			for(Road i : map_road.getRoads()) {
				i.advance(time);
			}
			for(TrafficSimObserver ob : observers) ob.onAdvanceEnd(map_road, eventos, time);
		}catch(Exception e) {
			for(TrafficSimObserver ob : observers) ob.onError(e.getMessage());
			throw new Exception(e);
		}
		
	}
	
	public void reset() {		//Extension
		time = 0;
		map_road.reset();
		eventos.clear();
		for(TrafficSimObserver ob : observers) ob.onReset(map_road, eventos, time);
	}
	
	public JSONObject report() {
		JSONObject jo = new JSONObject();
		jo.put("time", time);
		jo.put("state", map_road.report());
		
		return jo;
	}
	public void setTime(int n) {
		time = n;
	}
	public void setEventos(SortedArrayList<Event> eventos) {
		this.eventos = (SortedArrayList<Event>) eventos.clone();
	}
	
	public SortedArrayList<Event> getEventos() {
		return eventos;
	}

	@Override
	public void addObserver(TrafficSimObserver o) {
		if(!observers.contains(o)) {
			observers.add(o);
		}
		for(TrafficSimObserver ob : observers) ob.onRegister(map_road, eventos, time);
	}

	@Override
	public void removeObserver(TrafficSimObserver o) {
		if(observers.contains(o)) {
			observers.remove(o);
		}	
	}
}
