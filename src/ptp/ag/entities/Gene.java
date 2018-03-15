package ptp.ag.entities;

import ptp.entities.Product;

public class Gene implements Cloneable{

	private Integer clas;
	private Product product;

	public Gene(Integer clas, Product prod) {
		this.clas = clas;
		this.product = prod;
	}

	public Integer getClas() {
		return this.clas;
	}

	public Product getProduct() {
		return product;
	}
	
	public void setClas(Integer clas) {
		this.clas = clas;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equals = true;
		Gene g = (Gene) obj;
		if (this.product.getName().equals(g.product.getName())) {
			if (this.clas != g.clas ) {
				equals = false;
			}
		} else {
			equals = false;
		}

		return equals;
	}
	
	@Override
	public Object clone(){
		Gene g = new Gene(this.clas, this.product);		
		return g;
	}
}
