package ksat;

public class Tuple { 
	public int mk;
	public double avgTime;
	public double percentage;
	
	/**
	 * Generates a new Tuple with the starting values mk=0, avgTime=0, percentage=100.0.
	 */
	public Tuple(){
		this.mk = 0;
		this.avgTime = 0;
		this.percentage = 100.0;
	}
	
	/**
	 * Generates a String representation of this Tuple
	 * @return mk, avgTime and percentage in a comma-delimited array.
	 */
	public String toString(){
		return "["+mk+", "+avgTime+", "+percentage+"]";
	}
} 