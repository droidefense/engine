package droidefense.rulengine.base;

public interface NodeCondition {

    boolean condition();

    void branchTrue();

    void branchFalse();

}
