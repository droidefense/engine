package droidefense.xmodel.manifest;

import droidefense.xmodel.manifest.base.AbstractManifestClass;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class Instrumentation extends AbstractManifestClass {


    //object vars
    private boolean functionalTest;
    private boolean handleProfiling;
    private String icon;
    private String label;
    private String targetPackage;

    public boolean isFunctionalTest() {
        return functionalTest;
    }

    public void setFunctionalTest(boolean functionalTest) {
        this.functionalTest = functionalTest;
    }

    public boolean isHandleProfiling() {
        return handleProfiling;
    }

    public void setHandleProfiling(boolean handleProfiling) {
        this.handleProfiling = handleProfiling;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    @Override
    public void setParent(AbstractManifestClass parent) {

        if (this.parent == null)
            this.parent = parent;
        if (parent instanceof Manifest) {
            ((Manifest) this.parent).add(this);
        }
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }


}
