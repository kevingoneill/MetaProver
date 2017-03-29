package gui2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.OptionalDouble;

/**
 * Created by kevin on 3/25/17.
 */
public class TreeLayout {

  public static int BUFFER = 25;

  private NodePanel root;
  private ArrayList<NodePanel> inorderTraversal;    // a list of all NodePanels sorted by inorder rank
  private HashMap<Integer, ArrayList<NodePanel>> levels;  // a map of depths to all nodes of that
  private int maxDepth;

  public TreeLayout(NodePanel root) {
    this.root = root;
    inorderTraversal = new ArrayList<>();
    levels = new HashMap<>();

    maxDepth = 0;
    addToLevel(root, 0);
  }

  public void run() {
    inorderTraversal(root, 0);
    doLayout();
  }

  /**
   * Create an inorderTraversal in the member variable inorderTraversal
   * and populate the levels HashMap
   *
   * @param node
   */
  private int inorderTraversal(NodePanel node, int visited) {
    int numChildren = node.getNumChildren(),
            midpoint = numChildren / 2;
    for (int i = 0; i < midpoint; ++i)
      visited = inorderTraversal(node.getChildren().get(i), visited);

    node.setXRank(visited++);
    inorderTraversal.add(node);  // add this node to the inorder traversal
    if (node != root)
      addToLevel(node, node.getDepth());

    for (int i = midpoint; i < numChildren; ++i)
      visited = inorderTraversal(node.getChildren().get(i), visited);
    return visited;
  }

  /**
   * Add the node to the depth-sorted HashMap levels
   *
   * @param node  the node to be added
   * @param level the level (depth) of the node
   */
  private void addToLevel(NodePanel node, int level) {
    if (!levels.containsKey(level)) {
      ArrayList<NodePanel> list = new ArrayList<>();
      list.add(node);
      levels.put(level, list);
    } else
      levels.get(level).add(node);

    if (level > maxDepth)
      maxDepth = level;
  }

  /**
   * Traverse the tree in a breadth-first manner, assigning
   * x and y location values to each node based on depth and
   * inorder rank
   */
  private void doLayout() {
    // Set all of the y values on the way down
    int levelY = BUFFER;
    ArrayList<NodePanel> level;
    for (int i = 0; i <= maxDepth; ++i) {
      level = levels.get(i);
      int maxHeight = 0;
      for (NodePanel node : level) {
        node.setX(BUFFER);
        node.setY(levelY);
        maxHeight = Math.max(maxHeight, node.getHeight());
        //node.updateBounds();
      }

      levelY += maxHeight + BUFFER;
    }

    // Assign x values to the bottom level
    level = levels.get(maxDepth);
    int x = BUFFER;
    for (NodePanel node : level) {
      node.setX(x);
      x += node.getWidth() + BUFFER;
    }

    // Set all of the x values on the way back up
    // as an average of each node's child x values
    for (int i = maxDepth - 1; i >= 0; --i) {
      level = levels.get(i);
      for (int j = 0; j < level.size(); ++j) {
        NodePanel node = level.get(j);
        OptionalDouble o = node.getChildren().stream().mapToInt(NodePanel::getX).average();
        if (o.isPresent())
          node.setX((int) o.getAsDouble());

        // if this branch is too far left, recursively move the branch right
        if (j > 0) {
          NodePanel sibling = level.get(j - 1);
          if (node.getX() <= sibling.getX() + sibling.getWidth() + BUFFER) {
            node.moveBranch(sibling.getX() + sibling.getWidth() + BUFFER - node.getX(), 0);
          }
        }
      }
    }

    inorderTraversal.forEach(NodePanel::updateBounds);
  }
}
