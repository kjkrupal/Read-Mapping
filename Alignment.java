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

  public void globalAlignment(Alignment alignment){

    T[0][0] = new Cell(0, -999999,  -999999);

    for(int i = 1; i <= m; i++){
      T[i][0] = new Cell(-999999, opening_gap_penalty + i * extension_gap_penalty, -999999);
    }
    for(int j = 1; j <= n; j++){
      T[0][j] = new Cell(-999999, -999999, opening_gap_penalty + j * extension_gap_penalty);
    }

    for(int i = 1; i <= m; i++){
      for (int j = 1; j <= n; j++) {
        T[i][j] = new Cell(findMax(T[i-1][j-1].deletion_score,
                                   T[i-1][j-1].substitution_score,
                                   T[i-1][j-1].insertion_score) + S(str1[i-1], str2[j-1]),

                           findMax(T[i-1][j].deletion_score + extension_gap_penalty,
                                   T[i-1][j].substitution_score + opening_gap_penalty + extension_gap_penalty,
                                   T[i-1][j].insertion_score + opening_gap_penalty + extension_gap_penalty),

                           findMax(T[i][j-1].deletion_score + opening_gap_penalty + extension_gap_penalty,
                                   T[i][j-1].substitution_score + opening_gap_penalty + extension_gap_penalty,
                                   T[i][j-1].insertion_score + extension_gap_penalty));
      }
    }
    optimum_score = findMax(T[m][n].substitution_score, T[m][n].deletion_score, T[m][n].insertion_score);

    int maximum;
    int i = m;
    int j = n;

    while(i != 0 && j != 0){
      maximum = findMax(T[i][j].substitution_score, T[i][j].deletion_score, T[i][j].insertion_score);

      if(maximum == (findMax(T[i][j-1].deletion_score + opening_gap_penalty + extension_gap_penalty,
                             T[i][j-1].substitution_score + opening_gap_penalty + extension_gap_penalty,
                             T[i][j-1].insertion_score + extension_gap_penalty))){
        finalS1 = "-" + finalS1;
        finalS2 = str2[j-1] + finalS2;
        j--;
      }
      else if(maximum == (findMax(T[i-1][j].deletion_score + extension_gap_penalty,
                             T[i-1][j].substitution_score + opening_gap_penalty + extension_gap_penalty,
                             T[i-1][j].insertion_score + opening_gap_penalty + extension_gap_penalty))){
        finalS1 = str1[i-1] + finalS1;
        finalS2 = "-" + finalS2;
        i--;
      }
      else if(maximum == (findMax(T[i-1][j-1].deletion_score,
                             T[i-1][j-1].substitution_score,
                             T[i-1][j-1].insertion_score) + S(str1[i-1], str2[j-1]))){
        finalS1 = str1[i-1] + finalS1;
        finalS2 = str2[j-1] + finalS2;
        i--;
        j--;
      }

    }


    generateReport();
  }

  public void localAlignment(Alignment alignment){
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
    while(true){
      maximum = findMax(T[i][j].substitution_score, T[i][j].deletion_score, T[i][j].insertion_score);
      if(findMax(T[i][j].substitution_score, T[i][j].deletion_score, T[i][j].insertion_score) == 0)
        break;

        if(maximum == (findMax(T[i][j-1].deletion_score + opening_gap_penalty + extension_gap_penalty,
                               T[i][j-1].substitution_score + opening_gap_penalty + extension_gap_penalty,
                               T[i][j-1].insertion_score + extension_gap_penalty))){
          finalS1 = "-" + finalS1;
          finalS2 = str2[j-1] + finalS2;
          j--;
        }
      else if(maximum == (findMax(T[i-1][j].deletion_score + extension_gap_penalty,
                             T[i-1][j].substitution_score + opening_gap_penalty + extension_gap_penalty,
                             T[i-1][j].insertion_score + opening_gap_penalty + extension_gap_penalty))){
        finalS1 = str1[i-1] + finalS1;
        finalS2 = "-" + finalS2;
        i--;
      }
      else if(maximum == (findMax(T[i-1][j-1].deletion_score,
                             T[i-1][j-1].substitution_score,
                             T[i-1][j-1].insertion_score) + S(str1[i-1], str2[j-1]))){
        finalS1 = str1[i-1] + finalS1;
        finalS2 = str2[j-1] + finalS2;
        i--;
        j--;
      }

    }
    i = position[0];
    j = position[1];
    optimum_score = findMax(T[i][j].substitution_score, T[i][j].deletion_score, T[i][j].insertion_score);
    generateReport();
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
  public void generateReport(){

    System.out.println("\n\nScores: \t match = " + match_score + ", mismatch = "
                        + mismatch_score + ", h = " + opening_gap_penalty + ", g = " + extension_gap_penalty);
    System.out.println("\nSequence 1 = " + s1_name + ", Length = " + m + " characters"
                        + "\nSequence 2 = " + s2_name + ", Length = " + n + " characters\n");

    char[] alignedS1 = finalS1.toCharArray();
    char[] alignedS2 = finalS2.toCharArray();

    int rows = alignedS1.length%60 == 0 ? alignedS1.length/60 : alignedS1.length/60 + 1;

    for(int i = 0; i < rows; i++){
      //System.out.print("s1\t" + ((i * 60) + 1) + "\t");
      System.out.print("s1\t");

      for(int j = i * 60; j < 60 * (i+1); j++){
        try{System.out.print(alignedS1[j]);}catch(Exception e){break;}
      }
      /*if(i == rows - 1)
        System.out.print("\t" + alignedS1.length);
      else
        System.out.print("\t" + (60 * (i+1)));
        */
      System.out.println();
      System.out.print("\t");

      for(int j = i * 60; j < 60 * (i+1); j++){
        try{
          if(alignedS1[j] == alignedS2[j]){
            total_matches ++ ;
            System.out.print("|");
          }
          else
            System.out.print(" ");
        }catch(Exception e){break;}

      }
      System.out.println();
      //System.out.print("s2\t" + ((i * 60) + 1) + "\t");
      System.out.print("s2\t" );

      for(int j = i * 60; j < 60 * (i+1); j++){
        try{System.out.print(alignedS2[j]);}catch(Exception e){break;}
      }

      /*if(i == rows - 1)
        System.out.print("\t" + alignedS2.length);
      else
        System.out.print("\t" + (60 * (i+1)));
        */
      System.out.println("\n");

    }
    for(int i = 0; i < alignedS1.length; i++){
      if(alignedS1[i] == '-'){
        total_gaps ++;
        if(alignedS1[i-1] != '-'){
          total_opening_gaps ++;
        }
      }
    }
    for(int i = 0; i < alignedS2.length; i++){
      if(alignedS2[i] == '-'){
        total_gaps ++;
        if(alignedS2[i-1] != '-'){
          total_opening_gaps ++;
        }
      }
    }
    total_mismatches = alignedS1.length - total_matches - total_gaps;
    percent_identity = ((float)total_matches/alignedS1.length) * 100;
    percent_gap = ((float)total_gaps/alignedS1.length) * 100;

    System.out.println("\nReport:\n\n");
    System.out.println("Optimal score: " + optimum_score + "\n");
    System.out.println("Number of: matches = " + total_matches + ", mismatches = " + total_mismatches +
                        ", gaps = " + total_gaps + ", opening gaps = " + total_opening_gaps);
    System.out.println("\nIdentities = " + total_matches + "/" + alignedS1.length + "(" + percent_identity + "%), " +
                        "Gaps = " + total_gaps + "/" + alignedS1.length + "(" + percent_gap + "%)\n");

  }
}
