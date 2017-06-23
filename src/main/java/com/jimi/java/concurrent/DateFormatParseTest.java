package com.jimi.java.concurrent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 测试SimpleDateFormat并发问题
 *
 * 详细分析参考：
 *  http://www.cnblogs.com/peida/archive/2013/05/31/3070790.html
 *  http://tech.lede.com/2017/04/28/rd/server/SimpleDateFormatConcurrentDanger/
 * Created by lixinjian on 17/6/23.
 */
public class DateFormatParseTest extends Thread {
    /*
    SimpleDateFormat在并发场景下存在问题，因为parse()和format()都是线程不安全的。
    所以，若干线程做日期转换操作，得到的结果可能并不准确，而且可能报错。
    原因：SimpleDateFormat内部使用了成员变量calendar传递参数，导致并发情况下，calendar被不同线程修改。
     */
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /*
    解决方案：使用joda time，不用额外代码，内部完美解决并发问题。
     */
    private static  DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");


    private String name;
    private String dateStr;

    public DateFormatParseTest(String name, String dateStr) {
        this.name = name;
        this.dateStr = dateStr;
    }

    @Override
    public void run() {

        Date date = null;

        // SimpleDateFormat方式
        try {
            date = sdf.parse(dateStr);
            String s = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // joda time方式
        date = DateTime.parse(dateStr, dateTimeFormatter).toDate();

        System.out.println(name + " : date: " + date);
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();

        executor.execute(new DateFormatParseTest("Test_A", "2000-04-28"));
        executor.execute(new DateFormatParseTest("Test_B", "2017-04-28"));

        executor.shutdown();
    }
}
