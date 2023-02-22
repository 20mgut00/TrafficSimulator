package simulator.model;

import java.util.List;

public class MostCrowdedStrategy implements LightSwitchingStrategy {
	private int time;
	public MostCrowdedStrategy(int timeSlot) {
		time = timeSlot;
	}
	
	public int chooseNextGreen(List<Road> roads, List<List<Vehicle>> qs, int currGreen, int lastSwitchingTime, int currTime) {
		if(roads.isEmpty()) return -1;
		if(currGreen == -1) return buscar(0, qs);
		//
		if(currTime - lastSwitchingTime <= time) return currGreen;
		return buscar((currGreen + 1) % roads.size(), qs);
	}
	
	private int buscar(int pos, List<List<Vehicle>> qs) {
		int x = qs.get(pos).size();
		for(int i = 1; i < qs.size(); i++) {
			if(x < qs.get(i).size()) x = qs.get(i).size();
		}
		return x;
	}
}
