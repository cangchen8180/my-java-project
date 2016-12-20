package com.jimi.java.fundamental;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * @author jimi
 * @description
 * @date 2016-03-18 17:28.
 */
public class ReflectDemo {

    private static Logger logger = Logger.getLogger(String.valueOf(ReflectDemo.class));

    public static void main(String[] args) {
        /*try {
            //类加载
            /////////////////////////类加载////////////////////////
            Class clazz = Class.forName("com.jimi.java.fundamental.PlatEntity");
            logger.info("[reflect]完整类名：" + clazz.getName());
            logger.info("[reflect]类名：" + clazz.getSimpleName());
            /////////////////////////属性////////////////////////
            StringBuilder sb = new StringBuilder();
            Field[] fields = clazz.getDeclaredFields();
            if (fields != null) {
                for (Field field : fields) {
                    sb.append("field:name=" + field.getName() + ", type=" + field.getType() + ";");
                }
            }
            logger.info("[reflect]类属性：" + sb.toString());
            /////////////////////////方法////////////////////////
            Method[] methods = clazz.getMethods();
            if (methods != null) {
                for (Method method : methods) {
                    String methodName = method.getName();
                    if (methodName.startsWith("set") || methodName.startsWith("get")) {
                        logger.info("[reflect]类方法：" + "method:name=" + method.getName() + ", return param=" + method.getGenericReturnType());
                    }
                }
            }
            /////////////////////////类加载////////////////////////
            *//**
             * 通过Java反射机制得到类加载器信息
             * 在java中有三种类类加载器。[这段资料网上截取]
             1）Bootstrap ClassLoader 此加载器采用c++编写，一般开发中很少见。
             2）Extension ClassLoader 用来进行扩展类的加载，一般对应的是jre\lib\ext目录中的类
             (***)3）AppClassLoader 加载classpath指定的类，是最常用的加载器。同时也是java中默认的加载器。
             *//*
            String classLoadName = clazz.getClassLoader().getClass().getName();
            logger.info("[reflect]类加载器：" + classLoadName);

            /////////////////////////类实例化////////////////////////
            try {
                PlatEntity platEntity = (PlatEntity) clazz.newInstance();
                platEntity.setId(123);
                platEntity.setName("entity_name");
                platEntity.setType("test");
                logger.info("[reflect]类实例：platEntity=" + platEntity.toString());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/

        //////////////////////通过反射，根据变量名获取变量的值/////////////////////////

        PlatEntity platEntity = new PlatEntity();
        platEntity.setName("测试-name");
        platEntity.setId(123);

        try {
            /*
            getDeclaredField是可以获取一个类的所有字段.
            getField只能获取类的public 字段.
             */
            Field nameField = platEntity.getClass().getDeclaredField("name");
            //设置private字段为可访问
            if (!nameField.isAccessible()){
                nameField.setAccessible(true);
            }

            //获取字段类型
            /*Class<?> type = nameField.getType();
            logger.info("name type={}" + type.getName());*/
            Object nameValue = nameField.get(platEntity);
            logger.info("name value=" + nameValue);

            //强制转换类
            /*Object cast = type.cast(nameValue);
            logger.info("cast class=" + cast.getClass());*/
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
