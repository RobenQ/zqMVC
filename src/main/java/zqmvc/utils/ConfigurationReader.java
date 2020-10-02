package zqmvc.utils;

import zqmvc.utils.model.ViewConfiguration;

import javax.servlet.ServletConfig;
import java.util.ResourceBundle;

public class ConfigurationReader {

    //RouterPackagetName：配置路由扫描是配置文件中的key
    private final String ROUTER_PACKAGE_NAME_KEY = "RouterPackageName";

    private final String DEFAULT_ROUTER_PACKAGENAME = "";

    private String USER_ROUTER_PACKAGENAME;

    //configuration：配置DispacherServlet初始化参数的参数名称
    private final String CONFIGURATION_KEY = "configuration";

    private final String DEFAULT_CONFIGURATION_FILENAME = "zqMVC.properties";

    private String USER_CONFIGURATION_FILENAME;

    //viewTemplate：配置视图模板引擎时在配置文件中配置的属性的key
    private final String VIEW_TEMPLATE_KEY = "viewTemplate";

    private final String DEFAULT_VIEW_TEMPLATE = "jsp";

    private String USER_VIEW_TEMPLATE;

    //view.prefix：静态资源的前缀
    private final String VIEW_PREFIX = "view.prefix";

    private final String DEFAULT_VIEW_PREFIX = "/static/";

    private String USER_VIEW_PREFIX;

    //view.suffix：静态资源的后缀
    private final String VIEW_SUFFIX = "view.suffix";

    private final String DEFAULT_VIEW_SUFFIX = ".jsp";

    private String USER_VIEW_SUFFIX;

    public static ConfigurationReader getInstance(ServletConfig sc) {
        return new ConfigurationReader(sc);
    }
    private ConfigurationReader(ServletConfig sc) {
        String userConfigurationFileName = sc.getInitParameter(CONFIGURATION_KEY);

        if (userConfigurationFileName==null || userConfigurationFileName.equals(""))
            USER_CONFIGURATION_FILENAME = DEFAULT_CONFIGURATION_FILENAME.replace(".properties","");
        else
            USER_CONFIGURATION_FILENAME = userConfigurationFileName.replace(".properties","");

        ResourceBundle configuration = ResourceBundle.getBundle(USER_CONFIGURATION_FILENAME);

        try {
            if ((USER_ROUTER_PACKAGENAME=configuration.getString(ROUTER_PACKAGE_NAME_KEY))==null)
                USER_ROUTER_PACKAGENAME = DEFAULT_ROUTER_PACKAGENAME;
        } catch (Exception e) {
            USER_ROUTER_PACKAGENAME = DEFAULT_ROUTER_PACKAGENAME;
            //e.printStackTrace();
        }

        try {
            if ((USER_VIEW_TEMPLATE=configuration.getString(VIEW_TEMPLATE_KEY))==null)
                USER_VIEW_TEMPLATE = DEFAULT_VIEW_TEMPLATE;
        } catch (Exception e) {
            USER_VIEW_TEMPLATE = DEFAULT_VIEW_TEMPLATE;
            //e.printStackTrace();
        }

        try {
            if ((USER_VIEW_PREFIX=configuration.getString(VIEW_PREFIX))==null)
                USER_VIEW_PREFIX = DEFAULT_VIEW_PREFIX;
        } catch (Exception e) {
            USER_VIEW_PREFIX = DEFAULT_VIEW_PREFIX;
            //e.printStackTrace();
        }

        try {
            if ((USER_VIEW_SUFFIX=configuration.getString(VIEW_SUFFIX))==null)
                USER_VIEW_SUFFIX = DEFAULT_VIEW_SUFFIX;
        } catch (Exception e) {
            USER_VIEW_SUFFIX = DEFAULT_VIEW_SUFFIX;
            //e.printStackTrace();
        }

        if (!USER_VIEW_PREFIX.endsWith("/"))
            USER_VIEW_PREFIX = USER_VIEW_PREFIX+"/";
        if (!USER_VIEW_PREFIX.startsWith("/"))
            USER_VIEW_PREFIX = "/"+USER_VIEW_PREFIX;
        ViewConfiguration.getInstance().setViewType(USER_VIEW_TEMPLATE);
        ViewConfiguration.getInstance().setPrefix(USER_VIEW_PREFIX);
        ViewConfiguration.getInstance().setSuffix(USER_VIEW_SUFFIX);

//        printConfiguration();
    }

    private void printConfiguration(){
        System.out.println(this.USER_CONFIGURATION_FILENAME);
        System.out.println(getUSER_ROUTER_PACKAGENAME());
        System.out.println(ViewConfiguration.getInstance().toString());
    }

    public String getUSER_ROUTER_PACKAGENAME() {
        return USER_ROUTER_PACKAGENAME;
    }

}
