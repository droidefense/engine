package axmlparser.android.util;


public interface AttributeSet {

    int getAttributeCount();

    String getAttributeName(int var1);

    String getAttributeValue(int var1);

    String getPositionDescription();

    int getAttributeNameResource(int var1);

    int getAttributeListValue(int var1, String[] var2, int var3);

    boolean getAttributeBooleanValue(int var1, boolean var2);

    int getAttributeResourceValue(int var1, int var2);

    int getAttributeIntValue(int var1, int var2);

    int getAttributeUnsignedIntValue(int var1, int var2);

    float getAttributeFloatValue(int var1, float var2);

    String getIdAttribute();

    String getClassAttribute();

    int getIdAttributeResourceValue(int var1);

    int getStyleAttribute();

    String getAttributeValue(String var1, String var2);

    int getAttributeListValue(String var1, String var2, String[] var3, int var4);

    boolean getAttributeBooleanValue(String var1, String var2, boolean var3);

    int getAttributeResourceValue(String var1, String var2, int var3);

    int getAttributeIntValue(String var1, String var2, int var3);

    int getAttributeUnsignedIntValue(String var1, String var2, int var3);

    float getAttributeFloatValue(String var1, String var2, float var3);

    int getAttributeValueType(int var1);

    int getAttributeValueData(int var1);
}
