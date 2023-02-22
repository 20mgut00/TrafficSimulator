package simulator.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.Junction;
import simulator.model.Road;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;
import simulator.model.Vehicle;
import simulator.model.VehicleStatus;
import simulator.model.Weather;
import simulator.model.exception.WrongObjectException;

public class ControlPanel extends JPanel implements TrafficSimObserver{
	
	private Controller ctrl;
	
	private RoadMap map;
	private List<Event> eventos;
	private int time;
	private JFrame owner;
	
	boolean _stopped = false;
	JButton b_events, b_contClass, b_setWeather, b_run, b_exit;
	JButton b_reset, b_saveState, b_loadState; 		//Extension
	File _inFile; 		//Extension
	
	JMenuBar menu; 		//extension
	JMenuItem co2, wea, run, stop, exit, load, load_state, save_state, reset;		//Extension
	JToolBar toolBar, toolBar2;
	JSpinner n_ticks;
	
	public ControlPanel(Controller ctrl, JFrame owner) {
		this.ctrl = ctrl;
		this.owner = owner;
		ctrl.addObserver(this);
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(new BorderLayout());
		
		//menu superior de opciones
		menu = crearMenu();						//Extension
		this.add(menu, BorderLayout.NORTH);
		
		//barra con los diferentes botones
		toolBar = crearMainToolBar();
		this.add(toolBar, BorderLayout.WEST);		
		
		toolBar2 = crearToolBar();
		
		//barra del exit
		b_exit = crearBoton(new ImageIcon("resources/icons/exit.png"), "Exit the simulator");
		b_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exit();
			}
		});
		toolBar2.add(b_exit);
		
		this.add(toolBar2, BorderLayout.EAST);
	}
	
	private JMenu crearJMenu(String nombre, int keyCode) {		//Extension
		JMenu menu = new JMenu(nombre);
		menu.setMnemonic(keyCode);	//Extension
		return menu;
	}
	
	protected JMenuItem crearItem(String nombre, int keyCode) {		//Extension
		JMenuItem item = new JMenuItem(nombre);
		item.setAccelerator(KeyStroke.getKeyStroke(keyCode, ActionEvent.SHIFT_MASK));	//Extension
		return item;
	}
	
	private JMenuBar crearMenu() {		//Extension
		JMenuBar menu = new JMenuBar();
		
		JMenu file = crearJMenu("File", KeyEvent.VK_F);
		
		load = crearItem("Load File", KeyEvent.VK_F);
		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				load_events();
			}
		});
		file.add(load);
		
		JMenu states = crearJMenu("States", KeyEvent.VK_T);
		
		load_state = crearItem("Load State", KeyEvent.VK_L);
		load_state.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				load_state();
			}	
		});
		states.add(load_state);
		
		save_state = crearItem("Save State", KeyEvent.VK_S);
		save_state.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save_state();
			}	
		});
		states.add(save_state);
		
		file.add(states);
		
		exit = crearItem("Exit", KeyEvent.VK_E);
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}		
		});
		file.add(exit);
		
		menu.add(file);
		
		JMenu events = crearJMenu("Events", KeyEvent.VK_E);
		
		co2 = crearItem("Change CO2", KeyEvent.VK_C);
		co2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeCO2();
			}
		});
		events.add(co2);
		
		wea = crearItem("Change Weather", KeyEvent.VK_W);
		wea.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeWea();
			}			
		});
		events.add(wea);
		
		menu.add(events);
		
		JMenu simulator = crearJMenu("Simulator", KeyEvent.VK_S);
		
		run = crearItem("Run", KeyEvent.VK_R);
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				run();
			}	
		});
		simulator.add(run);
		
		stop = crearItem("Stop", KeyEvent.VK_P);
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setStop(true);
			}
		});
		simulator.add(stop);
		
		reset = crearItem("Reset", KeyEvent.VK_BACK_SPACE);
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		simulator.add(reset);
		
		menu.add(simulator);
		
		return menu;
	}
	
	
	private JToolBar crearMainToolBar() {
		JToolBar toolBar = crearToolBar();
		
		b_events = crearBoton(new ImageIcon("resources/icons/open.png"), "Open a new file");
		b_events.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				load_events();
			}
		});
		toolBar.add(b_events);
		
		toolBar.addSeparator();
		
		b_saveState = crearBoton(new ImageIcon("resources/icons/saveState.png"), "Save the current state of the simulator");		//Extension
		b_saveState.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save_state();
			}
		});
		toolBar.add(b_saveState);
		
		b_loadState = crearBoton(new ImageIcon("resources/icons/loadState.png"), "Load an specific state of the simulator");		//Extension
		b_loadState.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				load_state();
			}
		});
		toolBar.add(b_loadState);
		
		toolBar.addSeparator();
		
		b_contClass = crearBoton(new ImageIcon("resources/icons/co2class.png"), "Change CO2 Class of a Vehicle");
		b_contClass.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeCO2();
			}
		});
		toolBar.add(b_contClass);
		
		b_setWeather = crearBoton(new ImageIcon("resources/icons/weather.png"), "Change Weather of a Road");
		b_setWeather.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeWea();
			}
		});
		toolBar.add(b_setWeather);
		
		toolBar.addSeparator();
		
		b_run = crearBoton(new ImageIcon("resources/icons/run.png"), "Run the simulator");
		b_run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				run();
			}
		});
		toolBar.add(b_run);
		
		JButton b_stop = crearBoton(new ImageIcon("resources/icons/stop.png"), "Stop the simulator");
		b_stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setStop(true);
			}
		});
		toolBar.add(b_stop);
		
		toolBar.add(new JLabel("Ticks: "));
		//Spinner con los ticks que avanzar al pulsar play
		n_ticks = new JSpinner(new SpinnerNumberModel(0,0,10000,1));
		n_ticks.setMaximumSize(new Dimension(75,25));
		n_ticks.setPreferredSize(new Dimension(75,25));
		n_ticks.setToolTipText("Simulation tick to run: 1-10000");
		toolBar.add(n_ticks);
		
		b_reset = crearBoton(new ImageIcon("resources/icons/reset.png"), "Reset the simulator");		//Extension
		b_reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
			
		});
		toolBar.add(b_reset);
		
		return toolBar;
	}
	
	private JToolBar crearToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		return toolBar;
	}
	
	private JButton crearBoton(ImageIcon i, String toolTip) {
		JButton boton = new JButton();
		boton.setIcon(i);
		boton.setToolTipText(toolTip);
		return boton;
	}
	
	private void load_events(){
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("resources/examples"));
		if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			_inFile = fc.getSelectedFile();
			InputStream in;
			try {
				in = new FileInputStream(_inFile);
				ctrl.reset();
				ctrl.loadEvents(in);
			} catch (Exception ex) {
				onError(ex.getMessage());
			}
		}
	}
	
	private void save_state() {		//Extension
		JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("resources/examples"));
        //Solo muestra ficheros de extension .json
        fc.setFileFilter(new FileNameExtensionFilter("JSON file", "json"));
        if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            //Crea un fichero .json con el nombre de getSelectedFile
        	File _outState = fc.getSelectedFile();
        	if(!fc.getSelectedFile().getAbsolutePath().endsWith(".json")) {
        		_outState = new File(fc.getSelectedFile().getAbsolutePath() + ".json");
        	}
            try {
                OutputStream out = new FileOutputStream(_outState);
                PrintStream p = new PrintStream(out);
                JSONObject jo = new JSONObject();
                //Escribe en el fichero el path del fichero inicial de la simulacion
                jo.put("ficheroIni", _inFile);
                //Guarda el report del estado actual
                jo.put("report", ctrl.report());
                p.print(jo.toString(3));
                
                p.close();
            } catch (IOException ex) {
                onError(ex.getMessage());
            }
        }	
	}
	
	private void load_state() {		//Extension
		 JFileChooser fc = new JFileChooser();
         fc.setCurrentDirectory(new File("resources/examples"));
         fc.setFileFilter(new FileNameExtensionFilter("JSON file", "json")); //Only .json files
         if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
             File _inState = fc.getSelectedFile();
             InputStream in;
			try {
				in = new FileInputStream(_inState);
				JSONObject jo = new JSONObject(new JSONTokener(in));
				//Comprueba que el estado seleccionado pertenezca a la simulacion
				if(_inFile == null) throw new WrongObjectException("El estado seleccionado no pertenece a esta simulacion");
				if(jo.getString("ficheroIni").equals(_inFile.toString())) {
					ctrl.reset();
					ctrl.loadEvents(new FileInputStream(jo.getString("ficheroIni")));
					ctrl.run(1, null);
					//Iguala los parametros con los del report del fichero
					JSONObject jo2 = jo.getJSONObject("report");
					ctrl.setTime(jo2.getInt("time"));
					//ctrl.getTrafficSimulator().setTime(jo2.getInt("time"));
					JSONObject jo3 = jo2.getJSONObject("state");
					
					JSONArray ja = jo3.getJSONArray("road");
					for(int i = 0; i < ja.length(); i++) {
						JSONObject jo4 = ja.getJSONObject(i);
						Road r = map.getRoad(jo4.getString("id"));
						r.setT_contam(jo4.getInt("co2"));
						r.setV_max(jo4.getInt("speedlimit"));
						r.setWeather(Weather.valueOf(jo4.getString("weather")));
					}
					ja = jo3.getJSONArray("vehicles");
					for(int i = 0; i < ja.length(); i++) {
						JSONObject jo4 = ja.getJSONObject(i);
						Vehicle v = map.getVehicle(jo4.getString("id"));
						v.setDistance(jo4.getInt("distance"));
						v.setRoad(map.getRoad(jo4.getString("road")));
						v.setTotalContamination(jo4.getInt("co2"));
						v.setLocation(jo4.getInt("location"));
						v.setContaminationClass(jo4.getInt("class"));
						v.setSpeed(jo4.getInt("speed"));
						v.setStatus(VehicleStatus.valueOf(jo4.getString("status")));
					}
					ja = jo3.getJSONArray("junctions");
					for(int i = 0; i < ja.length(); i++) {
						JSONObject jo4 = ja.getJSONObject(i);
						Junction j = map.getJunction(jo4.getString("id"));
						if(jo4.getString("green").equals("none")) j.setGreen(-1);
						else j.setGreen(j.getInRoad().indexOf(jo4.get("green")));
					}
					
					for(int i = 0; i < eventos.size(); i++) {
						if(eventos.get(i).getTime() < jo2.getInt("time")) {
							eventos.remove(i);
							i--;
						}
						
					}
				}
				else {
					throw new WrongObjectException("El estado seleccionado no pertenece a esta simulacion");
				}
				
				
			} catch (Exception ex) {
				onError(ex.getMessage());
			}
         }
	}
	
	public void changeCO2() {
		new ChangeCO2ClassDialog(ctrl, map, time, owner);
		
	}
	
	public void changeWea() {
		new ChangeWeatherDialog(ctrl, map, time, owner);
	}
	
	void run() {
		setStop(false);
		enableToolBar(false);
		run_sim((int) n_ticks.getValue());
	}
	
	private void reset() {		//Extension
		if(JOptionPane.showOptionDialog(null, "Are you sure you want to reset?", "Reset", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
			ctrl.reset();
			ctrl.setEventos(ctrl.getEventIni()); //la lista de eventos se iguala a una auxiliar, que contiene la lista inicial
		}
	}
	
	private void exit() {
		if(JOptionPane.showOptionDialog(null, "Are you sure you want to exit?", "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) System.exit(0);
	}
	
	private void run_sim(int n) {
		if (n > 0 && !_stopped) {
			try {
				ctrl.run(1, null);
			} catch (Exception e) {
				onError(e.getMessage());
				setStop(true);
				return;
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					run_sim(n - 1);
				}
			});
		}
		else {
			//si el run esta activado, algunos botones quedan inhabilitados
			enableToolBar(true);
			setStop(true);
		}
	}
	
	private void enableToolBar(boolean enable) {
		b_events.setEnabled(enable);
		b_contClass.setEnabled(enable);
		b_setWeather.setEnabled(enable);
		b_run.setEnabled(enable);
		b_exit.setEnabled(enable);
		b_loadState.setEnabled(enable);
		b_saveState.setEnabled(enable);
	}
	
	void setStop(boolean stop) {
		_stopped = stop;
	}
	
	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		this.map = map;
		this.eventos = events;
		this.time = time;
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {}

	@Override
	public void onError(String err) {
		JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
