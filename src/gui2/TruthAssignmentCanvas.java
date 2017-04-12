package gui2;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.view.mxCellState;

import javax.swing.*;

/**
 * Created by kevin on 2/21/17.
 */
public class TruthAssignmentCanvas extends mxInteractiveCanvas {
  private CellRendererPane rendererPane = new CellRendererPane();
  private mxGraphComponent graphComponent;

  public TruthAssignmentCanvas(mxGraphComponent graphComponent) {
    this.graphComponent = graphComponent;
    graphComponent.add(rendererPane);
  }

  public void drawVertex(mxCellState state) {
    if (state.getCell() instanceof mxCell && ((mxCell) state.getCell()).isVertex() && ((mxCell) state.getCell()).getValue() instanceof TruthAssignmentPanel) {
      TruthAssignmentPanel vertexRenderer = (TruthAssignmentPanel) ((mxCell) state.getCell()).getValue();

      rendererPane.paintComponent(g, vertexRenderer, graphComponent,
              (int) (state.getX() + translate.getX()),
              (int) (state.getY() + translate.getY()),
              (int) state.getWidth(), (int) state.getHeight(), true);
    } else {
      super.drawCell(state);
    }
  }
}
