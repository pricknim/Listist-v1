package edu.codespring.listist.web;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class HandlebarsTemplateFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HandlebarsTemplateFactory.class);

    // Handlebars sablonmotra
    private static Handlebars handlebars;

    public static synchronized Template getTemplate(String templateName) throws IOException {
        // lazy initialization
        if (handlebars == null) {
            LOG.info("Building handlebars renderer");

            // A sablonokat a classpath-ből töltjük be (src/main/resources),
            // ezen belül megjelölünk egy dedikált foldert nekik (templates)
            TemplateLoader loader = new ClassPathTemplateLoader();
            loader.setPrefix("/templates");
            loader.setSuffix(".hbs");
            handlebars = new Handlebars(loader);
        }

        return handlebars.compile(templateName);
    }

}
