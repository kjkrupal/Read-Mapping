public class Cell{
	public int substitution_score;
	public int deletion_score;
	public int insertion_score;

	public Cell(int substitution_score, int deletion_score, int insertion_score){
		this.substitution_score = substitution_score;
		this.deletion_score = deletion_score;
		this.insertion_score = insertion_score;
	}
}
