package com.droidefense.map.base;

/**
 * Created by sergio on 22/5/16.
 */
public interface NodeCondition {

    boolean condition();

    void branchTrue();

    void branchFalse();

}
