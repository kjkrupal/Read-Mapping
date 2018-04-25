import java.util.ArrayList;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ReadMapping{

  String sequence_name, sequence;
  Node first;
  int start, end, max, n, leaf_id, node_id, alphabet_length;
  char[] string, alphabet;
  ArrayList <Integer> index = new ArrayList<Integer>();
  ArrayList <Object> list;
  LinkedHashMap<String, String> reads;
  int A[];
  int nextIndex;
  Node deepestNode;
  int align_position;
  int count;
  int read_ptr;
  PrintWriter writer;

  public ReadMapping(String sequence, String sequence_name, LinkedHashMap<String, String> reads, char[] alphabet){
    writer = new PrintWriter("the-file-name.txt", "UTF-8");
    this.sequence_name = sequence_name;
    this.sequence = sequence + "$";
    this.n = this.sequence.length();
    this.string = this.sequence.toCharArray();
    this.alphabet = alphabet;
    this.alphabet_length = alphabet.length;
    this.reads = reads;
    A = new int[n];
  }

  public void begin(){

    //Create suffix tree for reference genome
    Node root = createSuffixTree();

    DFS_PrepareST(root, A);

    mapReads(root);

  }

  public void mapReads(Node root){
    try{
    ArrayList list;
    //For all reads
    for(String key : reads.keySet()){

      HashMap <Integer, Node> map = new HashMap <Integer, Node> ();

      deepestNode = null;
      read_ptr = -1;

      //Get a single read
      String read = reads.get(key);

      int i = 0;

      //This while loop for every suffix of a particular read
      while(i < read.length() - 25){

        //FindLoc returns a node for current read
        list = findLoc(read.substring(i), root);

        Node temp = (Node) list.get(0);
        int count = (Integer) list.get(1);

        map.put(count, temp);
        //Increment i for next suffix of current read
        i++;
      }
      //System.out.println(map);

      int val = -1;

      for(int k : map.keySet()){
        if(k > val){
          val = k;
        }
      }
      //System.out.println("Val: " + val);
      deepestNode = map.get(val);
      //System.out.println("Deepest node depth: " + deepestNode.depth);
      //If the matches were never greater than 25, deepestNode would be null
      map = null;
      if(val >= 25){

        HashMap <Double, Integer> hits = new HashMap <Double, Integer> ();
        //For all candidate position
        for(int index = deepestNode.start_leaf; index <= deepestNode.end_leaf; index++){

          double identity = 0.0;
          double length_coverage = 0.0;
          int candidate = -1;
          //System.out.println(A[index]);

          ArrayList<Integer> values;
          int i_1 = A[index] - 1;
          int i_2 = A[index];

          int start = i_1 - read.length();
          int end = i_2 + read.length();

          if(start < 0)
            start = 0;

          if(end > n - 1)
            end = n - 1;

          values = localAlignment(sequence.substring(start, end), read);

          Double v1 = (double)((Integer)values.get(0)).intValue();
          Double v2 = (double)((Integer)values.get(1)).intValue();

          identity = v1 / v2 * 100;

          length_coverage = ((Integer) values.get(1) / read.length()) * 100;

          //System.out.println("Matches: " + (Integer) values.get(0) + " Aligned Length: " + (Integer) values.get(1) + "Read length: " + read.length());
          //System.out.println("Identity: " + identity + " Length coverage: " + length_coverage);
          if(identity >= 90.0 && length_coverage >= 80.0){

            hits.put(length_coverage, A[index] - 1);

          }

        }

        double cover = 0.0;

        for(double d : hits.keySet()){
          if(d >= cover){
            cover = d;
          }
        }

        System.out.println("Hit at: " + hits.get(cover));
        hits = null;
      }
      else{
        //No alignment required: Miss
        System.out.println("No hit");
      }


    }}
    catch(Exception e){
      e.printStackTrace();
    }

  }

  public ArrayList <Integer> localAlignment(String reference, String read){

    ArrayList <Integer> list = new ArrayList <Integer>();

    int align_position = 0;


    int match_score = 1;
    int mismatch_score = -2;
    int extension_gap_penalty = -1;
    int opening_gap_penalty = -5;

    Alignment alignment = new Alignment(reference, read, "reference", "read", match_score, mismatch_score, opening_gap_penalty, extension_gap_penalty);

    list = alignment.localAlignment(alignment);

    return list;


  }

  public ArrayList findLoc(String read, Node node){

    ArrayList list = new ArrayList();
    int read_pointer = 0;
    int count = 0;
    boolean mismatch = false;
    Node current = null;

    while(read_pointer < read.length()){

      if(node.children.containsKey(read.charAt(read_pointer))){

        current = node.children.get(read.charAt(read_pointer));

        int start = current.start;
        int end = current.end;

        while(start <= end){
          try{
            if(read.charAt(read_pointer) == string[start]){

              count++;
              start++;
              read_pointer++;
            }
            else{
              mismatch = true;
              break;
            }
          }
          catch(Exception e){
            break;
          }
        }

        if(mismatch){
          break;
        }

      }
      else{
        break;
      }
      node = current;
    }
    list.add(current);
    list.add(count);

    return list;
  }

  public void DFS_PrepareST(Node node, int A[]){

    if(node == null)
      return;

    if(node.children == null){

      A[nextIndex] = node.id;

      if(node.depth >= 25){
        node.start_leaf = nextIndex;
        node.end_leaf = nextIndex;
      }
      nextIndex++;
      return;
    }
    else{
      for(char ch : node.children.keySet()){
        DFS_PrepareST(node.children.get(ch), A);
      }
    }

    if(node.depth >= 25){
      char first_key = (Character) node.children.firstKey();
      char last_key = (Character) node.children.lastKey();
      Node u_left = node.children.get(first_key);
      Node u_right = node.children.get(last_key);
      node.start_leaf = u_left.start_leaf;
      node.end_leaf = u_right.end_leaf;
    }
  }

  public Node createSuffixTree(){
    //Initialize the starting index
    int i = 0;

    //Initialize root node
    Node root = new Node(node_id, null, -1, -1, null, 0, null, -1, -1);

    //Set the suffix link of root to root itself
    root.suffix_link = root;

    //Initialize the previous suffix to root
    Node previous_suffix = root;

    while(i != n){
      //The leaf id will denote the suffix number which will be used for BWT indexing
      leaf_id ++;
      //System.out.println(leaf_id);
      //System.out.println("Inserting suffix: " + sequence.substring(i));

      //List will contain Node v and index from which suffix needs to be inserted
      list = generalizedSuffixLink(previous_suffix, i);

      //System.out.println((Node)list.get(0) + " " + (Integer)list.get(1));
      previous_suffix = findPath((Node)list.get(0), (Integer)list.get(1));
      //System.out.println("previous_suffix: " + previous_suffix);
      //System.out.println("Suffix " + sequence.substring(i) + " inserted");
      //System.out.println("\n");
      i++;
    }

    return root;

  }

  public ArrayList generalizedSuffixLink(Node previous_suffix, int i){
    ArrayList pack = new ArrayList();
    Node v = null;
    int index = 0;
    try{
      //Inital case when 1st suffix is being inserted
      if(previous_suffix.parent == null){

        v = previous_suffix;
        index = i;
      }
      else{
        //Parent exists
        Node u = previous_suffix.parent;

        //Case 1
        if(u.suffix_link != null){
          //Case 1A
          if(u.parent != null){
            //System.out.println("Case 1A");

            int alpha = u.depth - 1;
            index = i + alpha;
            v = u.suffix_link;
          }
          //Case 1B
          else{
            //System.out.println("Case 1B");
            v = u.suffix_link;
            index = i;

          }
        }
        //Case 2
        else{
          Node u_prime = u.parent;

          //Case 2A
          if(u_prime.parent != null){
            //System.out.println("Case 2A");

            int alpha_prime = u_prime.depth - 1;
            Node v_prime = u_prime.suffix_link;
            String beta = sequence.substring(u.start, u.end + 1);

            v = nodeHop(v_prime, i + alpha_prime, beta);

            u.suffix_link = v;

            index = i + v.depth;
          }
          //Case 2B
          else{
            //System.out.println("Case 2B");

            Node v_prime = u_prime.suffix_link;

            if(u.depth == 1){
              v = u_prime;
            }
            else{
              String beta = sequence.substring(u.start + 1, u.end + 1);

              v = nodeHop(v_prime, i, beta);
            }
            u.suffix_link = v;

            index = i + v.depth;

          }
        }
      }

    }
    catch(Exception e){
      e.printStackTrace();
    }

    pack.add(v);
    pack.add(index);
    return pack;
  }

  public Node findPath(Node v, int i){

    //Preserve the current index i in id
    int id = i;

    //The following block executes when the v node doesn't have any children
    if(v.children == null){

      //Since no children create a new child
      v.children = new TreeMap<Character, Node>();

      //Increment node id
      //node_id++;

      //Put key-value pair in the newly created TreeMap
      v.children.put(string[i], new Node(leaf_id, v, i, n - 1, null, v.depth + (n - i), null, -1, -1));

      //Return suffix node
      return v.children.get(string[i]);
    }

    /* The following block executes when the v node has a child but
    doesn't have a child corresponding to current character in the string */

    else if(v.children.get(string[i]) == null){

      //Increment node id
      //node_id++;

      //Add a new child in the existing TreeMap
      v.children.put(string[i], new Node(leaf_id, v, i, n - 1, null, v.depth + (n - i), null, -1, -1));

      //Return suffix node
      return v.children.get(string[i]);
    }
    //The following block executes if the v node has a child corresponding to the character
    else{
      //Get the child node from the parent corresponding to character
      Node child = v.children.get(string[i]);

      //Store child node's start and end index into temporary variables
      int start = child.start;
      int end = child.end;
      //Begin match-mismatch process
      while(start != end + 1){
        //If match then continue matching until mismatch
        if(string[start] == string[i]){
          start++;
          i++;
        }
        //If mismatch then create a two new nodes
        else{
          /*Since now we have to create an internal node between the v node and it's child,
          the following line will get the refernce of v node's child so that it can be used to
          update the values v node's child according to the newly added internal node */
          Node child_1 = v.children.get(string[id]);
          //System.out.println("Yaha ghus with id " + id + " and i " + i);
          //Increment node id
          node_id++;

          //The following line creates a new internal node
          v.children.put(string[id], new Node(node_id, v, child.start, start - 1, null, v.depth + (start - child.start), null, -1, -1));

          //Get reference of newly created internal node
          Node new_internal_node = v.children.get(string[id]);

          //Update child_1's parent
          child_1.parent = new_internal_node;

          //Create a new TreeMap of children for internal node
          new_internal_node.children = new TreeMap<Character, Node>();

          //Put child_1 reference as child into newly created TreeMap
          new_internal_node.children.put(string[start], child_1);

          //Update the starting index of child_1
          child_1.start = start;

          //Increment node id
          //node_id++;

          //The following line creates a new leaf node
          new_internal_node.children.put(string[i], new Node(leaf_id, child_1.parent, i, n - 1, null, child_1.parent.depth + (n - i), null, -1, -1));

          //Return the suffix node
          return new_internal_node.children.get(string[i]);

        }

      }
      /* The follwing will execute if the string is exhausted on the internal node. There are two cases that are possible
      1. There will be a child for the next character so go to that child node and repeat findPath process
      2. There will not be any child for the next character so simply create a child and finish */

      //The following will execute if there is no child with next character
      if(child.children.get(string[i]) == null){

        //Increment node id
        //node_id++;

        //Create a new child of v node with string starting from next character
        child.children.put(string[i], new Node(leaf_id, v, i, n - 1, null, v.depth + (n - i), null, -1, -1));

        //Return the suffix node
        return child.children.get(string[i]);
      }
      //The following will execute if there is a child with next character
      else{

        //Recursively call the findPath function with updated parent and starting position
        return findPath(v.children.get(string[id]), i);
      }
    }
  }

  public Node nodeHop(Node v_prime, int i, String beta){

    Node temp = v_prime.children.get(beta.charAt(0));

    int temp_string_length = temp.end - temp.start + 1;

    if(temp_string_length < beta.length()){
      //Hop to another node
      return nodeHop(temp, i, beta.substring(temp_string_length));
    }
    else if(temp_string_length == beta.length()){
      //Since length matches, temp node is v
      return temp;
    }
    else{
      node_id++;
      //Create v
      v_prime.children.put(string[temp.start], new Node(node_id, v_prime, temp.start, (beta.length() + temp.start - 1),
                                                null, v_prime.depth + (beta.length() + temp.start - temp.start), null, -1, -1));
      //Get node v
      Node v = v_prime.children.get(string[temp.start]);

      //Update temp node's starting index
      temp.start = beta.length() + temp.start;

      //Set parent of temp node to v
      temp.parent = v;

      //Create children for v and put temp in it
      v.children = new TreeMap<Character, Node>();

      v.children.put(string[temp.start], temp);

      return v;
    }
  }

}
