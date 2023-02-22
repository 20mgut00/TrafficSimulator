package simulator.factories;

import org.json.JSONObject;

import simulator.model.Event;

public abstract class NewRoadEventBuilder extends Builder<Event> {

	public NewRoadEventBuilder(String type) {
		super(type);
	}
	
	protected abstract Event createTheInstance(JSONObject data);

}
