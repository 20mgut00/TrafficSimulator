package simulator.factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Pair;
import simulator.model.Event;
import simulator.model.NewSetContClassEvent;
import simulator.model.exception.BuilderException;

public class SetContClassEventBuilder extends Builder<Event> {

	public SetContClassEventBuilder(String type) {
		super(type);
	}

	
	protected Event createTheInstance(JSONObject data) {
		List<Pair<String, Integer>> l = new ArrayList<Pair<String, Integer>>();
		JSONArray aux = data.getJSONArray("info");
		for(int i = 0; i < aux.length(); i++) {
			l.add(new Pair<String, Integer>(aux.getJSONObject(i).getString("vehicle"), aux.getJSONObject(i).getInt("class")));
		}
		Integer time = data.getInt("time");
		
		try {
			if(time != null && !l.isEmpty()) {
				return new NewSetContClassEvent(time, l);
			}
			throw new BuilderException("JSON parameters may be null");
		}catch(BuilderException e) {
			System.err.format(e + e.getMessage());
			return null;
		}
		
		
	}

}
