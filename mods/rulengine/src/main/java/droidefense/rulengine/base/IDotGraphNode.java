package droidefense.rulengine.base;

public interface IDotGraphNode {

    String getAsDotGraph();

    String getConnectionLabel();

    String getNodeLabel();

    String getConnectionStyle();

    String getNodeStyle();

    boolean isDrawable();
}
