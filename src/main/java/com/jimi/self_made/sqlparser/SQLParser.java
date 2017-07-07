package com.jimi.self_made.sqlparser;

import org.apache.commons.lang.StringUtils;

import com.jimi.self_made.sqlparser.deparser.AlterDeParser;
import com.jimi.self_made.sqlparser.deparser.CreateDeParser;
import com.jimi.self_made.sqlparser.deparser.DeleteDeParser;
import com.jimi.self_made.sqlparser.deparser.DropDeParser;
import com.jimi.self_made.sqlparser.deparser.InsertDeParser;
import com.jimi.self_made.sqlparser.deparser.SQLDeParser;
import com.jimi.self_made.sqlparser.deparser.SelectDeParser;
import com.jimi.self_made.sqlparser.deparser.TruncateDeParser;
import com.jimi.self_made.sqlparser.deparser.UpdateDeParser;
import com.jimi.self_made.sqlparser.exception.SQLParserException;
import com.jimi.self_made.sqlparser.statement.Statement;

/**
 * sql处理器
 *
 * 为了避免并发问题，保持处理器是无状态的
 *
 * Created by lixinjian on 17/6/27.
 */
public class SQLParser {

    /**
     * 初始化支持的SQL解析器
     */
    private static final SQLDeParser[] parsers =
            new SQLDeParser[] {
                    new AlterDeParser(),
                    new CreateDeParser(),
                    new DeleteDeParser(),
                    new DropDeParser(),
                    new InsertDeParser(),
                    new SelectDeParser(),
                    new TruncateDeParser(),
                    new UpdateDeParser()
            };

    /**
     * 解析sql语句
     *
     * @param sql sql语句
     *
     * @return 语句实体，具体内容参{@linkplain Statement}
     */
    public static Statement parse(String sql) {

        // TODO: 17/6/27 22:48 格式化数据，使得对输入sql格式要求更低
        // TODO: 17/6/30 21:39 比如清理开头的空格等操作，应该放在这个位置

        if (StringUtils.isBlank(sql)) {
            return null;
        }

        sql = sql.trim();

        for (SQLDeParser parser : parsers) {
            if (parser.canParse(sql)) {
                return parser.parse(sql);
            }
        }

        throw new SQLParserException("can not parse, sql=" + sql);
    }

    /**
     * 获取sql中表名，辅助方法
     *
     * @param sql sql语句
     *
     * @return 表名
     */
    public static String getTableName(String sql) {
        Statement statement = parse(sql);
        if (statement == null) {
            return null;
        }

        return statement.getTableName();
    }

    public static void main(String[] args) {
        String tableName =
                SQLParser.getTableName("insert into ccc (`id`, `name`,`password`)valuse(1,'neo','password');");
        System.out.println("tableName = " + tableName);

        String tableName1 = SQLParser.getTableName("select * from tb_leads_00;");
        System.out.println("tableName1 = " + tableName1);

        String tableName2 = SQLParser.getTableName("alter table tb_general_dict add index "
                + "`index_value_categoryCode` (`value`, `category_code`);");
        System.out.println("tableName2 = " + tableName2);

        String tableName3 = SQLParser.getTableName("delete from tb_general_dict where id = 1 ;");
        System.out.println("tableName3 = " + tableName3);

        String tableName4 = SQLParser.getTableName("drop table `tb_leads_00`;");
        System.out.println("tableName4 = " + tableName4);

        String tableName5 = SQLParser.getTableName("    update tb_general_dict set name = 'neo' where id = 1;");
        System.out.println("tableName5 = " + tableName5);

        String tableName6 = SQLParser.getTableName("create table `dim_credit_para` (\n"
                + "          `para_type_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '参数类型id',\n"
                + "          `para_type_name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '参数名称',\n"
                + "          `para_value` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '参数值',\n"
                + "          `para_comment` VARCHAR(5000) NOT NULL DEFAULT '' COMMENT '参数备注',\n"
                + "                        PRIMARY KEY (`para_type_id`,`para_value`)\n"
                + "        ) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT '参数表';");
        System.out.println("tableName6 = " + tableName6);

    }
}
