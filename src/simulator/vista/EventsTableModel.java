package simulator.vista;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.misc.SortedArrayList;
import simulator.model.Event;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;

public class EventsTableModel extends AbstractTableModel implements TrafficSimObserver {
	
	List<Event> rowData;
	String[] headers = {"Time", "Desc."};
	
	public EventsTableModel(Controller ctrl) {
		ctrl.addObserver(this);
		rowData = new SortedArrayList<Event>();
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
		Event e = rowData.get(rowIndex);
		switch(columnIndex) {
		case 0: return e.getTime();
		case 1: return e.toString();
		}
		return null;
	}

	
	private void update(List<Event> eventos) {
		rowData = eventos;
		fireTableDataChanged();
	}
	
	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		update(events);
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		update(events);
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		update(events);
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		update(events);
	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
		update(events);

	}

	@Override
	public void onError(String err) {}

}
