package com.jimi.es;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jimi.es.entities.CustomerEntity;
import com.jimi.es.services.CustomerIndexService;
import com.jimi.es.utils.EsUtil;
import com.jimi.exception.LgPlatBusinessException;
import com.jimi.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author jimi
 * @description
 * @date 2016-03-22 14:48.
 */
public class ClientTestDemo {
    private static Logger logger = LoggerFactory.getLogger(ClientTestDemo.class.toString());
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ///////////////create index api//////////
        CustomerIndexService customerIndexService = new CustomerIndexService();
        //创建索引
        //boolean flag = customerIndexService.bulkInsertCustomer(3, 4);


        ///////////////query api//////////
         /*List<EsParam> paramList = new ArrayList<>();
        //param1
        EsParam param1 = new EsParam("shortName", "拉勾", EsParamTypeEnum.AND);
        paramList.add(param1);

        try {
            List<String> customerList = EsUtil.queryIndex("crm", "customer", paramList, 0, 100);
            logger.info("result={}", customerList.toString());
        } catch (LgPlatBusinessException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }*/


        ///////////////updateSelective api//////////
        /*Map<String, String> customerData = new HashMap<>();
        //customerData.put("name", null);
        //customerData.put("shortName", "拉勾885");
        customerData.put("registerTime", DateUtil.getNowDateASISO());*/
        CustomerEntity customer = new CustomerEntity();
        customer.setName("666");
        customer.setRegisterTime(null);
        try {
            boolean result = EsUtil.updateIndexSelectiveWithObj("crm", "customer", "32", customer);
        } catch (LgPlatBusinessException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        ///////////////update api//////////
        /*CustomerEntity customer = new CustomerEntity();
        //customer.setName("custome99999");
        customer.setShortName("客户简称1333");
        try {
            EsUtil.createIndex("crm", "customer", "32", gson.toJson(customer));
        } catch (LgPlatBusinessException e) {
            e.printStackTrace();
        }*/

        String customerStr = null;
        try {
            customerStr = EsUtil.queryIndex("crm", "customer", "32");
        } catch (LgPlatBusinessException e) {
            e.printStackTrace();
        }
        CustomerEntity customerEntity = gson.fromJson(customerStr, CustomerEntity.class);
        if (null != customerStr){
            logger.info("查询结果：{}", customerEntity.toString());
        }

        /*String name = "";
        long start = System.currentTimeMillis();
        int count = 1;

        while (!customerName.equals(name)){

            long start_1 = System.currentTimeMillis();
            try {
                String customerStr = EsUtil.queryIndex("crm", "customer", "AVQJmusWytJKTzR8M6IX");
                CustomerEntity customerEntity = gson.fromJson(customerStr, CustomerEntity.class);
                if (null != customerStr){
                    name = customerEntity.getName();
                    logger.info("查询结果：{}", name);
                }
            } catch (LgPlatBusinessException e) {
                logger.error("查询失败，errorMsg={}" + e.getMessage());
            }
            long end_1 = System.currentTimeMillis();
            logger.info("查询索引耗时：{}ms", end_1 - start_1);

            //等待5ms秒查询
            Thread.sleep(5);
            logger.info("查询索引次数：{}", count++);
        }

        long end = System.currentTimeMillis();
        logger.info("完成更新索引耗时：{}ms", end - start);*/


        ///////////////analyzer api//////////
        /*try {
            List<String> tokenList = EsUtil.analyzeTextByAnalyzer("crm", "ik", "拉勾网");
            if (!CollectionUtils.isEmpty(tokenList)){
                for (String token : tokenList){
                    logger.info("分词结果：{}", token);
                }
            }
        } catch (LgPlatBusinessException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }*/


        ///////////////admin api//////////
            //////////get mapping API/////////
        /*try {
            String mapping = EsUtil.EsAdminClient.getMapping("crm", "customer");
            logger.info("mapping={}", mapping);
        } catch (LgPlatBusinessException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

            //////////put mapping API/////////
        try {
            boolean result = EsUtil.EsAdminClient.putMapping("crm", "customer", null);

        } catch (LgPlatBusinessException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }*/
        ///////////////on shutdown//////////
        //node
        /*node.close();*/
    }

}
