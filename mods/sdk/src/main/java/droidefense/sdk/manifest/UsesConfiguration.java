package droidefense.sdk.manifest;

import droidefense.sdk.manifest.base.AbstractManifestClass;
import droidefense.sdk.manifest.enums.ReqNavigation;
import droidefense.sdk.manifest.enums.ReqTouchScreen;
import droidefense.sdk.manifest.enums.RqKeyboardType;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class UsesConfiguration extends AbstractManifestClass {


    //object vars
    private boolean reqFiveWayNav;
    private boolean reqHardKeyboard;
    private RqKeyboardType reqKeyboardType;
    private ReqNavigation reqNavigation;
    private ReqTouchScreen reqTouchScreen;

    @Override
    public void setParent(AbstractManifestClass parent) {

        if (this.parent == null)
            this.parent = parent;
        if (parent instanceof Manifest) {
            ((Manifest) this.parent).add(this);
        }
    }

    public boolean isReqFiveWayNav() {
        return reqFiveWayNav;
    }

    public void setReqFiveWayNav(boolean reqFiveWayNav) {
        this.reqFiveWayNav = reqFiveWayNav;
    }

    public boolean isReqHardKeyboard() {
        return reqHardKeyboard;
    }

    public void setReqHardKeyboard(boolean reqHardKeyboard) {
        this.reqHardKeyboard = reqHardKeyboard;
    }

    public RqKeyboardType getReqKeyboardType() {
        return reqKeyboardType;
    }

    public void setReqKeyboardType(RqKeyboardType reqKeyboardType) {
        this.reqKeyboardType = reqKeyboardType;
    }

    public ReqNavigation getReqNavigation() {
        return reqNavigation;
    }

    public void setReqNavigation(ReqNavigation reqNavigation) {
        this.reqNavigation = reqNavigation;
    }

    public ReqTouchScreen getReqTouchScreen() {
        return reqTouchScreen;
    }

    public void setReqTouchScreen(ReqTouchScreen reqTouchScreen) {
        this.reqTouchScreen = reqTouchScreen;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }


}
