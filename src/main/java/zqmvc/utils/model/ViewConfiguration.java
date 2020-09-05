package zqmvc.utils.model;

public class ViewConfiguration {
    private String viewType;
    private final String DEFAULT_VIEW_TYPE = "jsp";
    private String prefix;
    private final String DEFAULT_PREFIX = "/static/";
    private String suffix;
    private final String DEFAULT_SUFFFIX = ".jsp";

    private static ViewConfiguration viewConfiguration = new ViewConfiguration();

    public ViewConfiguration(){
        this.viewType = DEFAULT_VIEW_TYPE;
        this.prefix = DEFAULT_PREFIX;
        this.suffix = DEFAULT_SUFFFIX;
    }

    public static ViewConfiguration getInstance(){
        return viewConfiguration;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return "ViewConfiguration{" +
                "viewType='" + viewType + '\'' +
                ", DEFAULT_VIEW_TYPE='" + DEFAULT_VIEW_TYPE + '\'' +
                ", prefix='" + prefix + '\'' +
                ", DEFAULT_PREFIX='" + DEFAULT_PREFIX + '\'' +
                ", suffix='" + suffix + '\'' +
                ", DEFAULT_SUFFFIX='" + DEFAULT_SUFFFIX + '\'' +
                '}';
    }
}
