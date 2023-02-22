package simulator.model;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;


public class MoveFirstStrategy implements DequeuingStrategy {
	
	public List<Vehicle> dequeue(List<Vehicle> q) {
		List<Vehicle> aux = new LinkedList<Vehicle>();
		if(q.get(0).getLocation() == q.get(0).getRoad().getLength()) {
			aux.add(q.get(0));
		}
		return Collections.unmodifiableList(aux);
	}

}
