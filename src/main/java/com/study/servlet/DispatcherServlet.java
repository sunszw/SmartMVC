package com.study.servlet;

import com.study.annotation.RequestMapping;
import com.study.mapping.Handler;
import com.study.mapping.HandlerMapping;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 调度器处理分发请求
 */
public class DispatcherServlet extends HttpServlet {

    private HandlerMapping handlerMapping;

    /**
     * 读取smartmvc.xml配置文件
     * 将控制器实例化，然后交给HandleMapping处理
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        try {
            SAXReader saxReader = new SAXReader();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(getInitParameter("configLocation"));
            Document document = saxReader.read(inputStream);
            Element root = document.getRootElement();
            List<Element> elements = root.elements();
            //用于存存放bean对象
            List<Object> beans = new ArrayList<>();
            for (Element element : elements) {
                String className = element.attributeValue("class");
                //实例化所有bean对象
                Object object = Class.forName(className).newInstance();
                beans.add(object);
            }
            //调用handlerMapping处理
            handlerMapping = new HandlerMapping();
            handlerMapping.process(beans);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取提交的路径
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());
        //获取handler对象
        Handler handler = handlerMapping.getHandler(path);
        if (handler == null) {
            resp.sendError(404);
            return;
        }
        Object bean = handler.getBean();
        Method method = handler.getMethod();
        Object rv = null;
        try {
            //处理方法上的参数
            Class[] types = method.getParameterTypes();
            if (types.length == 0) {
                rv = method.invoke(bean);
            } else {
                Object[] params = new Object[types.length];
                for (int i = 0; i < types.length; i++) {
                    if (types[i] == HttpServletRequest.class) {
                        params[i] = req;
                    }
                    if (types[i] == HttpServletResponse.class) {
                        params[i] = resp;
                    }
                    method.invoke(bean, params);
                }
            }
            String viewName = rv.toString();
            //处理重定向
            if (viewName.startsWith("redirect:")) {
                RequestMapping classMapping = bean.getClass().getAnnotation(RequestMapping.class);
                String classPath = "";
                String redirectPath = "";
                if (classMapping != null && !classPath.contains("/")) {
                    classPath = classMapping.value();
                    redirectPath = contextPath + "/" + classPath + "/" + viewName.substring("redirect:".length());
                } else {
                    redirectPath = contextPath + classPath + "/" + viewName.substring("redirect:".length());
                }
                resp.sendRedirect(redirectPath);
                return;
            }
            //处理转发
            String forward = "/WEB-INF/" + viewName + ".jsp";
            req.getRequestDispatcher(forward).forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("系统繁忙，请稍后重试！");
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    public void destroy() {

    }
}
