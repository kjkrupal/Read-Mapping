import java.util.ArrayList;

public class Alignment{
  String s1;
  String s2;
  int match_score;
  int mismatch_score;
  int opening_gap_penalty;
  int extension_gap_penalty;
  int m;
  int n;
  char[] str1;
  char[] str2;
  Cell[][] T;
  String finalS1 = "";
  String finalS2 = "";
  int total_opening_gaps;
  int total_gaps;
  int total_matches;
  int total_mismatches;
  String s1_name;
  String s2_name;
  int optimum_score;
  float percent_identity;
  float percent_gap;

  public Alignment(String s1, String s2, String s1_name, String s2_name, int match_score, int mismatch_score, int opening_gap_penalty, int extension_gap_penalty){
    this.s1 = s1;
    this.s2 = s2;
    this.match_score = match_score;
    this.mismatch_score = mismatch_score;
    this.opening_gap_penalty = opening_gap_penalty;
    this.extension_gap_penalty = extension_gap_penalty;
    this.str1 = this.s1.toCharArray();
    this.str2 = this.s2.toCharArray();
    this.m = this.str1.length;
    this.n = this.str2.length;
    T = new Cell[m+1][n+1];
    this.s1_name = s1_name;
    this.s2_name = s2_name;
  }

  public ArrayList <Integer> localAlignment(Alignment alignment){
    T[0][0] = new Cell(0, 0, 0);

    for(int i = 1; i <= m; i++){
      T[i][0] = new Cell(0, 0, 0);
    }
    for(int j = 1; j <= n; j++){
      T[0][j] = new Cell(0, 0, 0);
    }

    for(int i = 1; i <= m; i++){
      for (int j = 1; j <= n; j++) {
        T[i][j] = new Cell(findMax(T[i-1][j-1].deletion_score,
                                   T[i-1][j-1].substitution_score,
                                   T[i-1][j-1].insertion_score,
                                   0) + S(str1[i-1], str2[j-1]),

                           findMax(T[i-1][j].deletion_score + extension_gap_penalty,
                                   T[i-1][j].substitution_score + opening_gap_penalty + extension_gap_penalty,
                                   T[i-1][j].insertion_score + opening_gap_penalty + extension_gap_penalty,
                                   0),

                           findMax(T[i][j-1].deletion_score + opening_gap_penalty + extension_gap_penalty,
                                   T[i][j-1].substitution_score + opening_gap_penalty + extension_gap_penalty,
                                   T[i][j-1].insertion_score + extension_gap_penalty,
                                   0));
      }
    }
    int[] position = new int[2];
    int value = -999999;
    for(int i = 0; i <= m; i++){
      for(int j = 0; j <= n; j++){
        if(findMax(T[i][j].substitution_score, T[i][j].deletion_score, T[i][j].insertion_score) >= value){
                value = findMax(T[i][j].substitution_score, T[i][j].deletion_score, T[i][j].insertion_score);
                position[0] = i;
                position[1] = j;
        }
      }
    }
    int maximum = 0;
    int i = position[0];
    int j = position[1];

    optimum_score = findMax(T[i][j].substitution_score, T[i][j].deletion_score, T[i][j].insertion_score);

    int current_value = findMax(T[i][j].substitution_score, T[i][j].deletion_score, T[i][j].insertion_score);

    char direction = '-';

    if(current_value == T[i][j].substitution_score) direction = 'S';
    if(current_value == T[i][j].deletion_score) direction = 'D';
    if(current_value == T[i][j].insertion_score) direction = 'I';

    while(i > 0 && j > 0 && current_value != 0){

      int temp_s;
      int temp_d;
      int temp_i;
      //System.out.println("hi");

      if(T[i][j].insertion_score == current_value && direction == 'I'){
        finalS1 = "-" + finalS1;
        finalS2 = str2[j-1] + finalS2;


        temp_s = T[i][j - 1].substitution_score + opening_gap_penalty + extension_gap_penalty;
        temp_d = T[i][j - 1].deletion_score + opening_gap_penalty + extension_gap_penalty;
        temp_i = T[i][j - 1].insertion_score + extension_gap_penalty;

        if(T[i][j].insertion_score == temp_s){
          current_value = T[i][j - 1].substitution_score;
          direction = 'S';
        }
        else if(T[i][j].insertion_score == temp_d){
          current_value = T[i][j - 1].deletion_score;
          direction = 'D';
        }
        else{
          current_value = T[i][j - 1].insertion_score;
          direction = 'I';
        }
        j--;

      }
      else if(T[i][j].deletion_score == current_value && direction == 'D'){
        finalS1 = str1[i-1] + finalS1;
        finalS2 = "-" + finalS2;


        temp_s = T[i - 1][j].substitution_score + opening_gap_penalty + extension_gap_penalty;
        temp_d = T[i - 1][j].deletion_score + extension_gap_penalty;
        temp_i = T[i - 1][j].insertion_score + opening_gap_penalty + extension_gap_penalty;

        if(T[i][j].deletion_score == temp_s){
          current_value = T[i - 1][j].substitution_score;
          direction = 'S';
        }
        else if(T[i][j].deletion_score == temp_d){
          current_value = T[i - 1][j].deletion_score;
          direction = 'D';
        }
        else{
          current_value = T[i - 1][j].insertion_score;
          direction = 'I';
        }
        i--;

      }
      else{
        finalS1 = str1[i-1] + finalS1;
        finalS2 = str2[j-1] + finalS2;


        temp_s = T[i - 1][j - 1].substitution_score + S(str1[i-1], str2[j-1]);
        temp_d = T[i - 1][j - 1].deletion_score + S(str1[i-1], str2[j-1]);
        temp_i = T[i - 1][j - 1].insertion_score + S(str1[i-1], str2[j-1]);

        if(T[i][j].substitution_score == temp_s){
          current_value = T[i - 1][j - 1].substitution_score;
          direction = 'S';
        }
        else if(T[i][j].substitution_score == temp_d){
          current_value = T[i - 1][j - 1].deletion_score;
          direction = 'D';
        }
        else{
          current_value = T[i - 1][j - 1].insertion_score;
          direction = 'I';
        }
        i--;
        j--;
      }

    }
    ArrayList <Integer> list = new ArrayList <Integer>();

    list = generateReport();

    return list;
  }

  public int S(char ai, char bj){
    return ai == bj ? match_score : mismatch_score;
  }

  public int findMax(int... numbers){

    int value = -999999;
    for(int i = 0; i < numbers.length; i++)
      if(numbers[i] >= value)
        value = numbers[i];
    return value;

  }

  public ArrayList<Integer> generateReport(){

    ArrayList <Integer> list = new ArrayList <Integer>();
    char[] alignedS1 = finalS1.toCharArray();
    char[] alignedS2 = finalS2.toCharArray();

    for(int i = 0; i < alignedS1.length; i++){

      if(alignedS1[i] == alignedS2[i])
        total_matches ++ ;

      if(alignedS1[i] == '-' || alignedS2[i] == '-')
        total_gaps ++;

    }

    total_mismatches = alignedS1.length - total_matches - total_gaps;

    list.add(total_matches);
    list.add(alignedS1.length);

    return list;

  }

}
