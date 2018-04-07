import java.util.TreeMap;

public class Node{
  int id;
  int leaf_id;
  Node suffix_link;
  Node parent;
  int start;
  int end;
  TreeMap<Character, Node> children;
  int depth;
  static int count;

  public Node(int id, Node parent, int start, int end, TreeMap<Character,Node> children, int depth, Node suffix_link){
    this.suffix_link = suffix_link;
    this.id = id;
    this.parent = parent;
    this.start = start;
    this.end = end;
    this.children = children;
    this.depth = depth;
    count++;
  }

  public Node(){
    System.out.println(count);
  }


}
