package com.jimi.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

/**
 * @author jimi
 * @version 2016-04-27 14:06.
 */
public class TestVelocity {

    public static void main(String[] args) {

        /*URL resource = TestVelocity.class.getClassLoader().getResource("layout/test.html");
        System.out.println("resourcePath=" + resource.getPath());*/

        ///////////////////test 可用///////////////////
        //初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        String basePath = "src/main/webapp/WEB-INF/";
        // 设置模板的路径
        Properties properties = new Properties();
        properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, basePath);
        properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        ve.init(properties);
        //取得velocity的模版
        /*Template t = ve.getTemplate("crm-web/src/hellovelocity.vm");*/
        Template t = ve.getTemplate("layout/test.html");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();
        //把数据填入上下文
        context.put("name", "Poorboy");
        context.put("date", (new Date()).toString());
        //为后面的展示，提前输入List数值
        StringWriter writer = new StringWriter();
        //转换输出
        t.merge(context, writer);
        System.out.println(writer.toString());
    }

}
