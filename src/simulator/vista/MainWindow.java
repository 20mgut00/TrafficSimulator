package simulator.vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;

import simulator.control.Controller;

public class MainWindow extends JFrame {

	private Controller ctrl;
	
	public MainWindow(Controller ctrl) {
		super("Traffic Simulator");
		this.ctrl = ctrl;
		initGUI();
	}
	
	private void initGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		this.setContentPane(mainPanel);
		
		//panel con menus
		ControlPanel ctrlPanel = new ControlPanel(ctrl, this);
		mainPanel.add(ctrlPanel, BorderLayout.PAGE_START);
		
		//barra inferior con variable time
		mainPanel.add(new StatusBar(ctrl), BorderLayout.PAGE_END);
		
		//panel con tablas y mapas
		JPanel viewsPanel = new JPanel(new GridLayout(1, 2));
		mainPanel.add(viewsPanel, BorderLayout.CENTER);
	
		JPanel tablesPanel = new JPanel();
		tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.Y_AXIS));
		viewsPanel.add(tablesPanel);
		
		JPanel mapsPanel = new JPanel();
		mapsPanel.setLayout(new BoxLayout(mapsPanel, BoxLayout.Y_AXIS));
		viewsPanel.add(mapsPanel);
		
		//Menu pop up
		//JPopupMenu popup = 
		crearPopup(ctrlPanel);
		
		//TABLAS
		
		JPanel eventsView = createViewPanel(new JTable(new EventsTableModel(ctrl)), "Events");
		eventsView.setPreferredSize(new Dimension(500, 200));		
		tablesPanel.add(eventsView);
		JPanel vehiclesView = createViewPanel(new JTable(new VehiclesTableModel(ctrl)), "Vehicles");
		vehiclesView.setPreferredSize(new Dimension(500, 200));
		tablesPanel.add(vehiclesView);
		JPanel roadsView = createViewPanel(new JTable(new RoadsTableModel(ctrl)), "Roads");
		roadsView.setPreferredSize(new Dimension(500, 200));
		tablesPanel.add(roadsView);
		JPanel junctionsView = createViewPanel(new JTable(new JunctionsTableModel(ctrl)), "Junctions");
		junctionsView.setPreferredSize(new Dimension(500, 200));
		tablesPanel.add(junctionsView);
		
		
		//MAPAS
		JPanel mapView = createViewPanel(new MapComponent(ctrl), "Map");
		mapView.setPreferredSize(new Dimension(500, 400));
		mapsPanel.add(mapView);
		JPanel mapByRoadView = createViewPanel(new MapByRoadComponent(ctrl), "Map by Road");
		mapByRoadView.setPreferredSize(new Dimension(500, 400));
		mapsPanel.add(mapByRoadView);
		
		
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if(JOptionPane.showOptionDialog(null, "Are you sure you want to exit?", "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) System.exit(0);
			}
			@Override
			public void windowActivated(WindowEvent arg0) {}
			@Override
			public void windowClosed(WindowEvent arg0) {}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowOpened(WindowEvent arg0) {}
		});
		
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.pack();
		this.setVisible(true);		
	}
	
	private JPopupMenu crearPopup(ControlPanel ctrlPanel) {
		//Menu pop up con opciones play, stop y change
		JPopupMenu menu = new JPopupMenu();
		menu.setSize(new Dimension(50, 50));
		menu.setOpaque(false);
		
		JMenuItem co2 = ctrlPanel.crearItem("Change CO2", KeyEvent.VK_C);
		co2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ctrlPanel.changeCO2();
				menu.setVisible(false);
			}	
		});
		menu.add(co2);
		
		JMenuItem wea = ctrlPanel.crearItem("Change Weather", KeyEvent.VK_W);
		wea.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ctrlPanel.changeWea();
				menu.setVisible(false);
			}
		});
		menu.add(wea);
		
		JMenuItem run = ctrlPanel.crearItem("Run", KeyEvent.VK_R);
		run.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ctrlPanel.run();
				menu.setVisible(false);
			}
		});
		menu.add(run);
		
		JMenuItem stop = ctrlPanel.crearItem("Stop", KeyEvent.VK_P);
		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ctrlPanel.setStop(true);
				menu.setVisible(false);
		}});
		menu.add(stop);
		
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getButton() == 3) {
					//El pop up aparece donde este el raton con boton derecho
					menu.setLocation(arg0.getLocationOnScreen());
					menu.setVisible(true);
				}
				else if(arg0.getButton() == 1) {
					//el menu desaparece con click izquierdo
					if(menu.isVisible()) {
						menu.setVisible(false);
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}
		});
		
		return menu;
	}
	private JPanel createViewPanel(JComponent c, String title) {
		JPanel p = new JPanel(new BorderLayout());
		Border b = BorderFactory.createLineBorder(Color.black, 2);
		p.setBorder(BorderFactory.createTitledBorder(b, title));
		
		p.add(new JScrollPane(c));
		return p;
	}
}
