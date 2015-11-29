package ksat;

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
	 * @return <tt>true</tt> if a satisfying interpretation could be found, <tt>false</tt> otherwise.
	 */
	public boolean solve(){
		int restarts = getNoOfRestarts();
		Long interp = modifiedRandomKSat(restarts);
		if (interp == null){
			System.out.println("No satisfying interpretation could be found.");
			return false;
		} else {
			System.out.println("A satisfying interpretation is "+interpretationToString(interp));
			return true;
		}
	}
	
	/**
	 * Executes the Modified Random-3-SAT algorithm.
	 * @param r The number of restarts in Modified Random-3-SAT. 
	 * @return A satisfying interpretation of the formula if such a one is found; <tt>null</tt> otherwise.
	 */
	private Long modifiedRandomKSat(int r){
		int l = 3 * noOfVars;
		for (int i = 0; i < r; i++){
			Long interp = randomKSat(l);
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
	private Long randomKSat(int l){
		long interp = getRandomInterpretation();
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
	private long getRandomInterpretation(){
		return rand.nextLong();
	}
	
	/**
	 * Tests if an interpretation satisfies the clauses.
	 * @param interp An interpretation.
	 * @return A clause that is not satisfied by the interpretation if this interpretation does not satisfy all clauses,
	 * <tt>null</tt> otherwise.
	 */
	private int[] satisfies(long interp){
		for (int[] clause : clauses){
			boolean sat = false;
			for (int i = 0; i < 3; i++){
				int lit = clause[i];
				int var = Math.abs(lit);
				boolean varTrue = (((1L << (var-1)) & interp) != 0);
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
	 * @return The interpretation with a bit flipped that corresponds to a literal in <tt>clause</tt>.
	 */
	private long flipRandomVar(int[] clause, long interp){
		int var = Math.abs(clause[rand.nextInt(3)]);
		return interp ^ (1L << (var-1));
	}
	
	/**
	 * Calculates the necessary number of restarts in Modified k-SAT for this formula.
	 * @return The necessary number of restarts in Modified k-SAT
	 */
	private int getNoOfRestarts(){
		double p = 0.5 * Math.pow(0.75, noOfVars);
		return (int) Math.ceil(30.0 / p);
	}
	
	/**
	 * Generates a String representation of an interpretation.
	 * @param interp The <tt>long</tt> representation of an interpretation of the formula.
	 * @return A string starting with the character '[' and ending with ']'. In between those is a comma-delimited list of boolean values, 
	 * one for each variable. If the ith-lowest bit of the interpretation is a 1, then the ith list entry is "true", otherwise "false".
	 */
	private String interpretationToString(long interp){
		String result = "[";
		for (int i = 0; i < noOfVars; i++){
			boolean varTrue = (((1L << i) & interp) != 0);
			if (varTrue){
				result += "true, ";
			} else {
				result += "false, ";
			}
		}
		result = result.substring(0,result.length()-2)+"]";
		return result;
	}
}
