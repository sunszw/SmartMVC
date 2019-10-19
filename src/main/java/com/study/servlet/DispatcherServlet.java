package com.study.servlet;

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
        SAXReader saxReader = new SAXReader();
        String fileName=getInitParameter("configLocation");
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        try {
            Document document = saxReader.read(inputStream);
            Element root = document.getRootElement();
            List<Element> elements = root.elements();

            //beans用于存放控制器实例
            List<Object> beans = new ArrayList<>();
            for (Element element : elements) {
                String className = element.attributeValue("class");
                //将控制器实例化
                Object object = Class.forName(className).newInstance();
                beans.add(object);
            }
            //将控制器实例交给Handlermapping处理
            handlerMapping = new HandlerMapping();
            handlerMapping.process(beans);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获得请求资源路径
        String uri = req.getRequestURI();
        //截取请求资源路径
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());
        //调用HandlerMapping对象的方法，获得对应的Handler对象
        Handler handler = handlerMapping.getHandler(path);
        if (handler == null) {
            resp.sendError(404);
            return;
        }
        //调用控制器的方法
        Object object = handler.getObject();
        Method method = handler.getMethod();
        Object rv = null;
        try {
            rv = method.invoke(object);
            String viewName = rv.toString();
            //处理重定向
            if (viewName.startsWith("redirect:")) {
                String redirectPath = contextPath + "/" + viewName.substring("redirect:".length());
                resp.sendRedirect(redirectPath);
                return;
            }
            //处理转发
            String forwardPath = "/WEB-INF/" + viewName + ".jsp";
            req.getRequestDispatcher(forwardPath).forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    public void destroy() {

    }
}