package droidefense.reporting;

import freemarker.template.*;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by .local on 03/05/2017.
 */
public class HTMLReporter extends AbstractReporter {

    @Override
    public boolean generateReport() {
        try {
            // 1. Configure FreeMarker
            //
            // You should do this ONLY ONCE, when your application starts,
            // then reuse the same Configuration object elsewhere.

            Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);

            // Where do we load the templates from:
            cfg.setClassForTemplateLoading(HTMLReporter.class, "template");

            // Some other recommended settings:
            cfg.setIncompatibleImprovements(new Version(2, 3, 20));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setLocale(Locale.US);
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            // 2. Proccess template(s)
            //
            // You will do this for several times in typical applications.

            // 2.1. Prepare the template input:

            Map<String, Object> input = new HashMap<String, Object>();

            input.put("title", "Vogella example");

            // 2.2. Get the template

            Template template = null;
            template = cfg.getTemplate("template.ftl");
            // 2.3. Generate the output

            // Write output to the console
            Writer consoleWriter = new OutputStreamWriter(System.out);
            template.process(input, consoleWriter);

            // For the sake of example, also write output into a file:
            Writer fileWriter = new FileWriter(new File("report.html"));
            template.process(input, fileWriter);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return false;
    }
}
