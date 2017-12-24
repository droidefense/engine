package droidefense.log4j;


import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;

/**
 * Created by .local on 17/09/2016.
 */
public class TestLog4JConfig {

    public static void main(String[] args) {
        Log.write(LoggerType.TRACE, "Hello World from Log4j");
    }
}
