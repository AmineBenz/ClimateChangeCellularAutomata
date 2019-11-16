package climateChangeTP;

import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class ClimateChangeTPModel implements ContextBuilder<Object> {

	int width 					;
	int height 					;
	int numberOfVehicules		;
	int numberOfTrees			;
	int numberOfFactories		;
	int numberOfPersons			;
	double vehiculeCo2Emission	;
	double treeCo2Emission		;
	double factoryCo2emission	;
	double co2MaxLevel			;
	double propagationPer		;
	double vaporationPer		;
	int vapoSpeed				;
	double airVaporationPer		;
	
	
	Grid<Object> climateChangeMap;
	
	@Override
	public Context build(Context<Object> context) {
		
		// getting all parameters ***********************************************************************
		
		Parameters parameters = (Parameters) RunEnvironment.getInstance().getParameters();
		
		width 	= parameters.getInteger("TileSize");
		height	= width;
		
		numberOfVehicules		= Math.abs(parameters.getInteger("NumbreOfVehicules"));
		numberOfPersons         = Math.abs(parameters.getInteger("NumberOfPersons"));
		numberOfFactories       = Math.abs(parameters.getInteger("NumberOfFactories"));
		numberOfTrees           = Math.abs(parameters.getInteger("NumberOfTrees"));
		
		vehiculeCo2Emission	    = Math.abs(parameters.getDouble("VehiculeCo2Emission"));
		treeCo2Emission		    = - Math.abs(parameters.getDouble("TreeCo2Emission")); // always negative
		factoryCo2emission	    = Math.abs(parameters.getDouble("FactoryCo2Emission"));
		co2MaxLevel			    = Math.abs(parameters.getDouble("Co2MaxLevel"));
		propagationPer		    = Math.abs(parameters.getDouble("PropagationPer"));
		vaporationPer		    = Math.abs(parameters.getDouble("VaporationPer"));
		airVaporationPer	    = Math.abs(parameters.getDouble("AirVaporationRate"));
		
		vapoSpeed				= Math.abs(parameters.getInteger("VaporationSpeed"));
		
		// world creation ********************************************************************************
		
		// grid creation
		createGrid(context);
		
		// create Agents
		populateGrid(context);
	
		return context;
	}

	private void populateGrid(Context<Object> context) {
		
		// getting boundries

		int xMinPopulated = (int) (width * 0.5);
		int xMaxPopulated = width;

		int yMinPopulated = (int) (height * 0.15) - 1;
		int yMaxPopulated = (int) ((height * 0.4) + yMinPopulated) + 1;

		int xMinSea = 0 - 1;
		int xMaxSea = (int) (width * 0.5) + 1;

		int yMinSea = (int) (height * 0.15) - 1;
		int yMaxSea = (int) ((height * 0.4) + yMinPopulated) + 1;

		int xMinDesert = 0;
		int xMaxDesert = width - 1;

		int yMinDesert = 0;
		int yMaxDesert = yMinPopulated - 1;

		int xMinSky = 0 - 1;
		int xMaxSky = width;

		int yMinSky = yMaxPopulated - 1;
		int yMaxSky = height;

		
		// setting vehicules **********************************************************************************************
		for (int i = 0; i < numberOfVehicules; i++) {
			Vehicule vehicule = new Vehicule(vehiculeCo2Emission,
					xMinPopulated, xMaxPopulated - 1, yMinPopulated + 1,
					yMaxPopulated - 1);
			context.add(vehicule);

			// set vehicule in the populated green area
			boolean exists = true;
			int xpos = 0, ypos = 0;
			while (exists) {
				xpos = RandomHelper.nextIntFromTo(xMinPopulated + 1, xMaxPopulated - 1);
				ypos = RandomHelper.nextIntFromTo(yMinPopulated + 1, yMaxPopulated - 1);
				if (climateChangeMap.getObjectAt(xpos, ypos) == null)
					exists = false;
			}
			climateChangeMap.moveTo(vehicule, xpos, ypos);
			vehicule.init(climateChangeMap);
		}

		// setting trees **********************************************************************************************
		for (int i = 0; i < numberOfTrees; i++) {
			Tree tree = new Tree(treeCo2Emission);
			context.add(tree);

			// set tree in the populated green area
			boolean exists = true;
			int xpos = 0, ypos = 0;
			while (exists) {
				xpos = RandomHelper.nextIntFromTo(xMinPopulated + 1, xMaxPopulated - 1);
				ypos = RandomHelper.nextIntFromTo(yMinPopulated + 1, yMaxPopulated - 1);
				if (climateChangeMap.getObjectAt(xpos, ypos) == null)
					exists = false;
			}
			climateChangeMap.moveTo(tree, xpos, ypos);
			tree.init(climateChangeMap);
		}

		// setting persons **********************************************************************************************
		for (int i = 0; i < numberOfPersons; i++) {
			Person person = new Person(xMinPopulated, xMaxPopulated - 1,
					yMinPopulated + 1, yMaxPopulated - 1);
			context.add(person);

			// set person in the populated green area
			boolean exists = true;
			int xpos = 0, ypos = 0;
			while (exists) {
				xpos = RandomHelper.nextIntFromTo(xMinPopulated + 1, xMaxPopulated - 1);
				ypos = RandomHelper.nextIntFromTo(yMinPopulated + 1, yMaxPopulated - 1);
				if (climateChangeMap.getObjectAt(xpos, ypos) == null)
					exists = false;
			}
			climateChangeMap.moveTo(person, xpos, ypos);
			person.init(climateChangeMap);
		}

		// setting factories **********************************************************************************************
		for (int i = 0; i < numberOfFactories; i++) {
			Factory factory = new Factory(factoryCo2emission);
			context.add(factory);

			// set factory in the populated green area
			boolean exists = true;
			int xpos = 0, ypos = 0;
			while (exists) {
				xpos = RandomHelper.nextIntFromTo(xMinPopulated + 1, xMaxPopulated - 1);
				ypos = RandomHelper.nextIntFromTo(yMinPopulated + 1, yMaxPopulated - 1);
				if (climateChangeMap.getObjectAt(xpos, ypos) == null)
					exists = false;
			}
			climateChangeMap.moveTo(factory, xpos, ypos);
			factory.init(climateChangeMap);
		}

		// setting co2 zones **********************************************************************************************

		for (int i = 0; i < width; i++) { // X axe
			for (int j = 0; j < height; j++) { // Y axe
				Zone zone = new Zone(co2MaxLevel, propagationPer,
						vaporationPer, vapoSpeed, airVaporationPer, 0,
						width - 1, 0, height - 1);
				context.add(zone);
				climateChangeMap.moveTo(zone, i, j);
				zone.init(climateChangeMap);

				// setting zone type
				if (i > xMinPopulated && i < xMaxPopulated && j > yMinPopulated && j < yMaxPopulated)
					zone.setZoneType(Zone.POPULATED_TYPE);
				else if (i > xMinSea && i < xMaxSea && j > yMinSea && j < yMaxSea)
					zone.setZoneType(Zone.SEA_TYPE);
				else if (i > xMinSky && i < xMaxSky && j > yMinSky && j < yMaxSky)
					zone.setZoneType(Zone.SKY_TYPE);
				else if (i > xMinDesert && i < xMaxDesert && j > yMinDesert && j < yMaxDesert)
					zone.setZoneType(Zone.DESERT_TYPE);
			}
		}
		
	}

	private void createGrid(Context<Object> context) {
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		climateChangeMap = gridFactory.createGrid("ClimateChangeMap", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new RandomGridAdder<Object>(), true, width, height));
	}

}
