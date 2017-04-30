package droidefense.axml.base;


public interface AttributeSet {

    int getAttributeCount();

    String getAttributeName(int key);

    String getAttributeValue(int key);

    String getPositionDescription();

    int getAttributeNameResource(int key);

    int getAttributeListValue(int key, String[] value, int var3);

    boolean getAttributeBooleanValue(int key, boolean value);

    int getAttributeResourceValue(int key, int value);

    int getAttributeIntValue(int key, int value);

    int getAttributeUnsignedIntValue(int key, int value);

    float getAttributeFloatValue(int key, float value);

    String getIdAttribute();

    String getClassAttribute();

    int getIdAttributeResourceValue(int key);

    int getStyleAttribute();

    String getAttributeValue(String key, String value);

    int getAttributeListValue(String key, String value, String[] var3, int var4);

    boolean getAttributeBooleanValue(String key, String value, boolean var3);

    int getAttributeResourceValue(String key, String value, int var3);

    int getAttributeIntValue(String key, String value, int var3);

    int getAttributeUnsignedIntValue(String key, String value, int var3);

    float getAttributeFloatValue(String key, String value, float var3);

    int getAttributeValueType(int key);

    int getAttributeValueData(int key);
}
