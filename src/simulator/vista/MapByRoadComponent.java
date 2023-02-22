package simulator.vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.Junction;
import simulator.model.Road;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;
import simulator.model.Vehicle;
import simulator.model.VehicleStatus;
import simulator.model.Weather;

public class MapByRoadComponent extends JComponent implements TrafficSimObserver {
	
	private static final long serialVersionUID = 1L;

	private static final int _JRADIUS = 10;

	private static final Color _BG_COLOR = Color.WHITE;
	private static final Color _ROAD_LABEL_COLOR = Color.BLACK;
	private static final Color _JUNCTION_COLOR = Color.BLUE;
	private static final Color _JUNCTION_LABEL_COLOR = new Color(200, 100, 0);
	private static final Color _GREEN_LIGHT_COLOR = Color.GREEN;
	private static final Color _RED_LIGHT_COLOR = Color.RED;

	private RoadMap _map;

	private Image _car;
	
	public MapByRoadComponent(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
		this.setPreferredSize(new Dimension(300,200));
	}
	private void initGUI() {
		_car = loadImage("car.png");
	}

	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// clear with a background color
		g.setColor(_BG_COLOR);
		g.clearRect(0, 0, getWidth(), getHeight());

		if (_map == null || _map.getJunctions().size() == 0) {
			g.setColor(Color.red);
			g.drawString("No map yet!", getWidth() / 2 - 50, getHeight() / 2);
		} else {
			updatePrefferedSize();
			drawMap(g);
		}
	}

	private void drawMap(Graphics g) {
		drawRoads(g);
		drawVehicles(g);
		drawJunctions(g);
		drawWeather(g);
		drawContamination(g);
	}

	private void drawRoads(Graphics g) {
		for(int i = 0; i < _map.getRoads().size(); i++) {
			Road r = _map.getRoads().get(i);
			// the road goes from (x1,y) to (x2,y);
			int x1 = 50;
			int x2 = getWidth() -100;
			int y = (i+1)*50;

			// choose a color for the road depending on the total contamination, the darker
			// the
			// more contaminated (wrt its co2 limit)
			int roadColorValue = 200 - (int) (200.0 * Math.min(1.0, (double) r.getTotalCO2() / (1.0 + (double) r.getCO2Limit())));
			Color roadColor = new Color(roadColorValue, roadColorValue, roadColorValue);

			// draw line from (x1,y1) to (x2,y2) with arrow of color arrowColor and line of
			// color roadColor.
			drawLine(g, x1, x2, y, roadColor);
			
			// draw the road's identifier at the left of the line
			g.setColor(_ROAD_LABEL_COLOR);
			g.drawString(r.getId(), x1 - 30, y);
		}
	}

	private void drawVehicles(Graphics g) {
		for (Vehicle v : _map.getVehicles()) {
			if (v.getStatus() != VehicleStatus.ARRIVED) {

				// The calculation below compute the coordinate (x) of the vehicle on the
				// corresponding road. It is calculated relativly to the length of the road, and
				// the location on the vehicle.
				Road r = v.getRoad();
				int x1 = 50;
				int x2 = getWidth() -100;
				int x = x1 + (int) ((x2-x1)*((double)v.getLocation()/(double)r.getLength()));
				int y = 0;
				
				boolean encontrado = false;
				while(y < _map.getRoads().size() && !encontrado) {
					if(_map.getRoads().get(y).getVehicle().contains(v)) {
						y = (y + 1) * 50;
						encontrado = true;
					}
					else y++;
				}
				// Choose a color for the vehcile's label and background, depending on its
				// contamination class
				int vLabelColor = (int) (25.0 * (10.0 - (double) v.getContaminationClass()));
				g.setColor(new Color(0, vLabelColor, 0));

				// draw an image of a car and it identifier
				g.drawImage(_car, x, y - 12, 16, 16, this);
				g.drawString(v.getId(), x, y - 12);
			}
		}
	}

	private void drawJunctions(Graphics g) {
		for(int i = 0; i < _map.getRoads().size(); i++) {
			Road r = _map.getRoads().get(i);
			// the road goes from (x1,y) to (x2,y);
			int x1 = 50;
			int x2 = getWidth() -100;
			int y = (i+1)*50;
			
			// choose a color for the circle depending on the traffic light of the road
			Color trafficColor = _RED_LIGHT_COLOR;
			int idx = r.getDest().getGreenLightIndex();
			if (idx != -1 && r.equals(r.getDest().getInRoad().get(idx))) {
				trafficColor = _GREEN_LIGHT_COLOR;
			}
			
			// draw a circle with center at (x,y) with radius _JRADIUS
			g.setColor(_JUNCTION_COLOR);
			g.fillOval(x1 - _JRADIUS / 2, y - _JRADIUS / 2, _JRADIUS, _JRADIUS);
			g.setColor(trafficColor);
			g.fillOval(x2 - _JRADIUS / 2, y - _JRADIUS / 2, _JRADIUS, _JRADIUS);
			
			// draw the junction's identifier above the circle
			g.setColor(_JUNCTION_LABEL_COLOR);
			g.drawString(r.getSrc().getId(), x1, y - 10);
			g.drawString(r.getDest().getId(), x2, y - 10);
		}
	}
	
	private void drawWeather(Graphics g) {
		for(int i = 0; i < _map.getRoads().size(); i++) {
			Road r = _map.getRoads().get(i);
			
			int x = getWidth() - 90;
			int y = ((i+1)*50)-16;
			
			// draw an image depending on the weather of the road
			if(r.getClima().equals(Weather.SUNNY)) g.drawImage(loadImage("sun.png"), x, y, 32, 32, null);
			else if(r.getClima().equals(Weather.WINDY)) g.drawImage(loadImage("wind.png"), x, y, 32, 32, null);
			else if(r.getClima().equals(Weather.CLOUDY)) g.drawImage(loadImage("cloud.png"), x, y, 32, 32, null);
			else if(r.getClima().equals(Weather.RAINY)) g.drawImage(loadImage("rain.png"), x, y, 32, 32, null);
			else g.drawImage(loadImage("storm.png"), x, y, 32, 32, this);
		}
	}

	private void drawContamination(Graphics g) {
		for(int i = 0; i < _map.getRoads().size(); i++) {
			Road r = _map.getRoads().get(i);
			int c = (int) Math.floor(Math.min((double) r.getTotalCO2()/(double) r.getCO2Limit(),1.0) /0.19);
			int x = getWidth() - 48;
			int y = ((i+1)*50)-16;
			
			// draw and image depending on the contamination of the road
			g.drawImage(loadImage("cont_" + c + ".png"), x, y, 32, 32, this);
		}
			
	}
	// this method is used to update the preffered and actual size of the component,
	// so when we draw outside the visible area the scrollbars show up
	private void updatePrefferedSize() {
		int maxW = 200;
		int maxH = 200;
		for (Junction j : _map.getJunctions()) {
			maxW = Math.max(maxW, j.getX());
			maxH = Math.max(maxH, j.getY());
		}
		maxW += 20;
		maxH += 20;
		if (maxW > getWidth() || maxH > getHeight()) {
		    setPreferredSize(new Dimension(maxW, maxH));
		    setSize(new Dimension(maxW, maxH));
		}
	}

	// This method draws a line from (x1,y1) to (x2,y2)
	// The last two arguments are the colors of the arrow and the line
	private void drawLine(Graphics g, int x1, int x2, int y, Color lineColor) {
		g.setColor(lineColor);
		g.drawLine(x1, y, x2, y);
	}

	// loads an image from a file
	private Image loadImage(String img) {
		Image i = null;
		try {
			return ImageIO.read(new File("resources/icons/" + img));
		} catch (IOException e) {
		}
		return i;
	}

	public void update(RoadMap map) {
		_map = map;		
		repaint();
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		update(map);
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		update(map);
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		update(map);
	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
		update(map);
	}

	@Override
	public void onError(String err) {
	}


}
