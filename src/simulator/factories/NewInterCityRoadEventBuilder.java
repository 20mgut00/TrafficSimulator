package simulator.factories;

import org.json.JSONObject;

import simulator.model.Event;
import simulator.model.NewInterCityRoadEvent;
import simulator.model.exception.BuilderException;

public class NewInterCityRoadEventBuilder extends NewRoadEventBuilder {

	public NewInterCityRoadEventBuilder(String type) {
		super(type);
	}
	
	protected Event createTheInstance(JSONObject data) {
		
		Integer time = data.getInt("time"), length = data.getInt("length"), maxSpeed = data.getInt("maxspeed"), co2Limit = data.getInt("co2limit");
		String id = data.getString("id"), srcJun = data.getString("src"), destJunc = data.getString("dest"), weather = data.getString("weather");
		try {
			if(time != null && length != null && maxSpeed != null && co2Limit != null && id != null && srcJun != null && destJunc != null && weather != null) {
				return new NewInterCityRoadEvent(time, id, srcJun, destJunc, length, co2Limit, maxSpeed, weather);
			}
			throw new BuilderException("JSON parameters may be null");
		}catch(BuilderException e) {
			System.err.format(e + e.getMessage());
			return null;
		}
	}

}
