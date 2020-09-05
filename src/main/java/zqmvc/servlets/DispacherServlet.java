package zqmvc.servlets;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import zqmvc.annotation.Router;
import zqmvc.annotation.URLMapping;
import zqmvc.utils.AnnotationScanner;
import zqmvc.utils.ConfigurationReader;
import zqmvc.utils.model.URlAndClassMethodMapper;
import zqmvc.utils.model.ViewConfiguration;
import zqmvc.views.ThymeleafView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author zhouqing
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 * @// TODO: 2020/8/12 该类用于分发请求
 */

public class DispacherServlet extends HttpServlet {

    private String packageName;
    private List<String> controllerList;
    private Map<String, URlAndClassMethodMapper> URLMapper;
    private ConfigurationReader configuration;
    private ViewConfiguration viewConfiguration;

    @Override
    public void init() {
        controllerList = new ArrayList<>();
        URLMapper = new HashMap<>();
        configuration = ConfigurationReader.getInstance(this.getServletConfig());
        viewConfiguration = ViewConfiguration.getInstance();
        initPackageName();
        initControllerList();
        initURLMaper();
        System.out.println("scanered URLMapping like following:");
        for (String key:
             URLMapper.keySet()) {
            URlAndClassMethodMapper uRlAndClassMethodMapper = URLMapper.get(key);
            System.out.println(key+"===>"+uRlAndClassMethodMapper.getObject().getClass().getSimpleName()+"===>"
                    +uRlAndClassMethodMapper.getMethod().getName());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            req.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        String requestURI = req.getRequestURI().replace(req.getContextPath(),"");
        if (requestURI.endsWith(".css")||requestURI.endsWith(".js")||requestURI.endsWith(".jpg")
                ||requestURI.endsWith(".JPG")||requestURI.endsWith(".png")||requestURI.endsWith(".PNG")
                ||requestURI.endsWith(".mp4")){
            try {
                this.getServletContext().getNamedDispatcher("default").forward(req,resp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            URlAndClassMethodMapper uRlAndClassMethodMapper= URLMapper.get(requestURI);
            if (uRlAndClassMethodMapper==null) {
                resp.setStatus(404);
                try {
                    resp.getWriter().write("你访问的页面不存在！");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    Method method = uRlAndClassMethodMapper.getMethod();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Object[] params = new Object[method.getParameterCount()];
                    int index = 0;
                    for (Class<?> clazz:
                            parameterTypes) {
                        if (clazz==HttpServletRequest.class)
                            params[index] = req;
                        else if (clazz==HttpServletResponse.class)
                            params[index] = resp;
                        else
                            params[index] = null;
                        index++;
                    }
                    Object result = method.invoke(uRlAndClassMethodMapper.getObject(),params);
                    //根据返回的数据类型进行不同的处理
                    if (result instanceof String)
                        if (((String) result).startsWith("redirect:")) {
                            result = ((String) result).replace("redirect:", "");
                            if (((String) result).startsWith("/"))
                                resp.sendRedirect(req.getContextPath()+(String) result);
                            else
                                resp.sendRedirect((String) result);
                        }else{
                            //req.getRequestDispatcher("/"+(String) result).forward(req,resp);
                            if (viewConfiguration.getViewType().equals("jsp"))
                                req.getRequestDispatcher(viewConfiguration.getPrefix()+(String)result+
                                    viewConfiguration.getSuffix()).forward(req,resp);
//                                this.getServletContext().getNamedDispatcher("jsp").forward(req,resp);
                            else if (viewConfiguration.getViewType().equals("thymeleaf")){
                                ITemplateEngine templateEngine = ThymeleafView.getTemplateEngine(this.getServletContext());
                                templateEngine.process((String) result,new WebContext(req,resp,this.getServletContext()),resp.getWriter());
                            }
                        }
                    else {
                        resp.getWriter().write(JSONObject.toJSONString(result));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    System.out.println("please check parameters of the method '"+uRlAndClassMethodMapper.getObject()
                            .getClass().getSimpleName()+
                            "."+uRlAndClassMethodMapper.getMethod().getName()+
                            "' it can not include other type unless HttpServletRequest or HttpServletResponse");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        doGet(req,resp);
    }

    private void initPackageName(){
        this.packageName = configuration.getUSER_ROUTER_PACKAGENAME();
        if (packageName==null)
            try {
                throw new Exception("get the Configuration Parameter 'packageName' failed!");
            } catch (Exception e) {
                e.printStackTrace();
            }
//        System.out.println("packageName:"+packageName);
    }

    private void initControllerList(){
        controllerList.addAll(AnnotationScanner.getScanner().scanner(packageName, Router.class));
    }

    private void initURLMaper(){
        for (String controller:
                controllerList) {
            try {
                Class clazz = Class.forName(controller);
                Object obj = clazz.newInstance();
                URLMapping urlMapping = (URLMapping) clazz.getDeclaredAnnotation(URLMapping.class);
                String baseURL;
                if (urlMapping==null)
                    baseURL = "";
                else
                    baseURL = urlMapping.value();

                Method[] declaredMethods = obj.getClass().getDeclaredMethods();
                for (Method m:
                        declaredMethods) {
                    String childURL = m.getAnnotation(URLMapping.class).value();
                    URLMapper.put(baseURL+"/"+childURL,new URlAndClassMethodMapper(obj,m));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
