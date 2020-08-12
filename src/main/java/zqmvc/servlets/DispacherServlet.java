package zqmvc.servlets;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import zqmvc.annotation.Router;
import zqmvc.annotation.URLMapping;
import zqmvc.utils.AnnotationScanner;
import zqmvc.utils.model.URlAndClassMethodMapper;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouqing
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 * @// TODO: 2020/8/12 该类用于分发请求
 */

public class DispacherServlet extends HttpServlet {

    private String packageName =null;
    private List<String> controllerList = new ArrayList<>();
    private Map<String, URlAndClassMethodMapper> URLMapper = new HashMap<>();

    @Override
    public void init() {
        this.packageName = this.getServletConfig().getInitParameter("packageName");
        if (packageName==null)
            try {
                throw new Exception("get the InitParameter 'packageName' failed! Please config this InitParameter.");
            } catch (Exception e) {
                e.printStackTrace();
            }

        controllerList.addAll(AnnotationScanner.getScanner().scanner(packageName, Router.class));
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
        System.out.println("scanered URLMapping like following:");
        for (String key:
             URLMapper.keySet()) {
            URlAndClassMethodMapper uRlAndClassMethodMapper = URLMapper.get(key);
            System.out.println(key+"====>>"+uRlAndClassMethodMapper.getObject().getClass().getSimpleName()+"===>>"
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
                if (result instanceof String)
                    if (((String) result).startsWith("redirect:")) {
                        result = ((String) result).replace("redirect:", "");
                        if (((String) result).startsWith("/"))
                            resp.sendRedirect(req.getContextPath()+(String) result);
                        else
                            resp.sendRedirect((String) result);
                    }else
                        req.getRequestDispatcher("/"+(String) result).forward(req,resp);
                else {
                    resp.getWriter().write(JSONObject.toJSONString(result));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                System.out.println("please check parameters of the method '"+uRlAndClassMethodMapper.getObject().getClass().getSimpleName()+
                        "."+uRlAndClassMethodMapper.getMethod().getName()+"' it can not include other type unless HttpServletRequest or HttpServletResponse");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        doGet(req,resp);
    }
}
