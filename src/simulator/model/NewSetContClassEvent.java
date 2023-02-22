package simulator.model;

import java.util.List;

import simulator.misc.Pair;
import simulator.model.exception.WrongObjectException;
import simulator.model.exception.WrongValueException;

public class NewSetContClassEvent extends Event {
	
	List<Pair<String, Integer>> cs;
	
	public NewSetContClassEvent(int time, List<Pair<String, Integer>> cs) {
		super(time);
		this.cs = cs;
	}
	@Override
	void execute(RoadMap map) throws Exception{
		try {
			if(cs != null) {
				for(Pair<String, Integer> i : cs) {
					if(map.getVehicles().contains(map.getVehicle(i.getFirst()))) {
						map.getVehicle(i.getFirst()).setContaminationClass(i.getSecond());
					}
					else throw new WrongObjectException("This vehicle does not exist\n");
				}
			}
			else throw new WrongValueException("The parameter must not be null\n");
		}catch(WrongValueException | WrongObjectException e) {
			throw new Exception(e.getMessage());
		}
		

	}
	@Override
	public String toString() {
		return "Change CO2 class: " + cs;
	}
}
