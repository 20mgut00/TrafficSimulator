package simulator.factories;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.Event;
import simulator.model.NewVehicleEvent;
import simulator.model.exception.BuilderException;

public class NewVehicleEventBuilder extends Builder<Event> {

	public NewVehicleEventBuilder(String type) {
		super(type);
	}

	protected Event createTheInstance(JSONObject data) throws ClassCastException {
		ArrayList<String> iti = new ArrayList<String>();
		Integer time = data.getInt("time"), maxSpeed = data.getInt("maxspeed"), contClass = data.getInt("class");
		String id = data.getString("id");
		JSONArray ja = data.getJSONArray("itinerary");
		for (int i = 0; i < ja.length(); i++) {
			iti.add(ja.getString(i));
		}
		
		try {
			if(time != null && maxSpeed != null && id != null && contClass != null && !iti.isEmpty()) {
				return new NewVehicleEvent(time, id, maxSpeed, contClass, iti);
			}
			throw new BuilderException("JSON parameters may be null");
		}catch(BuilderException e) {
			System.err.format(e + e.getMessage());
			return null;
		}
		
		
	}
}
