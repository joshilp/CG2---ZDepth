package client;

import client.testPages.StarburstLineTest;
import client.testPages.ParallelogramTest;
import client.testPages.RandomLineTest;
import client.testPages.CenteredTriangleTest;
import client.testPages.MeshPolygonTest;
import client.testPages.RandomPolygonTest;
import client.testPages.StarburstPolygonTest;

import client.interpreter.SimpInterpreter;

import client.interpreter.LineBasedReader;
import client.interpreter.RendererTrio;
import geometry.Point2D;

import line.AlternatingLineRenderer;
//import line.ExpensiveLineRenderer;
import line.LineRenderer;
import line.DDALineRenderer;
import line.BresenhamLineRenderer;
import line.AntialiasingLineRenderer;


import polygon.FilledPolygonRenderer;
import polygon.PolygonRenderer;
import polygon.ColorPolygonRenderer;
import polygon.WireFramePolygonRenderer;
import windowing.PageTurner;
import windowing.drawable.ColoredDrawable;
import windowing.drawable.DepthCueingDrawable;
import windowing.drawable.Drawable;
//import windowing.drawable.GhostWritingDrawable;
import windowing.drawable.InvertedYDrawable;
import windowing.drawable.TranslatingDrawable;
import windowing.drawable.ZBufferingDrawable;
import windowing.graphics.Dimensions;
import windowing.graphics.Color;

public class Client implements PageTurner {
	private static final int ARGB_WHITE = 0xff_ff_ff_ff;
	private static final int ARGB_GREEN = 0xff_00_ff_40;
	
	private static final int NUM_PAGES = 7;
//	protected static final double GHOST_COVERAGE = 0.14;

//	private static final int NUM_PANELS = 4; //Assignment 1
	private static final int NUM_PANELS = 1; //Assignment 2
	private static final Dimensions PANEL_SIZE = new Dimensions(650, 650);
//	private static final Point2D[] lowCornersOfPanels = {
//			new Point2D( 50, 400),
//			new Point2D(400, 400),
//			new Point2D( 50,  50),
//			new Point2D(400,  50),
//	};
	
	private final Drawable drawable;
	private int pageNumber = 0;
	
	private Drawable image;
	private Drawable[] panels;
//	private Drawable[] ghostPanels;					// use transparency and write only white
//	private Drawable largePanel; //Assignment 1
	private Drawable fullPanel; //Assignment 2
	
	private LineRenderer lineRenderers[];
	private PolygonRenderer polygonRenderer;
	private PolygonRenderer wireframeRenderer;
	private PolygonRenderer rainbowRenderer;
	private RendererTrio renderers;
	
	
	
	
	public Client(Drawable drawable) {
		this.drawable = drawable;	
		createDrawables();
		createRenderers();
	}

	public void createDrawables() {
		image = new InvertedYDrawable(drawable);
		image = new TranslatingDrawable(image, point(0, 0), dimensions(750, 750));
		image = new ColoredDrawable(image, ARGB_WHITE);
		
//		largePanel = new TranslatingDrawable(image, point(  50, 50),  dimensions(650, 650)); //Assignment 1
		fullPanel = new TranslatingDrawable(image, point(  50, 50),  dimensions(650, 650)); //Assignment 2
		fullPanel = new ZBufferingDrawable(fullPanel);
		
		createPanels();
//		createGhostPanels();
	}

	public void createPanels() {
		panels = new Drawable[NUM_PANELS];
		
		for(int index = 0; index < NUM_PANELS; index++) {
//			panels[index] = new TranslatingDrawable(image, lowCornersOfPanels[index], PANEL_SIZE);
			panels[index] = new TranslatingDrawable(image, point(50, 50), PANEL_SIZE);
		}
	}

	private Point2D point(int x, int y) {
		return new Point2D(x, y);
	}	
	private Dimensions dimensions(int x, int y) {
		return new Dimensions(x, y);
	}
	private void createRenderers() {
		rainbowRenderer = ColorPolygonRenderer.make();
		wireframeRenderer = WireFramePolygonRenderer.make();
		polygonRenderer = FilledPolygonRenderer.make();
	}
	
	// Assignment 2
		@Override
	public void nextPage() {
		Drawable depthCueingDrawable;
		System.out.println("PageNumber " + (pageNumber + 1));
		pageNumber = (pageNumber + 1) % NUM_PAGES;
		
		image.clear();
		fullPanel.clear();

		switch(pageNumber) {
		case 1:  new MeshPolygonTest(fullPanel, wireframeRenderer, MeshPolygonTest.USE_PERTURBATION);
				 break;
		case 2:  new MeshPolygonTest(fullPanel, rainbowRenderer, MeshPolygonTest.USE_PERTURBATION);
				 break;
		case 3:	 new CenteredTriangleTest(fullPanel, rainbowRenderer);
				 break;

		case 4:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.GREEN); 
				 SimpInterpreter interpreter = new SimpInterpreter("joshsPage4.simp", depthCueingDrawable, renderers);
				 interpreter.interpret();
				 break;

		case 5:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.BLUE);
				 interpreter = new SimpInterpreter("joshsPage5.simp", depthCueingDrawable, renderers);
				 interpreter.interpret();
				 break;

		case 6:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.WHITE);
				 interpreter = new SimpInterpreter("page6.simp", depthCueingDrawable, renderers);
				 interpreter.interpret();
				 break;		

		case 7:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.WHITE);
				 interpreter = new SimpInterpreter("page7.simp", depthCueingDrawable, renderers);
				 interpreter.interpret();
				 break;	

		case 0:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.WHITE);
				 interpreter = new SimpInterpreter("page8.simp", depthCueingDrawable, renderers);
				 interpreter.interpret();
				 break;	

		default: defaultPage();
				 break;
		}
	}

	private void defaultPage() {
		image.clear();
		fullPanel.fill(ARGB_GREEN, Double.MAX_VALUE);
//		largePanel.fill(ARGB_GREEN, Double.MAX_VALUE);
	}
}
