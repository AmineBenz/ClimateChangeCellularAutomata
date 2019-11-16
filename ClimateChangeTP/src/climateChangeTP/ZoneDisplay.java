package climateChangeTP;

import java.awt.Color;

import climateChangeTP.Zone;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.VSpatial;

public class ZoneDisplay extends DefaultStyleOGL2D {

	private ShapeFactory2D shapeFactory;

	@Override
	public void init(ShapeFactory2D factory) {
		this.shapeFactory = factory;
	}

	@Override
	public Color getColor(Object agent) {

		// carbon percentage 0 -> 1
		float carbonPer = (float) ((Zone) agent).getCo2Percentage();
		// System.err.println(carbonPer + " is this > 0 ??????????");
		
		// non carbon percentage 0 -> 1 AKA : what is left
		float otherPer = 1 - carbonPer;

		
		// calculating ground color
		Color baseColor = new Color(0, 0, 0);
		if (((Zone) agent).getZoneType() == Zone.DESERT_TYPE)
			baseColor = new Color(220, 150, 16);
		else if (((Zone) agent).getZoneType() == Zone.POPULATED_TYPE)
			baseColor = new Color(100, 160, 140);
		else if (((Zone) agent).getZoneType() == Zone.SKY_TYPE)
			baseColor = new Color(160, 210, 240);
		else // SEA
			baseColor = new Color(18, 81, 192);

		// color used to represent Co2
		Color carbonColor = Color.RED;
		
		// System.err.println((int) (carbonColor.getRed() * carbonPer) +
		// " is this out of range ? ?? ?? ??  ? ? ? ? ??");
		// System.err.println((int) (carbonColor.getGreen() * carbonPer) +
		// " is this out of range ? ?? ?? ??  ? ? ? ? ??");
		// System.err.println((int) (carbonColor.getBlue() * carbonPer) +
		// " is this out of range ? ?? ?? ??  ? ? ? ? ??");
		
		// calculating render color, combined with the two colors
		carbonColor = new Color((int) (carbonColor.getRed() * carbonPer),
				(int) (carbonColor.getGreen() * carbonPer),
				(int) (carbonColor.getBlue() * carbonPer));
		baseColor = new Color((int) (baseColor.getRed() * otherPer),
				(int) (baseColor.getGreen() * otherPer),
				(int) (baseColor.getBlue() * otherPer));

		// combining
		Color renderColor = new Color(
				carbonColor.getRed() + baseColor.getRed(),
				carbonColor.getGreen() + baseColor.getGreen(),
				carbonColor.getBlue() + baseColor.getBlue());

		return renderColor;
	}

	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		if (spatial == null) {
			spatial = shapeFactory.createRectangle(15, 15);

		}
		return spatial;
	}
}
