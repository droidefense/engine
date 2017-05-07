package droidefense.sdk.manifest;

import droidefense.sdk.manifest.base.AbstractManifestClass;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class SupportsScreens extends AbstractManifestClass {


    //object vars
    private boolean resizeable;
    private boolean smallScreens;
    private boolean normalScreens;
    private boolean largeScreens;
    private boolean xlargeScreens;
    private boolean anyDensity;
    private int requiresSmallestWidthDp;
    private int compatibleWidthLimitDp;
    private int largestWidthLimitDp;

    @Override
    public void setParent(AbstractManifestClass parent) {

        if (this.parent == null)
            this.parent = parent;
        if (parent instanceof Manifest) {
            ((Manifest) this.parent).add(this);
        }
    }

    public boolean isResizeable() {
        return resizeable;
    }

    public void setResizeable(boolean resizeable) {
        this.resizeable = resizeable;
    }

    public boolean isSmallScreens() {
        return smallScreens;
    }

    public void setSmallScreens(boolean smallScreens) {
        this.smallScreens = smallScreens;
    }

    public boolean isNormalScreens() {
        return normalScreens;
    }

    public void setNormalScreens(boolean normalScreens) {
        this.normalScreens = normalScreens;
    }

    public boolean isLargeScreens() {
        return largeScreens;
    }

    public void setLargeScreens(boolean largeScreens) {
        this.largeScreens = largeScreens;
    }

    public boolean isXlargeScreens() {
        return xlargeScreens;
    }

    public void setXlargeScreens(boolean xlargeScreens) {
        this.xlargeScreens = xlargeScreens;
    }

    public boolean isAnyDensity() {
        return anyDensity;
    }

    public void setAnyDensity(boolean anyDensity) {
        this.anyDensity = anyDensity;
    }

    public int getRequiresSmallestWidthDp() {
        return requiresSmallestWidthDp;
    }

    public void setRequiresSmallestWidthDp(int requiresSmallestWidthDp) {
        this.requiresSmallestWidthDp = requiresSmallestWidthDp;
    }

    public int getCompatibleWidthLimitDp() {
        return compatibleWidthLimitDp;
    }

    public void setCompatibleWidthLimitDp(int compatibleWidthLimitDp) {
        this.compatibleWidthLimitDp = compatibleWidthLimitDp;
    }

    public int getLargestWidthLimitDp() {
        return largestWidthLimitDp;
    }

    public void setLargestWidthLimitDp(int largestWidthLimitDp) {
        this.largestWidthLimitDp = largestWidthLimitDp;
    }

    @Override
    public String toString() {
        return "SupportsScreens{" +
                "resizeable=" + resizeable +
                ", smallScreens=" + smallScreens +
                ", normalScreens=" + normalScreens +
                ", largeScreens=" + largeScreens +
                ", xlargeScreens=" + xlargeScreens +
                ", anyDensity=" + anyDensity +
                ", requiresSmallestWidthDp=" + requiresSmallestWidthDp +
                ", compatibleWidthLimitDp=" + compatibleWidthLimitDp +
                ", largestWidthLimitDp=" + largestWidthLimitDp +
                '}';
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }


}
