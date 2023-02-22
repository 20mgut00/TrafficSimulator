package simulator.model;

import java.util.List;

import simulator.misc.Pair;
import simulator.model.exception.WrongObjectException;
import simulator.model.exception.WrongValueException;

public class SetWeatherEvent extends Event {
	
	List<Pair<String,Weather>> ws;
	
	public SetWeatherEvent (int time, List<Pair<String,Weather>> ws) {
		super(time);
		this.ws=ws;
	}
	@Override
	void execute(RoadMap map) throws Exception {
		try {
			if(ws != null) {
				for(Pair<String, Weather> i : ws) {
					if(map.getRoads().contains(map.getRoad(i.getFirst()))) {
						map.getRoad(i.getFirst()).setWeather(i.getSecond());
					}
					else throw new WrongObjectException("This road does not exist\n");
				}
			}
			else throw new WrongValueException("The parameter must not be null\n");
		}catch(WrongValueException | WrongObjectException e) {
			throw new Exception(e.getMessage());
		}

	}
	@Override
	public String toString() {
		return "Change Weather: " + ws;
	}
}
