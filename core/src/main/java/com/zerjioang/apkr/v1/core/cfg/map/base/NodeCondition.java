package com.zerjioang.apkr.v1.core.cfg.map.base;

/**
 * Created by sergio on 22/5/16.
 */
public interface NodeCondition {

    boolean condition();

    void branchTrue();

    void branchFalse();

}
