package simulator.model;

public class InterCityRoad extends Road {

	protected InterCityRoad(String id, Junction srcJunc, Junction destJunc, int maxSpeed, int contLimit, int length,
			Weather weather) throws Exception{
		super(id, srcJunc, destJunc, maxSpeed, contLimit, length, weather);
	}

	void reduceTotalContamination() {
		int x;
		if(clima.equals(Weather.SUNNY)) x = 2;
		else if(clima.equals(Weather.CLOUDY)) x = 3;
		else if(clima.equals(Weather.RAINY)) x = 10;
		else if(clima.equals(Weather.WINDY)) x = 15;
		else x = 20;
		t_contam = (int)(((100.0 - x) / 100.0)*t_contam);
	}
	
	void updateSpeedLimit() {
		if(l_contam < t_contam) v_limite = (int)(v_max * 0.5);
		else v_limite = v_max;
	}
	
	int calculateVehicleSpeed(Vehicle v) {
		if(clima.equals(Weather.STORM)) return (int)(v_limite * 0.8);
		else return v_limite;
	}
	
}
