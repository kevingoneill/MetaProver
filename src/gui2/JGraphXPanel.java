package gui2;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import expression.sentence.DeclarationParser;
import expression.sentence.Sentence;
import logicalreasoner.prover.SemanticProver;
import logicalreasoner.truthassignment.TruthAssignment;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A JGraphXPanel encloses a drawing space for graphs
 * using JGraphX
 */
public class JGraphXPanel extends JPanel {

  private mxGraph graph;
  private mxGraphComponent graphComponent;
  private mxCompactTreeLayout layout;
  private mxCell root = null;

  public JGraphXPanel() {
    super(new GridLayout(1, 1));
    graph = new mxGraph() {
      @Override
      public boolean isCellSelectable(Object o) {
        if (model.isEdge(o))
          return false;
        return super.isCellSelectable(o);
      }

      public void drawState(mxICanvas canvas, mxCellState state, boolean drawLabel) {
        if (getModel().isVertex(state.getCell()) && canvas instanceof mxImageCanvas
                && ((mxImageCanvas) canvas).getGraphicsCanvas() instanceof TruthAssignmentCanvas) {
          // Indirection for wrapped swing canvas inside image canvas (used for creating
          // the preview image when cells are dragged)
          ((TruthAssignmentCanvas) ((mxImageCanvas) canvas).getGraphicsCanvas()).drawVertex(state);
        } else if (getModel().isVertex(state.getCell()) && canvas instanceof TruthAssignmentCanvas) {
          // Redirection of drawing vertices in SwingCanvas
          ((TruthAssignmentCanvas) canvas).drawVertex(state);
        } else {
          super.drawState(canvas, state, drawLabel);
        }
      }

      @Override
      public mxRectangle getPreferredSizeForCell(Object o) {
        if (o instanceof mxCell && ((mxCell) o).isVertex())
          return ((mxCell) o).getGeometry();

        return super.getPreferredSizeForCell(o);
      }
    };

    graph.setCellsCloneable(false);
    graph.setCellsBendable(false);
    graph.setCellsDeletable(false);
    graph.setCellsEditable(false);
    graph.setCellsDisconnectable(false);
    graph.setCellsResizable(false);

    graph.setAllowDanglingEdges(false);
    graph.setAllowDanglingEdges(false);
    graph.setAllowLoops(false);
    graph.setAllowNegativeCoordinates(true);
    graph.setMultigraph(false);
    graph.setEdgeLabelsMovable(false);
    graph.setKeepEdgesInBackground(true);
    graph.setConnectableEdges(false);
    graph.setDisconnectOnMove(false);

    Map<String, Object> style = new HashMap<>();
    style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
    style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
    style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
    graph.getStylesheet().setDefaultEdgeStyle(style);


    Object p = graph.getDefaultParent();
    graphComponent = new mxGraphComponent(graph) {
      public mxInteractiveCanvas createCanvas() {
        return new TruthAssignmentCanvas(this);
      }
    };

    // Remove interfering mouselisteners
    //Arrays.stream(graphComponent.getGraphControl().getMouseListeners()).forEach(graphComponent.getGraphControl()::removeMouseListener);
    //Arrays.stream(graphComponent.getGraphControl().getMouseMotionListeners()).filter(l -> l instanceof mxConnectionHandler).forEach(graphComponent.getGraphControl()::removeMouseMotionListener);
    //Arrays.stream(graphComponent.getGraphControl().getMouseListeners()).forEach(System.out::println);

    add(graphComponent);
    graphComponent.setAntiAlias(true);
    graphComponent.setConnectable(false);
    graphComponent.setPanning(true);
    graphComponent.setCenterPage(true);
    graphComponent.setCenterZoom(true);
    graphComponent.setDragEnabled(true);
    graphComponent.setAutoExtend(true);
    graphComponent.setAutoScroll(true);
    graphComponent.zoomAndCenter();

    graphComponent.getViewport().setOpaque(true);
    graphComponent.getViewport().setBackground(Color.WHITE);
    graph.getModel().beginUpdate();
    try {
      /*
      mxCell v1 = (mxCell) graph.insertVertex(p, "A", "Hello", 250, 400, 238, 100);
      mxCell v3 = (mxCell) graph.insertVertex(p, "C", "World!", 300, 150, 80, 30);
      graph.insertEdge(p, null, "", v1, v3);
      */

      HashSet<String> declarations = new HashSet<>(),
              premises = new HashSet<>();
      declarations.add("Boolean A");
      declarations.add("Boolean B");
      declarations.add("Boolean C");
      premises.add("(implies A (and B C))");
      premises.add("(iff C B)");
      premises.add("(not C)");

      Set<Sentence> premiseSet = new HashSet<>();
      declarations.forEach(DeclarationParser::parseDeclaration);
      premises.forEach(premise -> premiseSet.add(Sentence.makeSentence(premise)));
      SemanticProver prover = new SemanticProver(premiseSet, Sentence.makeSentenceStrict("(not A)"), false);
      TruthAssignmentPanel.prover = prover;
      prover.run();

      layout = new mxCompactTreeLayout(graph, false, false);
      layout.setEdgeRouting(false);
      layout.setUseBoundingBox(true);
      layout.setResetEdges(true);
      layout.setMoveTree(true);

      root = makeTree(prover.getTruthAssignment());
    } finally {
      graph.getModel().endUpdate();
    }

    runLayout();
  }


  public mxCell makeTree(TruthAssignment root) {
    mxCell cell = new mxCell();
    cell.setValue(new TruthAssignmentPanel(cell, root));
    cell.setVertex(true);
    cell.setEdge(false);

    graph.getModel().beginUpdate();
    graph.addCell(cell, graph.getDefaultParent());
    root.getChildren().forEach(c -> graph.insertEdge(graph.getDefaultParent(), null, "", cell, makeTree(c)));
    graph.getModel().endUpdate();
    System.out.println(graph.getPreferredSizeForCell(cell));
    graph.updateCellSize(cell);
    return cell;
  }

  public void runLayout() {
    if (root == null)
      layout.execute(graph.getDefaultParent());
    else
      layout.execute(graph.getDefaultParent(), root);
  }
}
