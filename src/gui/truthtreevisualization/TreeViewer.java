package gui.truthtreevisualization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TreeViewer extends JPanel {
  private static final long serialVersionUID = -3510274108984179880L;

  private Point baseReference, clickPoint;
  private TruthTree tree;
  private int xPan, yPan;
  private String name;

  private static final int DEFAULT_VERT_SPACE = 20;
  private static final int DEFAULT_HORZ_SPACE = 10;
  private static final int DEFAULT_LINE_THICK = 2;
//	private static final float DEFAULT_FONT_SIZE = 12.0f;
//	
//	private int VERT_SPACE, HORZ_SPACE, LINE_THICK;
//	private float FONT_SIZE;

  public TreeViewer(TruthTree t, String name) {
    super();
    setOpaque(false);
    setBackground(new Color(0, 0, 0, 0));
    setLayout(null);
    baseReference = new Point(this.getWidth() / 2, 10);
//		setPreferredSize(new Dimension(w, h));
    setFocusable(true);
    this.tree = t;
    xPan = yPan = 0;
    clickPoint = new Point(0, 0);
    this.name = name;

//		VERT_SPACE = DEFAULT_VERT_SPACE;
//		HORZ_SPACE = DEFAULT_HORZ_SPACE;
//		LINE_THICK = DEFAULT_LINE_THICK;
//		FONT_SIZE = DEFAULT_FONT_SIZE;

    // Add listener for mouse drag panning
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        clickPoint = e.getPoint();
      }
    });

    addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        xPan -= clickPoint.x - e.getPoint().x;
        yPan -= clickPoint.y - e.getPoint().y;
        clickPoint = e.getPoint();
        repaint();
      }
    });

//		this.addMouseWheelListener(new MouseWheelListener() {
//			@Override
//			public void mouseWheelMoved(MouseWheelEvent e) {
//				if (e.getWheelRotation() < 0) {
//					VERT_SPACE += 1;
//					HORZ_SPACE += 1;
//					LINE_THICK += 1;
//					FONT_SIZE *= 1.01;
//				} else {
//					if (VERT_SPACE < DEFAULT_VERT_SPACE) {
//						return;
//					}
//					VERT_SPACE -= 1;
//					HORZ_SPACE -= 1;
//					LINE_THICK -= 1;
//					FONT_SIZE *= 0.99;
//				}
//				
//				updateFontSize(tree.getRoot(), FONT_SIZE);
//				revalidate();
//				repaint();
//			}
//		});
  }

  @Override
  public String getName() {
    return name;
  }

  public void addTree(TruthTree t) {
    this.tree = t;

    revalidate();
    repaint();
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;


    if (tree == null) {
      return;
    }

    baseReference.x = this.getWidth() / 2 + xPan;
    baseReference.y = 10 + yPan;

    Queue<BranchPositionPair> printQueue = new ConcurrentLinkedQueue<BranchPositionPair>();
    printQueue.add(new BranchPositionPair(tree.getRoot(), baseReference));
    while (!printQueue.isEmpty()) {
      // get next branch/reference point
      BranchPositionPair currPair = printQueue.remove();
      TreeBranch currBranch = currPair.branch;
      Point ref = currPair.reference;

      // display branch
      this.add(currBranch);
      Dimension size = currBranch.getPreferredSize();
      Point anchor = calculateAnchor(ref, size.width);
      currBranch.setBounds(anchor.x, anchor.y, size.width, size.height);

      // draw line if applicable
      currBranch.setBottomAnchor(new Point(ref.x, ref.y + size.height));
      if (currBranch.getParent() != null) {
        Point lineStart = currBranch.getParent().getBottomAnchor();
        g2.setColor(Color.blue);
        g2.setStroke(new BasicStroke(DEFAULT_LINE_THICK));
        g2.drawLine(lineStart.x, lineStart.y, ref.x, ref.y);
      }

      // calculate new reference points for children
      int currFrameWidth = getFrameWidth(currBranch);
      for (TreeBranch c : currBranch.getChildren()) {
        int childFrameWidth = getFrameWidth(c);
        int newX;
        int newY = ref.y + currBranch.getPreferredSize().height + DEFAULT_VERT_SPACE;
        double relativeFrameSize = (double) childFrameWidth / (double) currFrameWidth;
        int relativePlacement = (int) ((relativeFrameSize / 2.0) * currFrameWidth);
        if (c.isLeftChild()) {
          int leftBorder = ref.x - (currFrameWidth / 2);
          newX = leftBorder + relativePlacement;
        } else {
          int rightBorder = ref.x + (currFrameWidth / 2);
          newX = rightBorder - relativePlacement;
        }
        Point newRef = new Point(newX, newY);
        printQueue.add(new BranchPositionPair(c, newRef));
      }

      currBranch.revalidate();
      currBranch.repaint();
    }
  }

  private class BranchPositionPair {
    TreeBranch branch;
    Point reference;

    BranchPositionPair(TreeBranch b, Point r) {
      branch = b;
      reference = r;
    }
  }

  private Point calculateAnchor(Point reference, int branchWidth) {
    int x = reference.x - (branchWidth / 2);
    int y = reference.y;
    return new Point(x, y);
  }

  private int getFrameWidth(TreeBranch topBranch) {
    if (topBranch.isLeaf()) {
      return topBranch.getPreferredSize().width;
    } else {
      int fwidth = 0;
      for (TreeBranch c : topBranch.getChildren()) {
        fwidth += getFrameWidth(c);
      }
      return fwidth + DEFAULT_HORZ_SPACE;
    }

  }

//	private void updateFontSize(TreeBranch b, float newSize) {
//		b.getStatements().values().forEach(s -> {
//			s.setFont(s.getFont().deriveFont((float) 25.0));
//			s.revalidate();
//		});
//		b.revalidate();
//		b.repaint();
//		b.getChildren().forEach(c -> updateFontSize(c, newSize));
//	}
}