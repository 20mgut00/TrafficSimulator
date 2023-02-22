package simulator.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.factories.Builder;
import simulator.model.Event;
import simulator.model.RoadMap;
import simulator.model.SimulatedObject;
import simulator.model.TrafficSimObserver;

public class ChangeDialog extends JDialog implements TrafficSimObserver{ //Extension
	JPanel mainPanel, centerPanel, bottomPanel;
	Controller ctrl;
	RoadMap map;
	int time;
	
	JList<String> opciones;		//Extension
	JSpinner n_ticks;
	
	public ChangeDialog(Controller ctrl, int time, RoadMap map, JFrame owner) {
		super(owner,true);
		this.ctrl = ctrl;
		this.map = map;
		this.time = time;
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		centerPanel = new JPanel();
		centerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
	
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		bottomPanel.add(cancel);
		
		mainPanel.add(bottomPanel, BorderLayout.PAGE_END);
		
		this.add(mainPanel);
		this.setResizable(false);
		
	}
	
	public void createTextArea(String msg) {
		JTextArea text = new JTextArea(msg);
		text.setWrapStyleWord(true);
		text.setLineWrap(true);
        text.setEditable(false);
        text.setOpaque(false);
        text.setPreferredSize(new Dimension(0,40));
        this.add(text, BorderLayout.PAGE_START);
	}
	
	public void createJList(List lista) {		//Extension
		DefaultListModel<String> items = new DefaultListModel<String>();
		if(!lista.isEmpty()) {
			for(int i = 0; i < lista.size(); i++) {
				SimulatedObject aux = (SimulatedObject) lista.get(i);
				items.addElement(aux.getId());
			}
		}
		opciones = new JList<String>(items);
		opciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		opciones.setFixedCellWidth(50);
		opciones.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		centerPanel.add(opciones);
	}
	
	public void createSpinner(int ini, int l_bajo, int l_alto, int inter) {
		n_ticks = new JSpinner(new SpinnerNumberModel(ini, l_bajo, l_alto, inter));
		centerPanel.add(n_ticks);
	}
	
	public void performOk(Builder<Event> evento, JSONObject change, String type) {
		//Con los datos recibidos se crea un JSON
		JSONArray ja = new JSONArray();
		ja.put(change);
		
		JSONObject jo2 = new JSONObject();
		jo2.put("time", (int) n_ticks.getValue() + time);
		jo2.put("info", ja);
		
		JSONObject jo1 = new JSONObject();
		jo1.put("type", type);
		jo1.put("data", jo2);
		
		//Se a�ade el nuevo evento a la lista de existentes
		ctrl.addEvent(evento.createInstance(jo1));
		dispose();
	}
	
	private void exit() {
		this.removeAll();
		this.setVisible(false);
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {}

	@Override
	public void onError(String err) {
		JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
		
	}
	
}
