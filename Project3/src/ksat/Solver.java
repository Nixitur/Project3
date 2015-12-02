package ksat;

import java.util.Arrays;
import java.util.Random;

public class Solver {
	private int[][] clauses;
	private int noOfVars;
	private Random rand;
	
	/**
	 * Instantiates a new Solver for a 3-KNF formula.
	 * @param clauses The clauses in the formula. Each array in <tt>clauses</tt> corresponds to one clause. A positive value in the clause
	 * corresponds to a non-negated literal, a negative value to a negated literal of the variable that is the value's absolute.
	 * @param noOfVars The number of variables in the formula.
	 */
	public Solver(int[][] clauses, int noOfVars){
		rand = new Random();
		this.clauses = clauses;
		this.noOfVars = noOfVars;
	}
	
	/**
	 * Attempts to solve the given formula with the Modified Random-3-SAT algorithm with an error probability of at most 10^(-13).
	 * @param print <tt>true</tt> to print out whether a satisfying interpretation has been found as well as the interpretation itself, 
	 * <tt>false</tt> to not do that. 
	 * @return <tt>true</tt> if a satisfying interpretation could be found, <tt>false</tt> otherwise.
	 */
	public boolean solve(boolean print){
		int restarts = getNoOfRestarts();
		boolean[] interp = modifiedRandomKSat(restarts);
		if (interp == null){
			if (print)
				System.out.println("No satisfying interpretation could be found.");
			return false;
		} else {
			if (print)
				System.out.println("A satisfying interpretation is "+Arrays.toString(interp));
			return true;
		}
	}
	
	/**
	 * Executes the Modified Random-3-SAT algorithm.
	 * @param r The number of restarts in Modified Random-3-SAT. 
	 * @return A satisfying interpretation of the formula if such a one is found; <tt>null</tt> otherwise.
	 */
	private boolean[] modifiedRandomKSat(int r){
		int l = 3 * noOfVars;
		for (int i = 0; i < r; i++){
			boolean[] interp = randomKSat(l);
			if (interp != null){
				return interp;
			}
		}
		return null;
	}
	
	/**
	 * Executes the Random-3-SAT algorithm.
	 * @param l The number of iterations of Random 3-SAT.
	 * @return A satisfying interpretation of the formula if such a one is found; <tt>null</tt> otherwise.
	 */
	private boolean[] randomKSat(int l){
		boolean[] interp = getRandomInterpretation();
		for (int i = 0; i < l; i++){
			int[] failedClause = satisfies(interp);
			if (failedClause == null){
				return interp;
			}
			flipRandomVar(failedClause, interp);
		}
		return null;
	}
	
	/**
	 * Generates a random interpretation.
	 * @return A random interpretation.
	 */
	private boolean[] getRandomInterpretation(){
		boolean[] interp = new boolean[noOfVars];
		for (int i = 0; i < noOfVars; i++){
			interp[i] = rand.nextBoolean();
		}
		return interp;
	}
	
	/**
	 * Tests if an interpretation satisfies the clauses.
	 * @param interp An interpretation.
	 * @return A clause that is not satisfied by the interpretation if this interpretation does not satisfy all clauses,
	 * <tt>null</tt> otherwise.
	 */
	private int[] satisfies(boolean[] interp){
		for (int[] clause : clauses){
			boolean sat = false;
			for (int i = 0; i < 3; i++){
				int lit = clause[i];
				int var = Math.abs(lit);
				boolean varTrue = interp[var-1];
				if ((varTrue && (lit > 0)) || (!varTrue && (lit < 0))){
					sat = true;
					break;
				}
			}
			if (!sat){
				return clause;
			}
		}
		return null;
	}
	
	/**
	 * From a given clause, chooses a random literal and flips the corresponding variable in the interpretation.
	 * @param clause The given clause.
	 * @param interp An interpretation of the formula.
	 */
	private void flipRandomVar(int[] clause, boolean[] interp){
		int var = Math.abs(clause[rand.nextInt(3)]);
		interp[var-1] = !interp[var-1];
	}
	
	/**
	 * Calculates the necessary number of restarts in Modified k-SAT for this formula.
	 * @return The necessary number of restarts in Modified k-SAT
	 */
	private int getNoOfRestarts(){
		double p = 0.5 * Math.pow(0.75, noOfVars);
		return (int) Math.ceil(30.0 / p);
	}
}
