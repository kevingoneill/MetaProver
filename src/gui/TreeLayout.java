package gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * The TreeLayout class is responsible for arranging NodePanels
 * on the GraphPanel, such that no nodes overlap, all children are
 * evenly spaced, and every parent is centered between (and above)
 * its children.
 *
 * The running time is approx. O(3N)-
 *    - N for an inorder traversal, ranking nodes
 *    - N to set Y values based on depth
 *    - N to set X values based on inorder rank
 * Possible extensions- do these computations in place for O(N)
 */
public class TreeLayout {

  public static int BUFFER = 25;

  private NodePanel root;
  private ArrayList<NodePanel> inorderTraversal;    // a list of all NodePanels sorted by inorder rank
  private HashMap<Integer, ArrayList<NodePanel>> levels;  // a map of depths to all nodes of that
  private int minDepth, maxDepth, maxY;

  public TreeLayout(NodePanel root) {
    this.root = root;
    inorderTraversal = new ArrayList<>();
    levels = new HashMap<>();

    maxDepth = 0;
    minDepth = 0;
    addToLevel(root, 0);
  }

  public TreeLayout(Collection<NodePanel> roots) {
    maxY = 0;
    maxDepth = -1;
    inorderTraversal = new ArrayList<>();
    levels = new HashMap<>();

    for (NodePanel r : roots) {
      this.root = r;
      minDepth = maxDepth+1;
      addToLevel(root, minDepth);
      inorderTraversal(r, 0);
    }

    root = null;
  }

  public void run() {
    if (root != null)
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
      addToLevel(node, node.getDepth() + minDepth);

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
    maxY = BUFFER;
    ArrayList<NodePanel> level;
    for (int i = 0; i <= maxDepth; ++i) {
      level = levels.get(i);

      int maxHeight = 0;
      for (NodePanel node : level) {
        node.setX(BUFFER);
        node.setY(maxY);
        maxHeight = Math.max(maxHeight, node.getHeight());
      }

      maxY += maxHeight + BUFFER;
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

        // average children values
        if (node.getNumChildren() > 0) {
          NodePanel rightChild = node.getChildren().get(node.getNumChildren() - 1);
          int leftChildX = node.getChildren().get(0).getX(),
                  rightChildX = rightChild.getX() + rightChild.getWidth();
          node.setX((leftChildX + rightChildX - node.getWidth()) / 2);
        } else
          node.setX(BUFFER);

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
