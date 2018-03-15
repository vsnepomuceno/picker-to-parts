package ptp.ag.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ManagedBean;

@ManagedBean(name="population")
public class Population {
	
	private List<Individual> population;
	private List<Individual> pareto;
	
	private List<Individual> populationOrderBySpace;
	private List<Individual> populationOrderByTD;
	private List<Individual> populationOrderByTT;
	private List<Individual> populationOrderByCD;

	public Population() {
		this.population = new ArrayList<Individual>();
		this.pareto = new ArrayList<Individual>();
	}
	
	public void addIndividual(Individual i) {
		population.add(i);
	}
	
	public void addIndividualPareto(Individual i) {
		pareto.add(i);
	}
	
	public void removeIndividual(Individual i){
		population.remove(i);
	}
	
	public Integer getSize() {
		return this.population.size();
	}
	
	public List<Individual> getPopulation() {
		return this.population;
	}

	public List<Individual> getPopulationOrderBySpace() {
		this.populationOrderBySpace = new ArrayList<Individual>();		
		this.populationOrderBySpace.addAll(0, this.getPopulation());
		
		Collections.sort(this.populationOrderBySpace, new SpaceComparator());
		
		return this.populationOrderBySpace;
	}
	
	public List<Individual> getPopulationOrderByTotalDistance() {
		this.populationOrderByTD = new ArrayList<Individual>();		
		this.populationOrderByTD.addAll(0, this.getPopulation());
		
		Collections.sort(this.populationOrderByTD, new TotalDistanceComparator());
		
		return this.populationOrderByTD;
	}
	
	public List<Individual> getPopulationOrderByTotalTime() {
		this.populationOrderByTT = new ArrayList<Individual>();
		
		this.populationOrderByTT.addAll(0, this.getPopulation());
		
		Collections.sort(this.populationOrderByTT, new TotalTimeComparator());
		
		return this.populationOrderByTT;
	}
	
	public List<Individual> getPopulationOrderByCrouddistance() {
		this.populationOrderByCD = new ArrayList<Individual>();
		
		this.populationOrderByCD.addAll(0, this.getPopulation());
		
		Collections.sort(this.populationOrderByCD, new CroudDistanceComparator());
		
		return this.populationOrderByCD;
	}
	
	class SpaceComparator implements Comparator<Individual>{

		@Override
		public int compare(Individual o1, Individual o2) {
			return o2.getFitnessSpace().compareTo(o1.getFitnessSpace());
		}
	}
	
	class TotalDistanceComparator implements Comparator<Individual>{

		@Override
		public int compare(Individual o1, Individual o2) {
			return o2.getFitnessTotalDistance().compareTo(o1.getFitnessTotalDistance());
		}
	}
	
	class TotalTimeComparator implements Comparator<Individual>{

		@Override
		public int compare(Individual o1, Individual o2) {
			return o2.getFitnessTotalTime().compareTo(o1.getFitnessTotalTime());
		}
	}
	
	class CroudDistanceComparator implements Comparator<Individual>{

		@Override
		public int compare(Individual o1, Individual o2) {
			return o2.getCroudDistance().compareTo(o1.getCroudDistance());
		}
	}
}
