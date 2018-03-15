package ptp.properties;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;

import ptp.entities.Restriction;

@ManagedBean(name = "propertiesBean")
public class PropertiesBean {

	private Integer threshold;
	private Integer index;
	
	private Integer nx;
	private Integer na;
	private Integer nz;
	private Double wp;
	private Double wa;
	private Double hp;
	private Double lp;
	
	private int generations = 20;
	private double mutationRate = 0.1; // 10%
	private double crossoverRate = 0.2; // 20%
	private int populationInitialSize = 15;
	

	private List<Restriction> restrictions = new ArrayList<Restriction>();

	public Integer getThreshold() {
		return threshold;
	}

	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public List<Restriction> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(List<Restriction> restrictions) {
		this.restrictions = restrictions;
	}

	public Integer getNx() {
		return nx;
	}

	public void setNx(Integer nx) {
		this.nx = nx;
	}

	public Integer getNa() {
		return na;
	}

	public void setNa(Integer na) {
		this.na = na;
	}

	public Double getWp() {
		return wp;
	}

	public void setWp(Double wp) {
		this.wp = wp;
	}

	public Double getWa() {
		return wa;
	}

	public void setWa(Double wa) {
		this.wa = wa;
	}

	public Double getHp() {
		return hp;
	}

	public void setHp(Double hp) {
		this.hp = hp;
	}

	public Double getLp() {
		return lp;
	}

	public void setLp(Double lp) {
		this.lp = lp;
	}

	public Integer getNz() {
		return nz;
	}

	public void setNz(Integer nz) {
		this.nz = nz;
	}

	public Double getRz() {
		return 0.2;
	}

	public double getRxy() {
		return 0.5;
	}

	public int getGenerations() {
		return generations;
	}

	public void setGenerations(int generations) {
		this.generations = generations;
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}

	public double getCrossoverRate() {
		return crossoverRate;
	}

	public void setCrossoverRate(double crossoverRate) {
		this.crossoverRate = crossoverRate;
	}

	public int getPopulationInitialSize() {
		return populationInitialSize;
	}

	public void setPopulationInitialSize(int populationInitialSize) {
		this.populationInitialSize = populationInitialSize;
	}

}
