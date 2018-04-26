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
  int start_leaf;
  int end_leaf;

  public Node(int id, Node parent, int start, int end, TreeMap<Character,Node> children, int depth, Node suffix_link, int start_leaf, int end_leaf){
    this.suffix_link = suffix_link;
    this.id = id;
    this.parent = parent;
    this.start = start;
    this.end = end;
    this.children = children;
    this.depth = depth;
    count++;
  }

  public int nodeCount(){
    return count;
  }


}
