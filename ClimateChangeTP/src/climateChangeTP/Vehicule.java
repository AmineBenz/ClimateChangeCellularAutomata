package climateChangeTP;

import java.util.ArrayList;
import java.util.Iterator;

import antlr.collections.List;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.VNQuery;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Vehicule {

	public static final int MAX_MOVE_ATTEMPTS = 20;

	GridPoint location;
	Grid grid;

	double co2Emission;

	int xMin, xMax, yMin, yMax;

	int speed = 8;
	int speedCount = 0;

	public Vehicule(double co2Emission, int xMin, int xMax, int yMin, int yMax) {
		this.co2Emission = co2Emission;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}

	public void init(Grid<Object> grid) {
		this.location = grid.getLocation(this); // pb de get location
		this.grid = grid;
//		System.out.println("starting location = " + location.getX() + " , "	+ location.getY());

	}

	@ScheduledMethod(start = 5, interval = 1)
	public void step() {
		produceCo2();
		move();
	}

	private void produceCo2() {
		Iterator agents = grid.getObjectsAt(location.getX(), location.getY())
				.iterator();
		while (agents.hasNext()) {
			Object isItZone = agents.next();
			if (isItZone instanceof Zone) {
				((Zone) isItZone).addCo2Value(co2Emission);
			}
		}
	}

	private void move() {

		if (speedCount == speed) {
			speedCount = 0;

			VNQuery<Object> vnMove = new VNQuery<Object>(grid, this);
			ArrayList<Object> vnZones = new ArrayList<Object>();
			vnMove.query().forEach(vnZones::add);

			int west = 0;
			int east = 1;
			int south = 2;
			int north = 3;

			// /////////////////////////////////////////
			// System.out.println("before move  location = " + location.getX() +
			// " , " + location.getY());

			int pointMovingTo = RandomHelper.nextIntFromTo(west, north);
			int x = location.getX(), y = location.getY();

			boolean isMoving = false;
			int attempts = 0;

			do {
				pointMovingTo = RandomHelper.nextIntFromTo(west, north);
				x = location.getX();
				y = location.getY();

				if (pointMovingTo == west) {
					x = location.getX() - 1;
					y = location.getY();
				} else if (pointMovingTo == east) {
					x = location.getX() + 1;
					y = location.getY();
				} else if (pointMovingTo == south) {
					x = location.getX();
					y = location.getY() - 1;
				} else if (pointMovingTo == north) {
					x = location.getX();
					y = location.getY() + 1;
				}

				if (!(grid.getObjectAt(x, y) instanceof Vehicule)
						&& !(grid.getObjectAt(x, y) instanceof Person)
						&& !(grid.getObjectAt(x, y) instanceof Factory)
						&& !(grid.getObjectAt(x, y) instanceof Tree)
						&& x > xMin && x <= xMax && y > yMin && y <= yMax)
					isMoving = true;

				attempts++;
			} while (attempts <= MAX_MOVE_ATTEMPTS && !isMoving);

			if (isMoving)
				grid.moveTo(this, x, y);

			// System.out.println("new location = " + x +" , "+ y);
			// System.out.println(" voisins = " + vnZones.size());

			this.location = grid.getLocation(this);

			// /////////////////////////////////////////////
		} else
			speedCount++;

	}

	public GridPoint getLocation() {
		return location;
	}

}
