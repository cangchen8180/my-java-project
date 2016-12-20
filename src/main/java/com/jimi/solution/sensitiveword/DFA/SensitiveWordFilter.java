package com.jimi.solution.sensitiveword.DFA;

import com.jimi.solution.sensitiveword.Txts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author jimi
 * @description 敏感词检索器
 * @date 2016-03-18 19:14.
 */
public class SensitiveWordFilter {
    private static Logger logger = Logger.getLogger(String.valueOf(SensitiveWordFilter.class));

    /**
     * 从指定位置验证是否存在敏感词
     *
     * @param txt
     * @param index
     * @return
     */
    public static int checkSensitiveWord(String txt, int index) {
        Map sensitiveWordMap = SensitiveWordInit.getSensitiveWordMap();
        if (sensitiveWordMap == null) {
            logger.warning("敏感词库未加载");
            return -1;
        }
        int length = 0;
        boolean flag = false;
        Map<String, Object> keyWordMap = (Map<String, Object>) sensitiveWordMap;
        for (int i = index; i<txt.length(); i++){
            String indexWord = String.valueOf(txt.charAt(i));
            keyWordMap = (Map<String, Object>) keyWordMap.get(indexWord);
            if(keyWordMap != null){
                length++;
                if ("1".equals(keyWordMap.get("isEnd"))){
                    flag = true;
                }
            }else{
                break;
            }
        }
        if (length < 0 || !flag){
            length = 0;
        }

        return length;
    }

    /**
     * 获取指定文本包含的敏感词列表
     * @param txt
     * @return
     */
    public static List<String> getSensitiveWordList(String txt) {
        List<String> checkedSensitiveWordList = new ArrayList<>();
        for (int i = 0; i < txt.length(); i++) {
            int length = checkSensitiveWord(txt, i);
            if (length > 0) {
                checkedSensitiveWordList.add(txt.substring(i, i + length));
                //减1的原因，是因为for会自增
                i = i + length - 1;
            }
        }
        return checkedSensitiveWordList;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        logger.info("待检测文本字数：" + Txts.content.length());
        List<String> sensitiveWordList = SensitiveWordFilter.getSensitiveWordList(Txts.content);
        if (sensitiveWordList != null && sensitiveWordList.size() > 0) {
            logger.info("包含的敏感词：" + sensitiveWordList.toString());
        }else{
            logger.info("不包含规定的敏感词");
        }
        logger.info("用时：" + (System.currentTimeMillis() - startTime) + "ms");

    }
}
