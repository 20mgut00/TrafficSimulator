package simulator.vista;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;
import simulator.model.Vehicle;
import simulator.model.VehicleStatus;

public class VehiclesTableModel extends AbstractTableModel implements TrafficSimObserver {

	List<Vehicle> rowData;
	String[] headers = {"Id", "Location", "Itinerary", "CO2 Class", "Max. Speed", "Speed", "Total CO2", "Distance"};
	public VehiclesTableModel(Controller ctrl) {
		ctrl.addObserver(this);
		rowData = new ArrayList<Vehicle>();
	}
	
	private void update(List<Vehicle> vehicles) {
		rowData = vehicles;
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return this.rowData.size();
	}
	@Override
	public int getColumnCount() {
		return headers.length;
	}
	@Override
	public String getColumnName(int col) {
		return headers[col];
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Vehicle v = rowData.get(rowIndex);
		switch(columnIndex) {
		case 0: return v.getId();
		case 1: {
			if(v.getStatus() == VehicleStatus.PENDING) return "Pending";
			else if(v.getStatus() == VehicleStatus.ARRIVED) return "Arrived";
			else if(v.getStatus() == VehicleStatus.TRAVELING) return v.getRoad() + ":" + v.getLocation();
			else return "Waiting:" + v.getRoad().getDest();
		}
		case 2: return v.getItinerario();
		case 3: return v.getContaminationClass();
		case 4: return v.getMaxSpeed();
		case 5: return v.getSpeed();
		case 6: return v.getTotalContamination();
		case 7: return v.getDistance();
		}
		return null;
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		update(map.getVehicles());
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		update(map.getVehicles());
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		update(map.getVehicles());
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		update(map.getVehicles());
	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
		update(map.getVehicles());
	}

	@Override
	public void onError(String err) {}

}
