package com.jimi.es.services;

import com.jimi.es.constants.Constants;
import com.jimi.es.entities.CustomerEntity;
import com.jimi.es.entities.LinkManEntity;
import com.jimi.es.model.SearchConditionParam;
import com.jimi.es.utils.EsUtil;
import com.jimi.exception.LgPlatBusinessException;
import com.jimi.utils.DateUtil;
import com.jimi.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jimi
 * @version 2016-04-11 14:27.
 */
public class CustomerIndexService {
    private static Logger logger = LoggerFactory.getLogger(CustomerIndexService.class.toString());

    /**
     * 按条件查询客户信息
     * @param param
     * @return
     */
    public List<CustomerEntity> queryCustomerByCondition(SearchConditionParam param){
//        EsUtil.queryIndex()
        return null;
    }

    /**
     * 批量插入客户信息
     * @param startId
     * @param endId
     * @return
     */
    public boolean bulkInsertCustomer(int startId, int endId){
        for (int i = startId; i <= endId; i++) {
            CustomerEntity customer = new CustomerEntity();
            int id = i;
            customer.setId(id);
            customer.setName("business" + id);
            customer.setShortName("拉勾" + id);
            try {
                customer.setRegisterTime(DateUtil.string2dateFormate("2016-04-0" + id + " 10:20:00", "yyyy-MM-dd HH:mm:ss"));
            } catch (ParseException e) {
                logger.warn("格式化注册时间异常，使用当前时间。");
                customer.setRegisterTime(new Date());
            }
            customer.setLinkManList(getLinkManList(id));

            try {
                EsUtil.createIndex(Constants.ES_CRM_INDEX, Constants.ES_CRM_CUSTOMER_TYPE, JsonUtil.toJsonWithGson(customer));
            } catch (LgPlatBusinessException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    private static List<LinkManEntity> getLinkManList(int id) {
        String[] sexs = new String[]{"男", "女"};
        List<LinkManEntity> linkManList = new ArrayList<>();

        for (int i = 0; i < id; i++) {
            LinkManEntity linkMan = new LinkManEntity();
            linkMan.setId(i);
            linkMan.setUserId((i + 1) * 10);
            linkMan.setName("业务人" + i);
            linkMan.setSex(sexs[i % 2]);
            linkMan.setPhone("1362286888" + i);
            linkMan.setFax("011-8888" + i + "888");
            linkManList.add(linkMan);
        }

        return linkManList;
    }
}
