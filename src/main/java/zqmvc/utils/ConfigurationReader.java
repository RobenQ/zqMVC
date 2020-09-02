package zqmvc.utils;

import javax.servlet.ServletConfig;
import java.io.FileNotFoundException;
import java.util.ResourceBundle;

public class ConfigurationReader {

    //RouterPackagetName：配置路由扫描是配置文件中的key
    private final String ROUTER_PACKAGE_NAME_KEY = "RouterPackagetName";

    private final String DEFAULT_ROUTER_PACKAGENAME = "";

    private String USER_ROUTER_PACKAGENAME;

    //configuration：配置DispacherServlet初始化参数的参数名称
    private final String CONFIGURATION_KEY = "configuration";

    private final String DEFAULT_CONFIGURATION_FILENAME = "zqMVC.properties";

    private String USER_CONFIGURATION_FILENAME;

    //viewTemplate
    private final String VIEW_TEMPLATE_KEY = "viewTemplate";

    private final String DEFAULT_VIEW_TEMPLATE = "jsp";

    private String USER_VIEW_TEMPLATE;

    public synchronized static ConfigurationReader getInstance(ServletConfig sc) {
        return new ConfigurationReader(sc);
    }
    private ConfigurationReader(ServletConfig sc) {
        String userConfigurationFileName = sc.getInitParameter(CONFIGURATION_KEY);

        if (userConfigurationFileName==null || userConfigurationFileName=="")
            USER_CONFIGURATION_FILENAME = DEFAULT_CONFIGURATION_FILENAME.replace(".properties","");
        else
            USER_CONFIGURATION_FILENAME = userConfigurationFileName.replace(".properties","");

        ResourceBundle configuration = ResourceBundle.getBundle(USER_CONFIGURATION_FILENAME);

        if ((USER_ROUTER_PACKAGENAME=configuration.getString(ROUTER_PACKAGE_NAME_KEY))==null)
            USER_ROUTER_PACKAGENAME = DEFAULT_ROUTER_PACKAGENAME;
//        printConfiguration();
    }

    private void printConfiguration(){
        System.out.println(this.USER_CONFIGURATION_FILENAME);
        System.out.println(getUSER_ROUTER_PACKAGENAME());
    }

    public String getUSER_ROUTER_PACKAGENAME() {
        return USER_ROUTER_PACKAGENAME;
    }

}
