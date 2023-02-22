package simulator.factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Pair;
import simulator.model.Event;
import simulator.model.SetWeatherEvent;
import simulator.model.Weather;
import simulator.model.exception.BuilderException;

public class SetWeatherEventBuilder extends Builder<Event> {

	public SetWeatherEventBuilder(String type) {
		super(type);
	}
	
	protected Event createTheInstance(JSONObject data) {
		List<Pair<String, Weather>> l = new ArrayList<Pair<String, Weather>>();
		JSONArray aux = data.getJSONArray("info");
		for(int i = 0; i < aux.length(); i++) {
			l.add(new Pair<String, Weather>(aux.getJSONObject(i).getString("road"), Weather.valueOf(aux.getJSONObject(i).getString("weather").toUpperCase())));
		}
		Integer time = data.getInt("time");
		
		try {
			if(time != null && !l.isEmpty()) {
				return new SetWeatherEvent(time, l);
			}
			throw new BuilderException("JSON parameters may be null");
		}catch(BuilderException e) {
			System.err.format(e + e.getMessage());
			return null;
		}
		
	}

}
