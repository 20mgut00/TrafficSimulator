package simulator.model;

public abstract class NewRoadEvent extends Event {

	String id, srcJun, destJunc, weather;
	int length, co2Limit, maxSpeed;
	public NewRoadEvent(int time, String id, String srcJun, String destJunc, int length, int co2Limit, int maxSpeed, String weather) {
		super(time);
		this.id = id;
		this.srcJun = srcJun;
		this.destJunc = destJunc;
		this.length = length;
		this.co2Limit = co2Limit;
		this.maxSpeed = maxSpeed;
		this.weather = weather;
	}

	abstract void execute(RoadMap map) throws Exception;
	
	@Override
	public String toString() {
		return "New Road Event '"+id+"'";
	}

}
