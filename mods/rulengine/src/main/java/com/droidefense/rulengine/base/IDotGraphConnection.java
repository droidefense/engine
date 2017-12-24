package com.droidefense.rulengine.base;

public interface IDotGraphConnection {

    String getAsDotGraph();

    String getConnectionLabel();

    String getNodeLabel();

    String getConnectionStyle();

    String getNodeStyle();

    boolean isDrawable();
}
