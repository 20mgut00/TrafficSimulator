package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.DequeuingStrategy;
import simulator.model.Event;
import simulator.model.LightSwitchingStrategy;
import simulator.model.NewJunctionEvent;
import simulator.model.exception.BuilderException;

public class NewJunctionEventBuilder extends Builder<Event> {

	Factory<LightSwitchingStrategy> ls_Strategy;
	Factory<DequeuingStrategy> dq_Strategy;
	
	public NewJunctionEventBuilder(Factory<LightSwitchingStrategy> ls_Strategy, Factory<DequeuingStrategy> dq_Strategy) {
		super("new_junction");
		this.ls_Strategy = ls_Strategy;
		this.dq_Strategy = dq_Strategy;
	}

	protected Event createTheInstance(JSONObject data) throws ClassCastException {
		LightSwitchingStrategy ls = ls_Strategy.createInstance(data.getJSONObject("ls_strategy"));
		DequeuingStrategy dq = dq_Strategy.createInstance(data.getJSONObject("dq_strategy"));
		Integer time = data.getInt("time");
		String id = data.getString("id");
		JSONArray coor = data.getJSONArray("coor");
		try {
			if(time != null && id != null && ls != null && dq != null && !coor.isEmpty()) {
				return new NewJunctionEvent(time, id, ls, dq, coor.getInt(0), coor.getInt(1));
			}
			throw new BuilderException("JSON parameters may be null");
		}catch(BuilderException e) {
			System.err.format(e + e.getMessage());
			return null;
		}		
	}

}
