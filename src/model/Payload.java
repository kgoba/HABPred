package model;

public class Payload {
	public double mass;
	public String name;
	
	public Payload(String name, double mass) {
		this.name = name;
		this.mass = mass;
	}
	
	public double getMass() {
		return mass;
	}
}
