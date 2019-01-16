package polygon;

import java.awt.Panel;

import geometry.Vertex3D;
import line.DDALineRenderer;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class ColorPolygonRenderer implements PolygonRenderer {

	private ColorPolygonRenderer() {}
	
	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader) {
		
		LineRenderer DDAdrawer = DDALineRenderer.make();
		
		//polygon = Polygon.makeEnsuringClockwise(polygon.get(0),polygon.get(1),polygon.get(2));		
		
		Chain LChain = polygon.leftChain();
		Chain RChain = polygon.rightChain();
		
		int lengthL = LChain.numVertices;
		int lengthR = RChain.numVertices;

		if ((lengthL + lengthR) >= 3) {
		
			Vertex3D p0 = RChain.vertices.get(0);
			Vertex3D p1 = LChain.vertices.get(1);
			Vertex3D p2 = RChain.vertices.get(1);
			
			int p0_x = p0.getIntX();
			int p0_y = p0.getIntY();
			int p0_z = p0.getIntZ();
			double p0_r = p0.getColor().getR();
			double p0_g = p0.getColor().getG();
			double p0_b = p0.getColor().getB();
			
			int p1_x = p1.getIntX();
			int p1_y = p1.getIntY();
			int p1_z = p1.getIntZ();
			double p1_r = p1.getColor().getR();
			double p1_g = p1.getColor().getG();
			double p1_b = p1.getColor().getB();
			
			int p2_x = p2.getIntX();
			int p2_y = p2.getIntY();	
			int p2_z = p2.getIntZ();
			double p2_r = p2.getColor().getR();
			double p2_g = p2.getColor().getG();
			double p2_b = p2.getColor().getB();
			
			double dx_left = p0_x - p1_x;
			double dy_left = p0_y - p1_y;
			double dz_left = p0_z - p1_z;
			double dr_left = p0_r - p1_r;
			double dg_left = p0_g - p1_g;
			double db_left = p0_b - p1_b;
			double m_left = dx_left/dy_left;
			double mz_left = dz_left/dy_left;
			double mr_left = dr_left/dy_left;
			double mg_left = dg_left/dy_left;
			double mb_left = db_left/dy_left;
			
			double dx_right = p2_x - p0_x;
			double dy_right = p2_y - p0_y;
			double dz_right = p2_z - p0_z;
			double dr_right = p2_r - p0_r;
			double dg_right = p2_g - p0_g;
			double db_right = p2_b - p0_b;
			double m_right = dx_right/dy_right;
			double mz_right = dz_right/dy_right;
			double mr_right = dr_right/dy_right;
			double mg_right = dg_right/dy_right;
			double mb_right = db_right/dy_right;
			
			double dx_low = p1_x - p2_x;
			double dy_low = p1_y - p2_y;
			double dz_low = p1_z - p2_z;
			double dr_low = p1_r - p2_r;
			double dg_low = p1_g - p2_g;
			double db_low = p1_b - p2_b;
			double m_low = dx_low/dy_low; 
			double mz_low = dz_low/dy_low;
			double mr_low = dr_low/dy_low;
			double mg_low = dg_low/dy_low;
			double mb_low = db_low/dy_low;
	
			double y_middle = Math.max(p1_y, p2_y);
			double y_bottom = Math.min(p1_y, p2_y);
			
			double fx_left = p0_x;
			double fx_right = p0_x;
			double fz_left = p0_z;
			double fz_right = p0_z;
			
			int xleft = p0_x;
			int xright = p0_x;
	
			double r_left = p0.getColor().getR();
			double g_left = p0.getColor().getG();
			double b_left = p0.getColor().getB();
			
			double r_right = p0.getColor().getR();
			double g_right = p0.getColor().getG();
			double b_right = p0.getColor().getB();
			
			if (dy_left == 0)
			{
				fx_left = p1_x;
				r_left = p1_r;
				g_left = p1_g;
				b_left = p1_b;
				fz_left = p1_z;
			}
			
			if (dy_right == 0)
			{
				fx_right = p2_x;
				r_right = p2_r;
				g_right = p2_g;
				b_right = p2_b;
				fz_right = p2_z;
			}
			
			for (int y = p0_y; y > y_bottom; y--)
			{
				Color rgb_left = new Color(r_left, g_left, b_left);
				Color rgb_right = new Color(r_right, g_right, b_right);
	
				xleft = (int)Math.round(fx_left);
				xright = ((int)Math.round(fx_right));
	
				if (xleft <= xright-1)
				{
					Vertex3D v3d_xleft = new Vertex3D(xleft, y, fz_left, rgb_left);
					Vertex3D v3d_xright = new Vertex3D(xright-1, y, fz_right, rgb_right);
					DDAdrawer.drawLine(v3d_xleft, v3d_xright, drawable);
				}
				
				if (y > y_middle)
				{
					fx_left -= m_left;
					fx_right -= m_right;
	
					r_left -= mr_left;
					g_left -= mg_left;
					b_left -= mb_left;
	
					r_right -= mr_right;
					g_right -= mg_right;
					b_right -= mb_right;
					
					fz_left -= mz_left;
					fz_right -= mz_right;
				}
				
				if (y <= y_middle && p1_y > p2_y)
				{
					fx_left -= m_low;
					fx_right -= m_right;
	
					r_left -= mr_low;
					g_left -= mg_low;
					b_left -= mb_low;
	
					r_right -= mr_right;
					g_right -= mg_right;
					b_right -= mb_right;
					
					fz_left -= mz_low;
					fz_right -= mz_right;
				}
				
				if (y <= y_middle && p1_y < p2_y)
				{
					fx_left -= m_left;
					fx_right -= m_low;	
	
					r_left -= mr_left;
					g_left -= mg_left;
					b_left -= mb_left;
	
					r_right -= mr_low;
					g_right -= mg_low;
					b_right -= mb_low;
					
					fz_left -= mz_left;
					fz_right -= mz_low;
				}
				
			}
		
		}
	}
	
	public static PolygonRenderer make() {
		return new ColorPolygonRenderer();
	}
	
}
	