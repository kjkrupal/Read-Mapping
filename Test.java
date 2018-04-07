import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;

class Test{
  public static void main(String[] args) throws FileNotFoundException{
try{
    Scanner scan_file = new Scanner(new File(args[0]));
    HashMap<String, String> reads = new HashMap<String, String>();

    while(scan_file.hasNext()){
      String line = scan_file.nextLine().toString();

      if(line.charAt(0) == '>'){
        System.out.println(line);
        reads.put(line.substring(1), scan_file.nextLine().toString());
      }
    }

    System.out.println(reads);
  }

  catch(Exception e){
    e.printStackTrace();
  }
}
}
