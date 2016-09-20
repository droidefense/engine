package axmlparser.android.content.res;

import axmlparser.android.util.AttributeSet;
import axmlparser.org.xmlpull.v1.XmlPullParser;

public interface XmlResourceParser extends XmlPullParser, AttributeSet {

    void close();
}
