package com.jimi.self_made.sqlparser.deparser;

import java.util.regex.Pattern;

import com.jimi.self_made.sqlparser.statement.Statement;

/**
 * create语句解析器
 * 实例：
 * <tt>create table `dim_credit_para` (
 * `para_type_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '参数类型id',
 * `para_type_name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '参数名称',
 * `para_value` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '参数值',
 * `para_comment` VARCHAR(5000) NOT NULL DEFAULT '' COMMENT '参数备注',
 * PRIMARY KEY (`para_type_id`,`para_value`)
 * ) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT '参数表';</tt>
 * Created by lixinjian on 17/6/27.
 */
public class CreateDeParser extends AbstractSQLDeParser {

    private static final String OPERATE_TYPE = "create";

    private static final String SQL_REGEX =
            "[c|C][r|R][e|E][a|A][t|T][e|E]\\s*[t|T][a|A][b|B][l|L][e|E]\\s*([0-9a-zA-Z_`]+)\\s*.*\\(\\s*[.\\n]*(;)*";
    private static final Pattern SQL_PATTERN = Pattern.compile(SQL_REGEX);

    @Override
    protected Pattern getPattern() {
        return SQL_PATTERN;
    }

    @Override
    protected String getOperateType() {
        return OPERATE_TYPE;
    }

    public static void main(String[] args) {
        CreateDeParser createParser = new CreateDeParser();
        Statement statement =
                createParser.parse("CREATE TABLE `dim_credit_para` (\n"
                        + "          `para_type_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '参数类型id',\n"
                        + "          `para_type_name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '参数名称',\n"
                        + "          `para_value` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '参数值',\n"
                        + "          `para_comment` VARCHAR(5000) NOT NULL DEFAULT '' COMMENT '参数备注',\n"
                        + "                        PRIMARY KEY (`para_type_id`,`para_value`)\n"
                        + "        ) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT '参数表'");
        System.out.println("statement.getTableName() = " + statement.getTableName());
    }

}
