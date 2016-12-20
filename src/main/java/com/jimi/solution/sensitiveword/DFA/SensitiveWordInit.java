package com.jimi.solution.sensitiveword.DFA;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author jimi
 * @description 敏感词加载器
 * @date 2016-03-18 19:28.
 */
public class SensitiveWordInit {

    private static Logger logger = Logger.getLogger(String.valueOf(SensitiveWordInit.class));
    private final static String ENCODING = "gbk";
    private final static String FILE_PATH = "/home/jimi/lagou/0-敏感词过滤/敏感词库/2012年最新敏感词列表/论坛需要过滤的不良词语大全.txt";

    private static Map<String, Object> sensitiveWordMap;

    public static Map<String, Object> getSensitiveWordMap() {
        if (sensitiveWordMap == null){
            initKeyWord();
        }
        return sensitiveWordMap;
    }

    /**
     * 初始化词库
     * @return
     */
    public static Map initKeyWord(){
        try {
            //读取敏感词库
            Set<String> keyWordSet = loadSensitiveWords2Set(FILE_PATH);
            //将敏感词库加入到HashMap中
            addSensitiveWordToHashMap(keyWordSet);
            //spring获取application，然后application.setAttribute("sensitiveWordMap",sensitiveWordMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sensitiveWordMap;
    }

    /**
     * 读取敏感词库，将敏感词放入HashSet中，构建一个DFA算法模型：<br>
     * 中 = {
     *      isEnd = 0
     *      国 = {
     *          isEnd = 1
     *           人 = {isEnd = 0
     *                民 = {isEnd = 1}
     *                }
     *           男  = {
     *           	   isEnd = 0
     *           		人 = {
     *           			 isEnd = 1
     *           			}
     *           	}
     *           }
     *      }
     *  五 = {
     *      isEnd = 0
     *      星 = {
     *      	isEnd = 0
     *      	红 = {
     *              isEnd = 0
     *              旗 = {
     *                   isEnd = 1
     *                  }
     *              }
     *      	}
     *      }
     * @author jimi
     * @param keyWordSet  敏感词库
     */
    public static Map<String, Object> addSensitiveWordToHashMap(Set<String> keyWordSet) {
        if (keyWordSet == null || keyWordSet.size() == 0){
            return null;
        }
        //减少扩容操作
        sensitiveWordMap = new HashMap<>(keyWordSet.size());
        Map<String, Object> nowMap = null;
        Map<String, Object> newWordMap = null;
        for (String keyWord : keyWordSet){
            //todo:sensitiveWordMap的值从哪里来？？？
            nowMap = sensitiveWordMap;
            for (int i = 0; i < keyWord.length(); i++) {
                char keyChar = keyWord.charAt(i);
                //因为下面是将字符转字符串对象存为key的，所以取时也要用字符串类型取，否则取不到
                Object wordMap = nowMap.get(String.valueOf(keyChar));
                if (wordMap == null){
                    newWordMap = new HashMap<>();
                    newWordMap.put("isEnd", "0");
                    //此时是将值也赋给了sensitiveWordMap
                    nowMap.put(String.valueOf(keyChar), newWordMap);
                    nowMap = newWordMap;
                }else{
                    nowMap = (Map<String, Object>) wordMap;
                }

                if (i == keyWord.length() - 1){
                    nowMap.put("isEnd", "1");
                }
            }
        }

        return null;
    }

    /**
     * 加载指定敏感词库
     * @return 敏感词set
     */
    public static Set<String> loadSensitiveWords2Set(String filePath) {
        HashSet<String> sensitiveWordSet = new HashSet<>();

        File file = new File(filePath);
        if (!file.exists()) {
            logger.warning("[词库初始化]敏感词库文件不存在");
            return sensitiveWordSet;
        }
        if (!file.canRead()) {
            logger.info("[词库初始化]敏感词库文件不可读");
            return sensitiveWordSet;
        }

        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(file), ENCODING);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                String keyWord = str.split("=")[0];
                sensitiveWordSet.add(keyWord);
            }
            logger.info("[词库初始化]共获得敏感词数=" + sensitiveWordSet.size());
            return sensitiveWordSet;
        } catch (UnsupportedEncodingException e) {
            logger.warning("[词库初始化]加载敏感词异常");
        } catch (FileNotFoundException e) {
            logger.warning("[词库初始化]加载敏感词异常");
        } catch (IOException e) {
            logger.warning("[词库初始化]加载敏感词异常");
        } finally {
            try {
                inputStreamReader.close();
                logger.info("[词库初始化]inputStreamReader关闭");
            } catch (IOException e) {
                logger.warning("[词库初始化]inputStreamReader关闭异常");
            }
        }
        return sensitiveWordSet;
    }




    public static void main(String[] args) {
        Map keyWordMap = SensitiveWordInit.initKeyWord();
        logger.info("敏感词数=" + keyWordMap.toString());
    }
}
