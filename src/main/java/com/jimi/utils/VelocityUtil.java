package com.jimi.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author jimi
 * @version 2016-05-03 11:46.
 */
public class VelocityUtil {

    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * 使用velocity合并模板和参数
     * @param tplHtml 模板文件相对路径
     * @param paramMap 参数map
     * @return
     */
    public static final String merge(String basePath, String tplHtml, Map<String, String> paramMap){

        String encoding = VelocityUtil.getDefaultEncoding();

        return merge(basePath, tplHtml, paramMap, encoding);
    }

    /**
     * 使用velocity合并模板和参数
     * @param tplHtml   模板文件
     * @param paramMap  参数map
     * @param encoding 指定编码
     * @return
     */
    public static final String merge(String basePath, String tplHtml, Map<String, String> paramMap, String encoding){

        //参数验证
        if (StringUtils.isBlank(tplHtml)){
            throw new IllegalArgumentException("[VelocityUtil]tplHtml不能为空！");
        }
        if (!(new File(basePath + tplHtml)).exists()) {
            throw new IllegalArgumentException("[VelocityUtil]tplHtml文件不存在!");
        }
        if (StringUtils.isBlank(encoding)){
            encoding = VelocityUtil.getDefaultEncoding();
        }

        //引擎初始化
        VelocityEngine ve = new VelocityEngine();
        Properties properties = new Properties();
        //不设置basePath，即使全路径正确ve.getTemplate也会报错。
        properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, basePath);
        properties.setProperty(Velocity.INPUT_ENCODING, encoding);
        properties.setProperty(Velocity.OUTPUT_ENCODING, encoding);
        ve.init(properties);

        //获取模板
        Template t = ve.getTemplate(tplHtml);

        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();
        //把数据填入上下文
        if (!CollectionUtils.isEmpty(paramMap)){
            Set<String> keySet = paramMap.keySet();
            for (String key : keySet){
                context.put(key, paramMap.get(key));
            }
        }

        //为后面的展示，提前输入List数值
        StringWriter writer = new StringWriter();
        //转换输出
        t.merge(context, writer);

        return writer.toString();
    }

    /**
     * 获取默认使用的编码
     * @return
     */
    public static final String getDefaultEncoding(){

        return VelocityUtil.DEFAULT_ENCODING;
    }
}
