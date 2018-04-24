public class Cell{
	public int substitution_score;
	public int deletion_score;
	public int insertion_score;
	public int match_S;
	public int match_I;
	public int match_D;
	public int ali_S;
	public int ali_I;
	public int ali_D;

	public Cell(int substitution_score, int deletion_score, int insertion_score, int match_S, int match_I, int match_D, int ali_S,int ali_I, int ali_D){
		this.substitution_score = substitution_score;
		this.deletion_score = deletion_score;
		this.insertion_score = insertion_score;
		this.match_S = match_S;
		this.match_I = match_I;
		this.match_D = match_D;
		this.ali_S = ali_S;
		this.ali_I = ali_I;
		this.ali_D = ali_D;
	}
}
