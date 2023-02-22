package simulator.vista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import simulator.control.Controller;
import simulator.factories.SetWeatherEventBuilder;
import simulator.model.RoadMap;
import simulator.model.Weather;
import simulator.model.exception.WrongObjectException;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.json.JSONObject;

public class ChangeWeatherDialog extends ChangeDialog{
	JComboBox<Weather> weather;
	public ChangeWeatherDialog(Controller ctrl, RoadMap map, int time, JFrame owner) {
		super(ctrl, time, map, owner);
        initGUI();
	}
	private void initGUI() {
		//Se crea la ventana para seleccionar los datos del evento nuevo
		createTextArea("Schedule an event to change the weather of a road after a number of simulation ticks from now");
		centerPanel.add(new JLabel("Road: "));
        if(map != null) {
        	createJList(map.getRoads());		//Extension
        }
        centerPanel.add(new JLabel("Weather: "));
		weather = new JComboBox<Weather>(Weather.values());
		centerPanel.add(weather);
		centerPanel.add(new JLabel("Ticks: "));
		createSpinner(1,1,10000,1);
		
		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(opciones == null || opciones.isSelectionEmpty()) {		//Extension
						throw new WrongObjectException("Carretera no seleccionada");
					}
					else {
						//Se toman los datos necesarios para crear el evento
						String type = "set_weather";
		                Weather wea = (Weather) weather.getSelectedItem();
		                
		                JSONObject change = new JSONObject();
		                change.put("road", opciones.getSelectedValue());		//Extension	
		                change.put("weather", wea.name());
		                
		                performOk(new SetWeatherEventBuilder(type), change, type);
					}
				}
				catch(WrongObjectException ex) {
					onError(ex.getMessage());
				}
				
			}
		});
		bottomPanel.add(ok);
		this.setTitle("Change Weather Class");
		this.pack();
		this.setVisible(true);
	}

}
