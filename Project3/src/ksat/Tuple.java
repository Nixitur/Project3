package ksat;

public class Tuple { 
	public int mk;
	public double avgTime;
	public boolean outOfTime;
	
	/**
	 * Generates a new Tuple with the starting values mk=0, avgTime=0, outOfTime=false.
	 */
	public Tuple(){
		this.mk = 0;
		this.avgTime = 0;
		this.outOfTime = false;
	}
	
	/**
	 * Generates a String representation of this Tuple
	 * @return mk, avgTime and outOfTime in a comma-delimited array.
	 */
	public String toString(){
		return "["+mk+", "+avgTime+"]";
	}
} 