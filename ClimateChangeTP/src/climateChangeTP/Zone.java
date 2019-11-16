package climateChangeTP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.VNQuery;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Zone {
	
	public static final int DESERT_TYPE		= 0;
	public static final int SEA_TYPE		= 1;
	public static final int POPULATED_TYPE	= 2;
	public static final int SKY_TYPE		= 3;
	
	GridPoint 	location;
	Grid		grid;
	
	double 		co2Level		= 0;
	double		vaporatedCo2	= 0;
	
	double		co2MaxLevel, propagationPer, vaporationPer, airVaporationPer;
	
	int vaporationSpeed;
	int vaporationSpeedCount = 0;
	
	int zoneType;
	
	int xMin, xMax, yMin, yMax;
	
	
	
	public Zone(double co2MaxLevel2, double propagationPer, double vaporationPer, int vaporationSpeed, double airVaporationPer, int xMin, int xMax, int yMin, int yMax) {
		this.co2MaxLevel		= co2MaxLevel2;
		this.propagationPer		= propagationPer / 100;
		this.vaporationPer		= vaporationPer / 100;
		this.vaporationSpeed	= vaporationSpeed;
		this.airVaporationPer	= airVaporationPer / 100;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}
	
	public void init (Grid<Object> grid ){
		this.location	= grid.getLocation(this);
		this.grid		= grid;
	}
	
	@ScheduledMethod(start=5,interval=1)
	public void step(){
		
		// getting adjacent zones
		VNQuery<Object> vnQuery	= new VNQuery<Object>(grid, this);
		List<Object> zoneList = new ArrayList<Object>();
		vnQuery.query().forEach(zoneList::add);
		
		List<Object> objectsList = new ArrayList<Object>();
		
		for (Object object : zoneList) {
			if (object instanceof Zone)
				objectsList.add(object);
		}
		zoneList	= new ArrayList<Object>();
		for (Object zone : objectsList) {
			if (!(((Zone) zone).getZoneType() == Zone.SKY_TYPE) && (((Zone) zone).getLocation().getX() > 0))
				zoneList.add(zone);
		}
		
		
		// calculating value to propagate
		double propagatedValue 		=  propagationPer * co2Level;
		
		// sending the value to adjacent zones
		double unit			= 0.01d;
		while (propagatedValue >= unit && zoneList.size()>0 ){
			int index = RandomHelper.nextIntFromTo(0, zoneList.size()-1);
			((Zone) zoneList.get(index)).addCo2Value(unit);
			propagatedValue -= unit;
			// substracting the value from this zone
			co2Level 		-= unit;
		}
		
		// calculating evaporating co2
		if (vaporationSpeedCount == vaporationSpeed) {
			vaporationSpeedCount	= 0;
			
			Zone topZone = null;
			Iterator iterator = grid.getObjectsAt(location.getX(),
					location.getY() + 1).iterator();
			
			while (iterator.hasNext()) {
				Object object = iterator.next();
				if (object instanceof Zone)
					topZone = (Zone) object;
			}
			if (!(topZone == null) && location.getY() < yMax) {
				// calculate Co2Value
				
				if (vaporatedCo2 <= 0) { // this means this is a ground cell
					double vaporatedValue = vaporationPer * co2Level;
					topZone.addEvaporatedCo2Value(vaporatedValue);
					co2Level -= vaporatedValue;
					if (zoneType == SKY_TYPE)
						System.err.println("ground evaporated = "
								+ vaporatedCo2 + " / co2Level = " + co2Level);
					
				} else { // this means the value here is of the sky cell
					double vaporatedValue = airVaporationPer * vaporatedCo2;
					topZone.addEvaporatedCo2Value(vaporatedValue);
					vaporatedCo2 -= vaporatedValue;
					if (zoneType == SKY_TYPE)
						System.err.println("evaporated = " + vaporatedCo2);
				}
			}
		} else {
			vaporationSpeedCount++; // no evaporation this turn
		}
	}

	public GridPoint getLocation() {
		return location;
	}

	public int getZoneType() {
		return zoneType;
	}

	public void setZoneType(int zoneType) {
		this.zoneType = zoneType;
	}
	
	/**
	 * adds the given co2 value to the zone, preventing the levels from getting higher than the max value or lower than the min value in case the given value is negative
	 * @param value
	 */
	public void addCo2Value(double value){
		this.co2Level += value;
		if (this.co2Level < 0)
			this.co2Level	= 0;
		if (this.co2Level > co2MaxLevel)
			this.co2Level	= co2MaxLevel;
	}
	
	/**
	 * adds the given co2 value to the evaporated co2 in this zone, which is equvalent to the zone it projects at, for example at altitude 10, the tenth zone is used to represent this value
	 * @param value
	 */
	public void addEvaporatedCo2Value(double value) {
		this.vaporatedCo2 += value;
		if (this.vaporatedCo2 < 0)
			this.vaporatedCo2	= 0;
		if (this.vaporatedCo2 > co2MaxLevel)
			this.vaporatedCo2	= co2MaxLevel;
		if (zoneType == SKY_TYPE)
			System.err.println("recieve Value = " + vaporatedCo2);
	}
	
	/**
	 * gets the percentage of both local and evaporation co2, 0 is none, 1 is maximal
	 * @return
	 */
	public double getCo2Percentage() {
		double per = co2Level / co2MaxLevel;
		double vap = vaporatedCo2 / co2MaxLevel;
		per += vap;
		if (per > 1)
			per = 1;
		if (per < 0)
			per = 0;
		return per;
	}
	
	

}
