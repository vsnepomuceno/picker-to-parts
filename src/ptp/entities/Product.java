package ptp.entities;

public class Product implements Cloneable{
	
	private Integer number;
	private String name;
	private Double space1;
	private Double space2;
	private Double space3;
	private Double space4;
	private Double popularity;
	private Double demand;
	private Double client;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Double getSpace1() {
		return space1;
	}

	public void setSpace1(Double space1) {
		this.space1 = space1;
	}

	public Double getSpace2() {
		return space2;
	}

	public void setSpace2(Double space2) {
		this.space2 = space2;
	}

	public Double getSpace3() {
		return space3;
	}

	public void setSpace3(Double space3) {
		this.space3 = space3;
	}

	public Double getSpace4() {
		return space4;
	}

	public void setSpace4(Double space4) {
		this.space4 = space4;
	}

	public Double getPopularity() {
		return popularity;
	}

	public void setPopularity(Double popularity) {
		this.popularity = popularity;
	}

	public Double getDemand() {
		return demand;
	}

	public void setDemand(Double demand) {
		this.demand = demand;
	}

	public Double getClient() {
		return client;
	}

	public void setClient(Double client) {
		this.client = client;
	}

	public boolean validateProduct(){
		boolean ret = true;
		
		if (this.number == null || this.name == null ||
				this.space1 == null || this.space2 == null ||
				this.space4 == null || this.space4 == null || 
				this.demand == null) {
			ret = false;
		}	
		
		return ret;
	}
	
	@Override
	public boolean equals(Object obj) {
		Product p = (Product) obj;
		return (p.number == this.number) && (p.name == this.name);
	}
	
	@Override
	public Object clone(){
		Product p = new Product();

		p.setNumber(number);
		p.setName(name);
		p.setSpace1(space1);
		p.setSpace2(space2);
		p.setSpace3(space3);
		p.setSpace4(space4);
		p.setPopularity(popularity);
		p.setDemand(demand);
		p.setClient(client);
		
		return p;
	}
	
	public double getMaxSpaceSize() {
		double ret = 0.0;
		
		if ((this.space1 > this.space2) && (this.space1 > this.space3) && 
				(this.space1 > this.space4)) {
			ret = this.space1;
		} else if ((this.space2 > this.space1) && (this.space2 > this.space3) && 
				(this.space2 > this.space4)) {
			ret = this.space2;
		} else if ((this.space3 > this.space1) && (this.space3 > this.space2) && 
				(this.space3 > this.space4)) {
			ret = this.space3;
		} else if ((this.space4 > this.space1) && (this.space4 > this.space2) && 
				(this.space4 > this.space3)) {
			ret = this.space4;
		}
		
		return ret;
	}
}
