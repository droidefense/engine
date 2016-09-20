package com.zerjioang.apkr.v1.core.cfg.base;

/**
 * Created by sergio on 9/4/16.
 */
public interface IDotGraphNode {

    String getAsDotGraph();

    String getConnectionLabel();

    String getNodeLabel();

    String getConnectionStyle();

    String getNodeStyle();

    boolean isDrawable();
}
