package client.interpreter;

import java.util.Stack;

import client.interpreter.LineBasedReader;
import geometry.Point3DH;
import geometry.Rectangle;
import geometry.Vertex3D;
import line.LineRenderer;
//import notProvided.client.Clipper;
//import notProvided.client.DepthCueingDrawable;
import client.interpreter.RendererTrio;
import geometry.Transformation;
import polygon.Polygon;
import polygon.PolygonRenderer;
import polygon.ColorPolygonRenderer;
import polygon.Shader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;


public class SimpInterpreter {
	private static final int NUM_TOKENS_FOR_POINT = 3;
	private static final int NUM_TOKENS_FOR_COMMAND = 1;
	private static final int NUM_TOKENS_FOR_COLORED_VERTEX = 6;
	private static final int NUM_TOKENS_FOR_UNCOLORED_VERTEX = 3;
	private static final char COMMENT_CHAR = '#';
	private RenderStyle renderStyle;
	
	private Transformation CTM;
	private Transformation worldToScreen;
	
	private final PolygonRenderer renderer2 = ColorPolygonRenderer.make();
	
	private static int WORLD_LOW_X = -100;
	private static int WORLD_HIGH_X = 100;
	private static int WORLD_LOW_Y = -100;
	private static int WORLD_HIGH_Y = 100;
	
	private LineBasedReader reader;
	private Stack<LineBasedReader> readerStack;
	private Stack<Transformation> CTMStack;
	
	private Color defaultColor = Color.WHITE;
	private Color ambientLight = Color.BLACK;
	
	private Drawable drawable;
	private Drawable depthCueingDrawable;
	
	private LineRenderer lineRenderer;
	private PolygonRenderer filledRenderer;
	private PolygonRenderer wireframeRenderer;
	private Transformation cameraToScreen;
//	private Clipper clipper;

	public enum RenderStyle {
		FILLED,
		WIREFRAME;
	}
	public SimpInterpreter(String filename, Drawable drawable, RendererTrio renderers) {
		this.drawable = drawable;
		this.depthCueingDrawable = drawable;
		this.lineRenderer = RendererTrio.getLineRenderer();
		this.filledRenderer = renderers.getFilledRenderer();
		this.wireframeRenderer = renderers.getWireframeRenderer();
		this.defaultColor = Color.WHITE;
		makeWorldToScreenTransform(drawable.getDimensions());
		
		reader = new LineBasedReader(filename);
		readerStack = new Stack<>();
		CTMStack = new Stack<>();
		renderStyle = RenderStyle.FILLED;
		CTM = Transformation.identity();
		
		makeWorldToScreenTransform(drawable.getDimensions());
	}

	private void makeWorldToScreenTransform(Dimensions dimensions) {
		// TODO: fill this in		
		worldToScreen = Transformation.identity();
		worldToScreen.scale(dimensions.getWidth()/(WORLD_HIGH_X - WORLD_LOW_X), dimensions.getHeight()/(WORLD_HIGH_Y - WORLD_LOW_Y), 1, false);
		worldToScreen.translate(dimensions.getWidth()/2.0, dimensions.getHeight()/2.0, 0, false);
	}
	
	public void interpret() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretLine(line);
			while(!reader.hasNext()) {
				if(readerStack.isEmpty()) {
					return;
				}
				else {
					reader = readerStack.pop();
				}
			}
		}
	}
	public void interpretLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretCommand(tokens);
			}
		}
	}
	private void interpretCommand(String[] tokens) {
		switch(tokens[0]) {
		case "{" :      push();   break;
		case "}" :      pop();    break;
		case "wire" :   wire();   break;
		case "filled" : filled(); break;
		
		case "file" :		interpretFile(tokens);		break;
		case "scale" :		interpretScale(tokens);		break;
		case "translate" :	interpretTranslate(tokens);	break;
		case "rotate" :		interpretRotate(tokens);	break;
		case "line" :		interpretLine(tokens);		break;
		case "polygon" :	interpretPolygon(tokens);	break;
//		case "camera" :		interpretCamera(tokens);	break;
//		case "surface" :	interpretSurface(tokens);	break;
//		case "ambient" :	interpretAmbient(tokens);	break;
//		case "depth" :		interpretDepth(tokens);		break;
//		case "obj" :		interpretObj(tokens);		break;
		
		default :
			System.err.println("bad input line: " + tokens);
			break;
		}
	}

	private void push() {
		// TODO: finish this method
		Transformation CTM2 = Transformation.identity();
		CTM2.copy(CTM);
		CTMStack.push(CTM2);
		
	}
	
	private void pop() {
		// TODO: finish this method
		CTM = CTMStack.pop();
	}
	
	private void wire() {
		// TODO: finish this method
		System.out.println("wire");
		renderStyle = RenderStyle.WIREFRAME;
	}
	
	private void filled() {
		// TODO: finish this method
		System.out.println("filled");
		renderStyle = RenderStyle.FILLED;
	}
	
	// this one is complete.
	private void interpretFile(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"'; 
		String filename = quotedFilename.substring(1, length-1);
		file(filename + ".simp");
	}
	
	private void file(String filename) {
		readerStack.push(reader);
		reader = new LineBasedReader(filename);
	}	

	private void interpretScale(String[] tokens) {
		double sx = cleanNumber(tokens[1]);
		double sy = cleanNumber(tokens[2]);
		double sz = cleanNumber(tokens[3]);
		// TODO: finish this method
		CTM.scale(sx, sy, sz, true);
	}
	
	private void interpretTranslate(String[] tokens) {
		double tx = cleanNumber(tokens[1]);
		double ty = cleanNumber(tokens[2]);
		double tz = cleanNumber(tokens[3]);
		// TODO: finish this method
		CTM.translate(tx, ty, tz, true);
	}
	
	private void interpretRotate(String[] tokens) {
		String axisString = tokens[1];
		double angleInDegrees = cleanNumber(tokens[2]);
		// TODO: finish this method
		double angleinRad = Math.toRadians(angleInDegrees);
		
		switch(axisString)
		{
		case("X"): CTM.rotateX(angleinRad, true);
		break;
		
		case("Y"): CTM.rotateY(angleinRad, true);
		break;
		
		case("Z"): CTM.rotateZ(angleinRad, true);
		break;	
		}
	}
	
	private double cleanNumber(String string) {
		return Double.parseDouble(string);
	}
	
	private enum VertexColors {
		COLORED(NUM_TOKENS_FOR_COLORED_VERTEX),
		UNCOLORED(NUM_TOKENS_FOR_UNCOLORED_VERTEX);
		
		private int numTokensPerVertex;
		
		private VertexColors(int numTokensPerVertex) {
			this.numTokensPerVertex = numTokensPerVertex;
		}
		
		public int numTokensPerVertex() {
			return numTokensPerVertex;
		}
	}
	
	private void interpretLine(String[] tokens) {			
		Vertex3D[] vertices = interpretVertices(tokens, 2, 1);

		// TODO: finish this method
		Vertex3D p0 = vertices[0];
		Vertex3D p1 = vertices[1];
//		lineRenderer.drawLine(vertices[0], vertices[1], drawable);
		line(p0, p1);
	}	
	
	private void interpretPolygon(String[] tokens) {			
		Vertex3D[] vertices = interpretVertices(tokens, 3, 1);

		// TODO: finish this method
		Polygon polygon = Polygon.make(vertices);
		if (renderStyle == RenderStyle.FILLED)
		{
			filledRenderer.drawPolygon(polygon, drawable);
		}
		else
		{
			wireframeRenderer.drawPolygon(polygon, drawable);
		}

	}
	
	
	
	public Vertex3D[] interpretVertices(String[] tokens, int numVertices, int startingIndex) {
		VertexColors vertexColors = verticesAreColored(tokens, numVertices);	
		Vertex3D vertices[] = new Vertex3D[numVertices];
		
		for(int index = 0; index < numVertices; index++) {
			vertices[index] = interpretVertex(tokens, startingIndex + index * vertexColors.numTokensPerVertex(), vertexColors);
		}
		return vertices;
	}
	
	public VertexColors verticesAreColored(String[] tokens, int numVertices) {
		return hasColoredVertices(tokens, numVertices) ? VertexColors.COLORED :
														 VertexColors.UNCOLORED;
	}
	
	public boolean hasColoredVertices(String[] tokens, int numVertices) {
		return tokens.length == numTokensForCommandWithNVertices(numVertices);
	}
	
	public int numTokensForCommandWithNVertices(int numVertices) {
		return NUM_TOKENS_FOR_COMMAND + numVertices*(NUM_TOKENS_FOR_COLORED_VERTEX);
	}

	
	private Vertex3D interpretVertex(String[] tokens, int startingIndex, VertexColors colored) {
		Point3DH point = interpretPoint(tokens, startingIndex);
		
		Color color = defaultColor;
		if(colored == VertexColors.COLORED) {
			color = interpretColor(tokens, startingIndex + NUM_TOKENS_FOR_POINT);
		}

		// TODO: finish this method
		Vertex3D v = new Vertex3D(point,color);				
		return worldToScreen.getV3DTrans(CTM.getV3DTrans(v));
	}
	
	public Point3DH interpretPoint(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);

		// TODO: finish this method
		return new Point3DH(x,y,z);
	}
	
	public Color interpretColor(String[] tokens, int startingIndex) {
		double r = cleanNumber(tokens[startingIndex]);
		double g = cleanNumber(tokens[startingIndex + 1]);
		double b = cleanNumber(tokens[startingIndex + 2]);

		// TODO: finish this method
		return new Color(r,g,b);
	}

	private void line(Vertex3D p1, Vertex3D p2) {
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);
		// TODO: finish this method
//		lineRenderer.drawLine(screenP1, screenP2, drawable);
	}
	
	private void polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3) {
//		Vertex3D screenP1 = transformToCamera(p1);
//		Vertex3D screenP2 = transformToCamera(p2);
//		Vertex3D screenP3 = transformToCamera(p3);
//		// TODO: finish this method
//		
//		Polygon polygon = Polygon.make(screenP1, screenP2, screenP3);
//		filledRenderer.drawPolygon(polygon, drawable);
	}

	private Vertex3D transformToCamera(Vertex3D vertex) {
		// TODO: finish this method
//		int x = vertex.getIntX();
//		int y = vertex.getIntY();
//		int z = vertex.getIntZ();
//		
//		cameraToScreen = Transformation.identity();
//		cameraToScreen.scale(300, 300, 300);
//		cameraToScreen.translate(325, 325, -50);
//		cameraToScreen.translate(x, y, z);
//		Color color = vertex.getColor();
//		Vertex3D v = new Vertex3D(cameraToScreen.get_x(), cameraToScreen.get_y(), cameraToScreen.get_z(), color);
//		return v;
		return null;
	}

}
