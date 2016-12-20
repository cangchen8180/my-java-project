package com.jimi.velocity;

import com.itextpdf.text.pdf.BaseFont;
import com.jimi.utils.VelocityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jimi
 * @version 2016-04-27 18:09.
 */
@Controller
@RequestMapping("/crm/velocityController")
public class VelocityController {

    @Autowired
    TestVelocity testVelocity;

    @RequestMapping("test")
    public void test(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //pageContext.getServletContext().getRealPath("/")
        //getPath
        ServletContext sc = request.getSession().getServletContext();
        String path = sc.getRealPath(""); //值为D:\apache-tomcat-6.0.26\webapps\createpdf
        System.out.println("原path: " + path);
        //把路径中的反斜杠转成正斜杠
        path = path.replaceAll("\\\\", "/"); //值为D:/apache-tomcat-6.0.26/webapps/createpdf
        System.out.println(path);

        String path2 = sc.getRealPath("/");
        System.out.println("path2: " + path2);

        System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));

        System.out.println("request.getRequestURI: " + request.getRequestURI());
        //获取使用的端口号
        System.out.println(request.getLocalPort());

        String path3 = request.getContextPath();
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path3+"/";

        System.out.println("basepath: " + basePath);


        response.setContentType("application/pdf");

        /*StringBuffer html = new StringBuffer();
        html.append("<!DOCTYPE html>");
        html.append("<html>")
                .append("<head  lang=\"en\">")
                .append("<meta  charset=\"UTF-8\"/>")
                .append("</head>")
                .append("<body>");

        html.append("<h1>dd</h1>");
        html.append("dsfsdfsddfsdfsddf\n" +
                "<p>222222222222</p>");

        html.append("</body></html>");*/

        try {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("name", "Poorboy");
            paramMap.put("date", (new Date()).toString());
            String resultHtml = VelocityUtil.merge(path, "/WEB-INF/layout/test.html", paramMap);

            ITextRenderer renderer = new ITextRenderer();

            /*File file = new File(path + "/WEB-INF/layout/test.html");
            *//*renderer.setDocumentFromString(html.toString());*//*
            renderer.setDocument(file);*/
            renderer.setDocumentFromString(resultHtml);

            // 解决中文支持问题
            ITextFontResolver fontResolver = renderer.getFontResolver();
            fontResolver.addFont(path + "/WEB-INF/layout/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            /*//微软雅黑字体文件
            fontResolver.addFont("C:/Windows/fonts/msyh.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            //微软雅黑加粗后额字体文件
            fontResolver.addFont("C:/Windows/fonts/msyhbd.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);*/

            // 解决图片的相对路径问题
            //renderer.getSharedContext().setBaseURL("file:/C:/Documents and Settings/dashan.yin/workspace/createpdf/WebRoot/images");
            //renderer.getSharedContext().setBaseURL("file:/D:/apache-tomcat-6.0.26/webapps/createpdf/images");
            //renderer.getSharedContext().setBaseURL("file:/" + path + "/images");

            renderer.layout();

            OutputStream os = response.getOutputStream();
            renderer.createPDF(os);

            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
