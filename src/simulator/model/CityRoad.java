package simulator.model;

public class CityRoad extends Road {

	protected CityRoad(String id, Junction srcJunc, Junction destJunc, int maxSpeed, int contLimit, int length, Weather weather) throws Exception {
		super(id, srcJunc, destJunc, maxSpeed, contLimit, length, weather);
	}

	void reduceTotalContamination() {
		if(clima.equals(Weather.WINDY) || clima.equals(Weather.STORM)) t_contam -= 10;
		else t_contam -= 2;
		if(t_contam < 0) t_contam = 0;
	}
	
	void updateSpeedLimit() {
		v_limite = v_max;
	}
	int calculateVehicleSpeed(Vehicle v) {
		int f = v.getContaminationClass(), s = v_limite;
		return (int)(((11.0 - f) / 11.0) * s);
	}
}
