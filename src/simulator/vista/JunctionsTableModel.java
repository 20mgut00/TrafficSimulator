package simulator.vista;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.Junction;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;

public class JunctionsTableModel extends AbstractTableModel implements TrafficSimObserver {
	
	List<Junction> rowData;
	String[] headers = {"Id", "Green", "Queues"};
	public JunctionsTableModel(Controller ctrl) {
		ctrl.addObserver(this);
		rowData = new ArrayList<Junction>();
	}
	
	private void update(List<Junction> junctions) {
		rowData = junctions;
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
		Junction j = rowData.get(rowIndex);
		switch(columnIndex) {
		case 0: return j.getId();
		case 1: {
			if(j.getGreenLightIndex() == -1) {
				return "NONE";
			}
			else return j.getInRoad().get(j.getGreenLightIndex());
		}
		case 2: return j.getColas();
		}
		return null;
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		update(map.getJunctions());
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		update(map.getJunctions());
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		update(map.getJunctions());
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		update(map.getJunctions());
	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
		update(map.getJunctions());
	}

	@Override
	public void onError(String err) {}

}
