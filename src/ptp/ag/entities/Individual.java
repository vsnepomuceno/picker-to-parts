package ptp.ag.entities;

import java.util.ArrayList;
import java.util.List;

import ptp.entities.Product;
import ptp.entities.Restriction;
import ptp.entities.Restriction.RESTRICTION_TYPE;
import ptp.entities.Warehouse;
import ptp.properties.PropertiesBean;

public class Individual implements Cloneable {

	private List<Gene> genes;
	private Double fitnessSpace;
	private Double fitnessTotalDistance;
	private Double fitnessTotalTime;
	private Warehouse warehouse;
	
	private Double croudDistanceSpace;
	private Double croudDistanceTT;
	private Double croudDistanceTD;
	
	private Double croudDistance;

	public Individual() {
		this.genes = new ArrayList<Gene>();
		this.fitnessSpace = 0.0;
		this.fitnessTotalDistance = 0.0;
		this.fitnessTotalTime = 0.0;
		this.croudDistance = 0.0;
		this.croudDistanceSpace = 0.0;
		this.croudDistanceTD = 0.0;
		this.croudDistanceTT = 0.0;
	}

	public Individual(List<Gene> genes) {
		this.genes = genes;
	}

	public void addGene(Gene g) {
		genes.add(g);
	}

	public void removeGene(Gene g) {
		genes.remove(g);
	}

	public List<Gene> getGenes() {
		return genes;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public boolean validate(List<Restriction> list) {
		boolean valid = true;

		if (list != null && list.size() > 0) {
			for (Restriction r : list) {
				Product p1 = r.getProduct1();
				Product p2 = r.getProduct2();
				RESTRICTION_TYPE resType = r.getResType();
				Gene gp1 = null;
				Gene gp2 = null;

				for (Gene g : this.genes) {
					if (g.getProduct().getName().equals(p1.getName())) {
						gp1 = g;
					}
					if (g.getProduct().getName().equals(p2.getName())) {
						gp2 = g;
					}
				}

				switch (resType) {
				case CONTIGUOUS_CLASS:
					if (gp1.getClas() + 1 == gp2.getClas() || 
							gp1.getClas() - 1 == gp2.getClas() ||
							gp1.getClas() == gp2.getClas()) {
						valid = false;
					}
				case DIFFERENT_CLASS:
					if (gp1.getClas() == gp2.getClas()) {
						valid = false;
					}
					break;
				case SAME_CLASS:
					if (gp1.getClas() != gp2.getClas()) {
						valid = false;
					}
					break;
				default:
					break;
				}

				if (!valid) {
					break;
				}
			}
		}

		return valid;

	}

	public void calculateFitness(PropertiesBean propertiesBean) {

		// alloc warehouse spaces
		int y = 0;
		double soma = 0.0;
		for (Gene g : genes) {
			soma += g.getProduct().getMaxSpaceSize();
		}

		soma = (soma / propertiesBean.getNx()) / propertiesBean.getNz();
		y = (int)(soma + 1.0);

		this.warehouse = new Warehouse(propertiesBean.getNx(), y, propertiesBean.getNz());
		List<Gene> genesByClass = new ArrayList<Gene>();
		for (int i = 1; i <= this.getNumberOfPossibleClasses(); i++) {
			for (Gene g : genes) {
				if (g.getClas() == i) {
					genesByClass.add(g);
				}
			}
			if (genesByClass.size() > 0) {
				int maxInv = getMaxInvetByClass(genesByClass);
				warehouse.addClass(i, maxInv);
				double demand = 0.0;
				for (Gene g : genesByClass) {					
					demand += g.getProduct().getDemand();
				}				
				this.warehouse.addDemand(i , demand);
				genesByClass.clear();
				this.fitnessSpace += maxInv;
			}
		}
		calculateTotalDistanceAndTime(propertiesBean);
			}

	private void calculateTotalDistanceAndTime(PropertiesBean propertiesBean) {
		
		for (int i = 1; i <= this.getNumberOfPossibleClasses(); i++) {
			
			this.warehouse.calculateAvarageDistanceAndTimeByClass(i, 
															propertiesBean);			
			
		}
		for (Integer clas : this.warehouse.getClasses()) {
			try {
				this.fitnessTotalDistance += (this.warehouse.getADC(clas)*
												this.warehouse.getDemand(clas));
				this.fitnessTotalTime += (this.warehouse.getATC(clas)*
											this.warehouse.getDemand(clas));
			}catch(Exception ex) {
				System.out.println(clas);
				ex.printStackTrace();
			}
		}		
	}

	private int getMaxInvetByClass(List<Gene> genesByClass) {
		int maxInv = 0;
		double somaS1 = 0.0;
		double somaS2 = 0.0;
		double somaS3 = 0.0;
		double somaS4 = 0.0;
		for (Gene g : genesByClass) {
			somaS1 += g.getProduct().getSpace1();
			somaS2 += g.getProduct().getSpace2();
			somaS3 += g.getProduct().getSpace3();
			somaS4 += g.getProduct().getSpace4();
		}

		if ((somaS1 > somaS2) && (somaS1 > somaS3) && (somaS1 > somaS4)) {
			maxInv = (int) somaS1;
		} else if ((somaS2 > somaS1) && (somaS2 > somaS3) && (somaS2 > somaS4)) {
			maxInv = (int) somaS2;
		} else if ((somaS3 > somaS1) && (somaS3 > somaS2) && (somaS3 > somaS4)) {
			maxInv = (int) somaS3;
		} else if ((somaS4 > somaS1) && (somaS4 > somaS2) && (somaS4 > somaS3)) {
			maxInv = (int) somaS4;
		}
		return maxInv;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equals = true;
		Individual ind = (Individual) obj;

		for (int i = 0; i < this.genes.size(); i++) {
			if (!this.genes.get(i).equals(ind.genes.get(i))) {
				equals = false;
				break;
			}
		}

		return equals;
	}

	public int getNumberOfPossibleClasses() {
		return this.genes.size();
	}

	@Override
	public Object clone() {
		Individual ind = new Individual();

		for (Gene g : this.genes) {
			ind.addGene((Gene) g.clone());
		}

		return ind;
	}

	@Override
	public String toString() {
		String ret = "";

		for (Gene g : this.genes) {
			ret = ret + g.getClas() + " | ";
		}
		ret += "\n";
		
		ret += "Fit. Spac = " + this.fitnessSpace + " | ";
		ret += "Fit. Dist = " + this.fitnessTotalDistance + " | ";
		ret += "Fit. Time = " + this.fitnessTotalTime + "\n";

		return ret;
	}

	public Double getFitnessSpace() {
		return fitnessSpace;
	}

	public Double getFitnessTotalDistance() {
		return fitnessTotalDistance;
	}

	public Double getFitnessTotalTime() {
		return fitnessTotalTime;
	}

	public Double getCroudDistanceSpace() {
		return croudDistanceSpace;
	}

	public void setCroudDistanceSpace(Double croudDistanceSpace) {
		this.croudDistanceSpace = croudDistanceSpace;
	}

	public Double getCroudDistanceTT() {
		return croudDistanceTT;
	}

	public void setCroudDistanceTT(Double croudDistanceTT) {
		this.croudDistanceTT = croudDistanceTT;
	}

	public Double getCroudDistanceTD() {
		return croudDistanceTD;
	}

	public void setCroudDistanceTD(Double croudDistanceTD) {
		this.croudDistanceTD = croudDistanceTD;
	}

	public Double getCroudDistance() {
		this.croudDistance = this.croudDistanceSpace + 
				this.croudDistanceTD + this.croudDistanceTT;
		return croudDistance;
	}
	
}
