package simulator.model;

import java.util.List;

public interface DequeuingStrategy {
	public List<Vehicle> dequeue(List<Vehicle> q);
}
