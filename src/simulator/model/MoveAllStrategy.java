package simulator.model;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;

public class MoveAllStrategy implements DequeuingStrategy {

	public List<Vehicle> dequeue(List<Vehicle> q) {
		List<Vehicle> aux = new LinkedList<Vehicle>();
		for(int i = 0; i < q.size(); i++) {
			if(q.get(i).getLocation() == q.get(i).getRoad().getLength()) {
				aux.add(q.get(i));
			}
			
		}
		return Collections.unmodifiableList(aux);
	}

}
