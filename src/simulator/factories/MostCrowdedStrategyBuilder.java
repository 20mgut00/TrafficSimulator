package simulator.factories;

import org.json.JSONObject;

import simulator.model.LightSwitchingStrategy;
import simulator.model.MostCrowdedStrategy;

public class MostCrowdedStrategyBuilder extends Builder<LightSwitchingStrategy> {

	public MostCrowdedStrategyBuilder(String type) {
		super(type);
	}

	protected LightSwitchingStrategy createTheInstance(JSONObject data) {
		return new MostCrowdedStrategy(data.optInt("TimeSlot", 1));
	}

}
