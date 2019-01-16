package polygon;

import java.awt.Panel;

import geometry.Vertex3D;
import line.DDALineRenderer;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class WireFramePolygonRenderer implements PolygonRenderer {

	private WireFramePolygonRenderer() {}
	
	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader) {
		
		
		LineRenderer DDAdrawer = DDALineRenderer.make();
		
		polygon = Polygon.makeEnsuringClockwise(polygon.get(0),polygon.get(1),polygon.get(2));
		
		Chain LChain = polygon.leftChain();
		Chain RChain = polygon.rightChain();
		
		Vertex3D p0 = RChain.vertices.get(0);
		Vertex3D p1 = LChain.vertices.get(1);
		Vertex3D p2 = RChain.vertices.get(1);
		
		DDAdrawer.drawLine(p0, p1, drawable);
		DDAdrawer.drawLine(p1, p2, drawable);
		DDAdrawer.drawLine(p2, p0, drawable);
		
	
	}
	
	public static PolygonRenderer make() {
		return new WireFramePolygonRenderer();
	}
	
}
