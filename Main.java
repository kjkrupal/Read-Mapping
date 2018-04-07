import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;

public class Main{
  public static void main(String[] args) {
    try{

      if(args.length != 2)
        throw new InputArgumentException("Specify proper sequence file path and alphabet file path in order");

      String sequence_file_name = args[0];
      if(!new File(sequence_file_name).exists())
        throw new InputArgumentException("Specify proper file name and path for sequence file");

      String alphabet_file_name = args[1];
      if(!new File(alphabet_file_name).exists())
        throw new InputArgumentException("Specify proper file name and path for Alphabet file");

      @SuppressWarnings("unchecked")
      ArrayList<ArrayList> list = readSequenceFile(sequence_file_name);

      char[] alphabet = readAlphabetFile(alphabet_file_name);

      SuffixTreeConstruction tree = new SuffixTreeConstruction((String)list.get(0).get(0), (String)list.get(1).get(0), alphabet);

      tree.createSuffixTree();
    }
    catch(Exception exception){
      System.out.println(exception);
    }
  }

  static ArrayList<ArrayList> readSequenceFile(String file_name) throws FileNotFoundException {
    Scanner scan_file = new Scanner(new File(file_name));
    ArrayList<String> key_list = new ArrayList<String>();
    ArrayList<String> sequence_list = new ArrayList<String>();
    ArrayList<ArrayList> list = new ArrayList<ArrayList>();
    String sequence_name = "";
    String sequence = "";

    while(scan_file.hasNext()){
      String line = scan_file.nextLine().toString();
      if(line.contains(">")){
        if(!sequence.equals(""))
          sequence_list.add(sequence);
        sequence_name = line.substring(1);
        key_list.add(sequence_name);
        sequence = "";
      }
      else{
        sequence = sequence + line;
      }
    }
    sequence_list.add(sequence);
    list.add(sequence_list);
    list.add(key_list);
    return list;
  }

  static char[] readAlphabetFile(String file_name) throws FileNotFoundException{
    Scanner scan_file = new Scanner(new File(file_name));

    String line = scan_file.nextLine().toString();
    String[] string = line.split("\\s");
    char[] alphabet = new char[string.length];

    for(int i = 0; i < string.length; i++){
      alphabet[i] = string[i].charAt(0);
    }
    return alphabet;
  }
}
