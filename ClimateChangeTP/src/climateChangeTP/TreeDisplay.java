package climateChangeTP;

import java.io.IOException;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.VSpatial;

public class TreeDisplay extends DefaultStyleOGL2D {

	private ShapeFactory2D shapeFactory;

	@Override
	public void init(ShapeFactory2D factory) {
		this.shapeFactory = factory;
	}

	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		if (spatial == null) {
			try {
				spatial = shapeFactory.createImage("icons/Tree.png");
			} catch (IOException e) {
				e.printStackTrace();
				spatial = shapeFactory.createCircle(5, 1);
			}
		}

		return spatial;
	}

}
