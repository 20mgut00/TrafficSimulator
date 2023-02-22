package simulator.factories;

import org.json.JSONObject;

import simulator.model.LightSwitchingStrategy;
import simulator.model.RoundRobinStrategy;

public class RoundRobinStrategyBuilder extends Builder<LightSwitchingStrategy> {

	public RoundRobinStrategyBuilder(String type) {
		super(type);
	}
	
	protected LightSwitchingStrategy createTheInstance(JSONObject data) {
		return new RoundRobinStrategy(data.optInt("timeslot", 1));
	}

}
