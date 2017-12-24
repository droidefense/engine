package droidefense.social;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GooglePlayCheckerTest {

    @Test
    public void checkName() {
        GooglePlayChecker gpc = new GooglePlayChecker(null);
        Assert.assertEquals(gpc.getPluginName(), "Google Play checker");
    }

    @Test
    public void checkRemoteData1() {
        GooglePlayChecker gpc = new GooglePlayChecker(null);
        Assert.assertTrue(
                gpc.getGooglePlayData(
                        "https://play.google.com/store/apps/details?id=com.google.android.youtube"
                ).contains("YouTube")
        );
    }

    @Test
    public void checkRemoteData2() {
        GooglePlayChecker gpc = new GooglePlayChecker(null);
        Assert.assertTrue(
                gpc.getGooglePlayData(
                        "https://play.google.com/store/apps/details?id=org.videolan.vlc"
                ).contains("VLC for Android")
        );
    }

    @Test
    public void checkRemoteData3() {
        GooglePlayChecker gpc = new GooglePlayChecker(null);
        Assert.assertTrue(
                gpc.getGooglePlayData(
                        "https://play.google.com/store/apps/details?id=org.videolan.vlc"
                ).contains("</div></body></html>")
        );
    }

    @Test
    public void checkAppExistanceFailed() {
        GooglePlayChecker gpc = new GooglePlayChecker(null);
        Assert.assertFalse(
                gpc.existsOnGooglePlay(
                        "https://play.google.com/store/apps/details?id=org.videolan.vlcfghjfghfghfg"
                )
        );
    }

    @Test
    public void checkAppExistanceSuccess() {
        GooglePlayChecker gpc = new GooglePlayChecker(null);
        Assert.assertTrue(
                gpc.existsOnGooglePlay(
                        "https://play.google.com/store/apps/details?id=org.videolan.vlc"
                )
        );
    }
}
