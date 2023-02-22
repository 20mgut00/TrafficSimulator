package simulator.vista;

import simulator.control.Controller;
import simulator.factories.SetContClassEventBuilder;
import simulator.model.RoadMap;
import simulator.model.exception.WrongObjectException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.json.JSONObject;

public class ChangeCO2ClassDialog extends ChangeDialog{
	JComboBox<Integer> listC;
	public ChangeCO2ClassDialog(Controller ctrl, RoadMap map, int time, JFrame owner) {
		super(ctrl, time, map, owner);
		initGUI();	
	}
	
	private void initGUI(){
		//Se crea la ventana para seleccionar los datos del evento nuevo
		createTextArea("Schedule an event to change the CO2 class of a vehicle after a number of simulation ticks from now.");
		centerPanel.add(new JLabel("Vehicle:"));
		if(map != null) {
			createJList(map.getVehicles());		//Extension
		}
		centerPanel.add(new JLabel("CO2 Class:"));
		Integer nums[] = {0,1,2,3,4,5,6,7,8,9,10};
		listC = new JComboBox<Integer>(nums);
		centerPanel.add(listC);
		centerPanel.add(new JLabel("Ticks: "));
		createSpinner(1,1,10000,1);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				try {
					if(opciones == null || opciones.isSelectionEmpty()) {	//Extension
						throw new WrongObjectException("Coche no seleccionado");
						
					}
					else {
						//Se toman los datos necesarios para crear el evento
						String type = "set_cont_class";
						JSONObject change = new JSONObject();
						change.put("vehicle", opciones.getSelectedValue());		//Extension
			            change.put("class", listC.getSelectedItem());
			               
			            performOk(new SetContClassEventBuilder(type), change, type);
					}
				}
				catch(WrongObjectException ex) {
					onError(ex.getMessage());
				}
				
				
			}
		});
		bottomPanel.add(ok);
		this.setTitle("Change CO2 Class");
		this.pack();
		this.setVisible(true);
	}
}
