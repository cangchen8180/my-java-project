/*  
 * Amount2RMB.java 2008-6-15   
 */  
package com.jimi.utils;   
  
import java.util.regex.Matcher;   
import java.util.regex.Pattern;   
  
public class Amount2RMB {   
    private static final Pattern AMOUNT_PATTERN =    
            Pattern.compile("^(0|[1-9]\\d{0,11})\\.(\\d\\d)$"); // 不考虑分隔符的正确性   
    private static final char[] RMB_NUMS = "零壹贰叁肆伍陆柒捌玖".toCharArray();   
    private static final String[] UNITS = {"元", "角", "分", "整"};   
    private static final String[] U1 = {"", "拾", "佰", "仟"};   
    private static final String[] U2 = {"", "万", "亿"};   
  
    /**  
     * 将金额（整数部分等于或少于12位，小数部分2位）转换为中文大写形式.  
     * @param amount 金额数字  
     * @return       中文大写  
     * @throws IllegalArgumentException  
     */  
    public static String convert(String amount) throws IllegalArgumentException {   
        // 去掉分隔符   
        amount = amount.replace(",", "");   
  
        // 验证金额正确性   
        if (amount.equals("0.00")) {   
            throw new IllegalArgumentException("金额不能为零.");   
        }   
        Matcher matcher = AMOUNT_PATTERN.matcher(amount);   
        if (! matcher.find()) {   
            throw new IllegalArgumentException("输入金额有误.");   
        }   
  
        String integer  = matcher.group(1); // 整数部分   
        String fraction = matcher.group(2); // 小数部分   
  
        String result = "";   
        if (! integer.equals("0")) {   
            result += integer2rmb(integer) + UNITS[0]; // 整数部分   
        }   
        if (fraction.equals("00")) {   
            result += UNITS[3]; // 添加[整]   
        } else if (fraction.startsWith("0") && integer.equals("0")) {   
            result += fraction2rmb(fraction).substring(1); // 去掉分前面的[零]   
        } else {   
            result += fraction2rmb(fraction); // 小数部分   
        }   
  
        return result;   
    }   
  
    // 将金额小数部分转换为中文大写   
    private static String fraction2rmb(String fraction) {   
        char jiao = fraction.charAt(0); // 角   
        char fen  = fraction.charAt(1); // 分   
        return (RMB_NUMS[jiao - '0'] + (jiao > '0' ? UNITS[1] : ""))   
                + (fen > '0' ? RMB_NUMS[fen - '0'] + UNITS[2] : "");   
    }   
  
    // 将金额整数部分转换为中文大写   
    private static String integer2rmb(String integer) {   
        StringBuilder buffer = new StringBuilder();   
        // 从个位数开始转换   
        int i, j;   
        for (i = integer.length() - 1, j = 0; i >= 0; i--, j++) {   
            char n = integer.charAt(i);   
            if (n == '0') {   
                // 当n是0且n的右边一位不是0时，插入[零]   
                if (i < integer.length() - 1 && integer.charAt(i + 1) != '0') {   
                    buffer.append(RMB_NUMS[0]);   
                }   
                // 插入[万]或者[亿]   
                if (j % 4 == 0) {   
                    if (i > 0 && integer.charAt(i - 1) != '0'  
                            || i > 1 && integer.charAt(i - 2) != '0'  
                            || i > 2 && integer.charAt(i - 3) != '0') {   
                        buffer.append(U2[j / 4]);   
                    }   
                }   
            } else {   
                if (j % 4 == 0) {   
                    buffer.append(U2[j / 4]);     // 插入[万]或者[亿]   
                }   
                buffer.append(U1[j % 4]);         // 插入[拾]、[佰]或[仟]   
                buffer.append(RMB_NUMS[n - '0']); // 插入数字   
            }   
        }   
        return buffer.reverse().toString();   
    }   
  
    public static void main(String[] args) {   
        // log4j?   
        System.out.println("壹万陆仟肆佰零玖元零贰分".equals(convert("16,409.02")));   
        System.out.println("壹仟肆佰零玖元伍角".equals(convert("1,409.50")));   
        System.out.println("陆仟零柒元壹角肆分".equals(convert("6,007.14")));   
        System.out.println("壹仟陆佰捌拾元叁角贰分".equals(convert("1,680.32")));   
        System.out.println("叁佰贰拾伍元零肆分".equals(convert("325.04")));   
        System.out.println("肆仟叁佰贰拾壹元整".equals(convert("4,321.00")));   
        System.out.println("壹分".equals(convert("0.01")));   
           
        System.out.println(convert("1234,5678,9012.34")   
                .equals("壹仟贰佰叁拾肆亿伍仟陆佰柒拾捌万玖仟零壹拾贰元叁角肆分"));   
        System.out.println(convert("1000,1000,1000.10")   
                .equals("壹仟亿零壹仟万零壹仟元壹角"));   
        System.out.println(convert("9009,9009,9009.99")   
                .equals("玖仟零玖亿玖仟零玖万玖仟零玖元玖角玖分"));   
        System.out.println(convert("5432,0001,0001.01")   
                .equals("伍仟肆佰叁拾贰亿零壹万零壹元零壹分"));   
        System.out.println(convert("1000,0000,1110.00")   
                .equals("壹仟亿零壹仟壹佰壹拾元整"));   
        System.out.println(convert("1010,0000,0001.11")   
                .equals("壹仟零壹拾亿零壹元壹角壹分"));   
        System.out.println(convert("1000,0000,0000.01")   
                .equals("壹仟亿元零壹分"));   
    }   
}  
