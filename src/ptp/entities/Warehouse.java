package ptp.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ptp.properties.PropertiesBean;

public class Warehouse {

	private int x;
	private int y;
	private int z;

	private int[][][] warehouseClasses;
	private int warehouseSize;

	private int lastX;
	private int lastY;
	private int lastZ;


	private Map<Integer, Double> ADcs = new HashMap<Integer, Double>();
	private Map<Integer, Double> ATcs = new HashMap<Integer, Double>();
	private Map<Integer, Double> demands = new HashMap<Integer, Double>();
	
	public Warehouse(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.lastX = 0;
		this.lastY = 0;
		this.lastZ = 0;

		this.warehouseClasses = new int[x][y][z];
	}

	public void addClass(int clas, int quantity) {
		for (int i = 0; i < quantity; i++) {

			if (lastX < x) {
				this.warehouseClasses[lastX][lastY][lastZ] = clas;
				lastX++;
			} else if ((lastZ + 1) < z) {
				lastZ++;
				lastX = 0;
				this.warehouseClasses[lastX][lastY][lastZ] = clas;
				lastX++;
			} else if ((lastY + 1) < y)  {
				lastY++;
				lastX = 0;
				lastZ = 0;
				this.warehouseClasses[lastX][lastY][lastZ] = clas;
				lastX++;
			}
		}
		warehouseSize += quantity;
	}

	public void calculateAvarageDistanceAndTimeByClass(Integer clas, PropertiesBean pb) {
		Double ADc = 0.0;
		Double ATc = 0.0;
		int q = 0;

		int xc = 0;
		int yc = 0;
		int zc = 0;
		
		for (int i = 0; i < warehouseSize; i++) {
			// just walk
			if (this.warehouseClasses[xc][yc][zc] != clas) {
				if (xc < (x-1)) {
					xc++;
				} else if ((zc + 1) < z) {
					zc++;
					xc = 0;
				} else if ((yc + 1) < y)  {
					yc++;
					xc = 0;
					zc = 0;
				}
			} else {
			// calculate distance
				Double DSx = 0.0;
				
				double ax1 = ((pb.getWa()*pb.getNa())+(pb.getLp()*pb.getNx()))/2.0;
				int aisle = (xc/2) + 1;
				double ax2 = (2.0*(aisle) - 1.0)*(pb.getLp()+(pb.getWa()/2.0));
				DSx = Math.abs(ax1 - ax2);
				
				Double DSy = Math.abs(((yc + 1) - 0.5)*pb.getWp());
				Double DSz = (zc)*pb.getHp();
				
				ADc += (DSx + DSy + DSz); 
				
				ATc += ((DSx + DSy)/pb.getRxy()) + (DSz/pb.getRz());
				q++;
				
				if (xc < (x-1)) {
					xc++;
				} else if ((zc + 1) < z) {
					zc++;
					xc = 0;
				} else if ((yc + 1) < y)  {
					yc++;
					xc = 0;
					zc = 0;
				}
			}
		}
		if (q != 0) {
			this.ADcs.put(clas, (ADc/q));
			this.ATcs.put(clas, (ATc/q));
		}
	}
	
	public Double getADC(Integer clas) {
		return this.ADcs.get(clas);
	}
	
	public Double getATC(Integer clas) {
		return this.ATcs.get(clas);
	}
	
	public Set<Integer> getClasses() {
		return this.ADcs.keySet();
	}

	public void addDemand(int i, double demand) {
		this.demands.put(i, demand);
	}
	
	public Double getDemand(Integer clas) {
		return this.demands.get(clas);
	}
}
