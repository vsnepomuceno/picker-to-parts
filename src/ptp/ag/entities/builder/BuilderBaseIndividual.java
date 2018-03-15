package ptp.ag.entities.builder;

import java.util.List;

import ptp.ag.entities.Gene;
import ptp.ag.entities.Individual;
import ptp.entities.Product;
import ptp.properties.PropertiesBean;

public class BuilderBaseIndividual {

	public static Individual createBaseIndividual(List<Product> products, PropertiesBean propertiesBean) {
		Individual ind = new Individual();
		int productClass = 1;
		for (Product p : products) {			
			Gene g = new Gene(productClass, p);
			ind.addGene(g);
			productClass++;
		}

		return ind;
	}

}
