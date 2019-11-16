package climateChangeTP;

import java.util.ArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.VNQuery;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Person {

	public static final int MAX_MOVE_ATTEMPTS = 20;

	GridPoint location;
	Grid grid;

	int xMin, xMax, yMin, yMax;

	int speed		= 30;
	int speedCount	= 0;

	public Person(int xMin, int xMax, int yMin, int yMax) {
		this.xMin			= xMin;
		this.xMax			= xMax;
		this.yMin			= yMin;
		this.yMax			= yMax;
	}

	public void init(Grid<Object> grid) {
		this.location = grid.getLocation(this);
		this.grid = grid;
	}

	@ScheduledMethod(start = 5, interval = 1)
	public void step() {
		//		move();
	}

	/**
	 * not used, enable a person agent to move along the grid using a speed
	 */
	private void move() {

		if (speedCount == speed) {
			speedCount	= 0;

			VNQuery<Object> vnMove = new VNQuery<Object>(grid, this);
			ArrayList<Object> vnZones = new ArrayList<Object>();
			vnMove.query().forEach(vnZones::add);

			int west = 0;
			int east = 1;
			int south = 2;
			int north = 3;

			// /////////////////////////////////////////

			System.out.println("before move  location = " + location.getX()
					+ " , " + location.getY());

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
						&& x > xMin && x <= xMax && y > yMin && y <= yMax) {
					isMoving = true;
					System.err.println("boooooooooooom");
				}

				attempts++;
			} while (attempts <= MAX_MOVE_ATTEMPTS && !isMoving);

			if (isMoving) {
				grid.moveTo(this, x, y);
				System.out.println("new location = " + x + " , " + y);
				System.out.println(" voisins = " + vnZones.size());
			}

			this.location = grid.getLocation(this);
		}
		else 
			speedCount ++;
	}

	public GridPoint getLocation() {
		return location;
	}

}
