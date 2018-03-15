package ptp.ag;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;

import ptp.ag.entities.Gene;
import ptp.ag.entities.Individual;
import ptp.ag.entities.Population;
import ptp.ag.entities.builder.BuilderBaseIndividual;
import ptp.ag.exceptions.ToManyRestrictionsException;
import ptp.entities.Product;
import ptp.properties.PropertiesBean;

@ManagedBean(name = "ag")
public class AG {

	private static final Integer ITERATIONS_FACTOR = 3;
	private static Integer maxCreateInitialPopulationIterations;
	private static Integer populationSize;

	private static Population population;
	private static List<Individual> parentsCrossover = new ArrayList<Individual>();
	private static List<Individual> parentsmutation = new ArrayList<Individual>();
	private static Individual baseIndividual;

	private static PropertiesBean propBean;

	public static Population runAG(List<Product> products, PropertiesBean propertiesBean)
			throws ToManyRestrictionsException {

		propBean = propertiesBean;
		populationSize = propertiesBean.getPopulationInitialSize();
		maxCreateInitialPopulationIterations = ITERATIONS_FACTOR * populationSize;

		baseIndividual = BuilderBaseIndividual.createBaseIndividual(products, propertiesBean); // OK

		createInitialPopulation(propertiesBean, products); // OK
		int actualGeneration = 0;
		do {
			fitnessEvaluation(propertiesBean); // OK
			selectParents(); // OK
			crossoverIndividuals(); // OK
			individualsMutation(); // OK
			fitnessEvaluation(propertiesBean); // OK
			selectNextGeneration();// ok

			actualGeneration++;
		} while (stopExecution(actualGeneration));

		removeDominated();

		return population;
	}

	private static boolean stopExecution(int actualGeneration) {
		return actualGeneration <= propBean.getGenerations();
	}

	private static void individualsMutation() {
		System.out.println("parentsmutation = " + parentsmutation.size());
		for (Individual ind : parentsmutation) {

			if (Math.random() <= propBean.getMutationRate()) {
				Individual i = (Individual) ind.clone();
				int newClass = (int) (Math.random() * ((double) i.getNumberOfPossibleClasses()));
				int mutationPoint = (int) (Math.random() * ((double) i.getGenes().size()));

				if (newClass < baseIndividual.getNumberOfPossibleClasses()) {
					i.getGenes().get(mutationPoint).setClas(newClass + 1);
				} else {
					i.getGenes().get(mutationPoint).setClas(newClass);
				}

				if (i.validate(propBean.getRestrictions()) && !existsInPopulation(i)) {
					population.addIndividual(i);
					//System.out.println("Houve mutação!!!");
				}
			}
		}
	}

	private static void crossoverIndividuals() {

		parentsmutation.clear();
		for (int k = 0; k < parentsCrossover.size(); k = k + 2) {

			Individual i = (Individual) parentsCrossover.get(k).clone();
			Individual i2 = (Individual) parentsCrossover.get(k + 1).clone();

			int crossoverPoint = (int) (Math.random() * ((double) i.getNumberOfPossibleClasses()));

			for (int l = crossoverPoint; l < i.getGenes().size(); l++) {
				i.getGenes().get(l).setClas(parentsCrossover.get(k + 1).getGenes().get(l).getClas());
				i2.getGenes().get(l).setClas(parentsCrossover.get(k).getGenes().get(l).getClas());
			}

			parentsmutation.add(i);
			parentsmutation.add(i2);
			if (i.validate(propBean.getRestrictions()) && !existsInPopulation(i)) {
				population.addIndividual(i);

			}

			if (i2.validate(propBean.getRestrictions()) && !existsInPopulation(i2)) {
				population.addIndividual(i2);
			}
		}
	}

	private static void selectParents() {

		parentsCrossover.clear();

		for (int i = 0; i < population.getPopulation().size(); i++) {
			if (Math.random() <= propBean.getCrossoverRate()) {
				parentsCrossover.add(population.getPopulation().get(i));
			}
		}
		if (parentsCrossover.size() % 2 != 0) {
			int index = (int) (Math.random() * (double) parentsCrossover.size());
			parentsCrossover.add(population.getPopulation().get(index));
		}

//		System.out.println("Selecionou TODOS!!! Size = " + parentsCrossover.size());

	}

	private static void fitnessEvaluation(PropertiesBean propertiesBean) {
		for (Individual ind : population.getPopulation()) {
			if (ind.getFitnessSpace().compareTo(0.0) == 0) {
				ind.calculateFitness(propertiesBean);
			}
		}
	}

	private static void selectNextGeneration() {
		// find dominated
		List<Individual> dominated = new ArrayList<Individual>();
		List<Individual> populationCopy = new ArrayList<Individual>();

		populationCopy.addAll(population.getPopulation());
		int count = 0;
		do {
			dominated.clear();
			for (int i = 0; i < populationCopy.size(); i++) {
				for (int j = 0; j < populationCopy.size(); j++) {
					if (populationCopy.get(j).getFitnessSpace() < populationCopy.get(i).getFitnessSpace()
							&& populationCopy.get(j).getFitnessTotalDistance() < populationCopy.get(i)
									.getFitnessTotalDistance()
							&& populationCopy.get(j).getFitnessTotalTime() < populationCopy.get(i)
									.getFitnessTotalTime()) {
						dominated.add(populationCopy.get(i));
						break;
					}
				}
			}
			count++;
			populationCopy.clear();
			populationCopy.addAll(dominated);
		} while (((population.getPopulation().size() - dominated.size()) < populationSize) && (count < 4));

		// remove dominateds
		for (Individual ind : dominated) {
			population.removeIndividual(ind);
		}

		if (population.getSize() > populationSize) {

			List<Individual> pOrderBySpace = population.getPopulationOrderBySpace();
			List<Individual> pOrderByTTime = population.getPopulationOrderByTotalTime();
			List<Individual> pOrderByTDistance = population.getPopulationOrderByTotalDistance();

			Double spaceMax = pOrderBySpace.get(0).getFitnessSpace();
			Double spaceMin = pOrderBySpace.get(pOrderBySpace.size() - 1).getFitnessSpace();
			pOrderBySpace.get(0).setCroudDistanceSpace(10000.0);
			pOrderBySpace.get(pOrderBySpace.size() - 1).setCroudDistanceSpace(10000.0);

			Double TTMax = pOrderByTTime.get(0).getFitnessTotalTime();
			Double TTMin = pOrderByTTime.get(pOrderByTTime.size() - 1).getFitnessTotalTime();
			pOrderByTTime.get(0).setCroudDistanceTT(10000.0);
			pOrderByTTime.get(pOrderByTTime.size() - 1).setCroudDistanceTT(10000.0);

			Double TDMax = pOrderByTDistance.get(0).getFitnessTotalDistance();
			Double TDMin = pOrderByTDistance.get(pOrderByTDistance.size() - 1).getFitnessTotalDistance();
			pOrderByTDistance.get(0).setCroudDistanceTD(10000.0);
			pOrderByTDistance.get(pOrderByTDistance.size() - 1).setCroudDistanceTD(10000.0);

			for (int i = 1; i < (population.getSize() - 1); i++) {
				Individual ind = pOrderBySpace.get(i);
				ind.setCroudDistanceSpace(
						(pOrderBySpace.get(i + 1).getFitnessSpace() - pOrderBySpace.get(i - 1).getFitnessSpace())
								/ (spaceMax - spaceMin));

				ind = pOrderByTTime.get(i);
				ind.setCroudDistanceTT((pOrderByTTime.get(i + 1).getFitnessTotalTime()
						- pOrderByTTime.get(i - 1).getFitnessTotalTime()) / (TTMax - TTMin));

				ind = pOrderByTDistance.get(i);
				ind.setCroudDistanceSpace((pOrderByTDistance.get(i + 1).getFitnessTotalDistance()
						- pOrderByTDistance.get(i - 1).getFitnessTotalDistance()) / (TDMax - TDMin));
			}

			List<Individual> toRemoveByCD = new ArrayList<Individual>();
			List<Individual> pOrderByCD = population.getPopulationOrderByCrouddistance();
			for (int i = population.getSize()-1; i < pOrderByCD.size(); i++) {
				toRemoveByCD.add(pOrderByCD.get(i));
			}

			for (Individual ind : toRemoveByCD) {
				population.removeIndividual(ind);
			}
		}
	}

	private static void createInitialPopulation(PropertiesBean propertiesBean, List<Product> products)
			throws ToManyRestrictionsException {
		population = new Population();
		Integer iterations = 0;

		if (baseIndividual.validate(propertiesBean.getRestrictions())) {
			population.addIndividual(baseIndividual);
		}

		do {
			Individual ind = new Individual();

			for (Gene g : baseIndividual.getGenes()) {
				int clas = (int) (Math.random() * ((double) baseIndividual.getNumberOfPossibleClasses()));
				Product prod = (Product) g.getProduct().clone();
				Gene newGene = null;
				if (clas < baseIndividual.getNumberOfPossibleClasses()) {
					newGene = new Gene(clas + 1, prod);
				} else {
					newGene = new Gene(clas, prod);
				}

				ind.addGene(newGene);
			}

			if (ind.validate(propertiesBean.getRestrictions())) {
				if (!existsInPopulation(ind)) {
					population.addIndividual(ind);
				}
			} else {
				iterations++;
			}

		} while ((population.getSize() < populationSize) && iterations <= maxCreateInitialPopulationIterations);

		if (population.getSize() < populationSize) {
			throw new ToManyRestrictionsException(
					"Could not possible to " + "generate population, due to restrictions!");
		}
	}

	private static boolean existsInPopulation(Individual ind) {
		boolean equals = false;
		for (Individual individual : population.getPopulation()) {
			if (ind.equals(individual)) {
				equals = true;
				break;
			}
		}

		return equals;
	}

	public static boolean validateStorage(PropertiesBean propertiesBean) {
		boolean ret = false;
		if (propertiesBean.getNa() * 2 == propertiesBean.getNx()) {
			ret = true;
		}

		return ret;
	}

	private static void removeDominated() {
		// find dominated
		List<Individual> dominated = new ArrayList<Individual>();
		for (int i = 0; i < population.getPopulation().size(); i++) {
			for (int j = 0; j < population.getPopulation().size(); j++) {
				if (population.getPopulation().get(j).getFitnessSpace() < population.getPopulation().get(i)
						.getFitnessSpace()
						&& population.getPopulation().get(j).getFitnessTotalDistance() < population.getPopulation()
								.get(i).getFitnessTotalDistance()
						&& population.getPopulation().get(j).getFitnessTotalTime() < population.getPopulation().get(i)
								.getFitnessTotalTime()) {
					dominated.add(population.getPopulation().get(i));
					break;
				}
			}
		}

		// remove dominateds
		for (Individual ind : dominated) {
			population.removeIndividual(ind);
		}
	}

}
