package zqmvc.views;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import zqmvc.utils.model.ViewConfiguration;

import javax.servlet.ServletContext;

public class ThymeleafView {
    private static TemplateEngine templateEngine;

    private ThymeleafView() {

    }

    public static TemplateEngine getTemplateEngine(ServletContext servletContext){
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        // This will convert "home" to "/WEB-INF/templates/home.html"
        templateResolver.setPrefix(ViewConfiguration.getInstance().getPrefix());
        templateResolver.setSuffix(ViewConfiguration.getInstance().getSuffix());
        // Template cache TTL=1h. If not set, entries would be cached until expelled
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));

        // Cache is set to true by default. Set to false if you want templates to
        // be automatically updated when modified.
        templateResolver.setCacheable(true);

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }
}
