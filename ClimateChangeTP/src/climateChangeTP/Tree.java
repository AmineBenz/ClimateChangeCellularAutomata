package climateChangeTP;

import java.util.Iterator;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Tree {

	GridPoint 	location;
	Grid		grid;
	
	double 		co2Emission;
	
	public Tree(double co2Emission){
		this.co2Emission	= co2Emission;
	}
	
	public void init (Grid<Object> grid ){
		this.location	= grid.getLocation(this);
		this.grid		= grid;
	}
	
	@ScheduledMethod(start=5,interval=1)
	public void step(){
		consumeCo2();
	}

	public GridPoint getLocation() {
		return location;
	}
	
	private void consumeCo2() {
		Iterator agents = grid.getObjectsAt(location.getX(), location.getY()).iterator();
		while (agents.hasNext()){
			Object isItZone = agents.next();
			if (isItZone instanceof Zone){
				((Zone) isItZone).addCo2Value(co2Emission);
			}
		}
	}
	
	
}
