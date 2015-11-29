package ksat;

public class Solver {
	private int[][] clauses;
	private boolean[] interp;
	public Solver(int[][] clauses, int noOfVars){
		this.clauses = clauses;
		System.out.println(satisfies());
		interp = new boolean[noOfVars];
	}
	
	public boolean satisfies(){
		for (int[] clause : clauses){
			boolean sat = false;
			for (int i = 0; i < 3; i++){
				int lit = clause[i];
				int var = Math.abs(lit);
				if (((interp[var-1]) && (lit > 0)) || ((!interp[var-1]) && (lit < 0))){
					sat = true;
					break;
				}
			}
			if (!sat){
				return false;
			}
		}
		return true;
	}
}
