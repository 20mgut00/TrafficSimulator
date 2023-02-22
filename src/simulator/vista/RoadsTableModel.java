package simulator.vista;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.Road;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;

public class RoadsTableModel extends AbstractTableModel implements TrafficSimObserver {
	
	List<Road> rowData;
	String[] headers = {"Id", "Length", "Weather", "Max. Speed", "Speed Limit", "Total CO2", "CO2 Limit"};
	public RoadsTableModel(Controller ctrl) {
		ctrl.addObserver(this);
		rowData = new ArrayList<Road>();
	}
	
	private void update(List<Road> roads) {
		rowData = roads;
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
		Road r = rowData.get(rowIndex);
		switch(columnIndex) {
		case 0: return r.getId();
		case 1: return r.getLength();
		case 2: return r.getClima();
		case 3: return r.getMaxSpeed();
		case 4: return r.getSpeedLimit();
		case 5: return r.getTotalCO2();
		case 6: return r.getCO2Limit();
		}
		return null;
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		update(map.getRoads());
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		update(map.getRoads());
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		update(map.getRoads());
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		update(map.getRoads());
	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
		update(map.getRoads());
	}

	@Override
	public void onError(String err) {}

}
