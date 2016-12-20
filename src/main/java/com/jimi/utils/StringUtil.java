package com.jimi.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 验证字符串是否是数值
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    /**
     * 验证是否是邮箱
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String regex = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return match(regex, email);
    }

    /**
     * 验证电话号码
     *
     * @param
     * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isFixedPhone(String fixedPhone) {
        String regex = "^(\\d{3,4}-)?\\d{6,8}$";
        return match(regex, fixedPhone);
    }

    /**
     * 验证输入手机号码
     *
     * @param
     * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isPhone(String phone) {
        String regex = "^[1]+[3,5,7,8]+\\d{9}$";
        return match(regex, phone);
    }

    /**
     * @param regex
     *            正则表达式字符串
     * @param str
     *            要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
