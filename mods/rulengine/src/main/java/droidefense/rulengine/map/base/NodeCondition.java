package droidefense.rulengine.map.base;

public interface NodeCondition {

    boolean condition();

    void branchTrue();

    void branchFalse();

}
