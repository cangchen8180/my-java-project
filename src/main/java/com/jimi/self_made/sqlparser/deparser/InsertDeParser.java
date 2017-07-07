package com.jimi.self_made.sqlparser.deparser;

import java.util.regex.Pattern;

import com.jimi.self_made.sqlparser.statement.Statement;

/**
 * insert语句解析器
 * 实例：
 * <tt>insert into ccc (`id`, `name`,`password`)valuse(1,'neo','password');</tt>
 * <tt>insert into ccc valuse(1,'neo','password');</tt>
 * Created by lixinjian on 17/6/27.
 */
public class InsertDeParser extends AbstractSQLDeParser {

    private static final String OPERATE_TYPE = "insert";

    private static final String SQL_REGEX =
            "[i|I][n|N][s|S][e|E][r|R][t|T]\\s*[i|I][n|N][t|T][o|O]\\s*([0-9a-zA-Z_`]+)\\s*.*(;)*";
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
        InsertDeParser insertParser = new InsertDeParser();
        Statement statement =
                insertParser.parse("insert into  ccc (`id`, `name`,`password`)valuse(1,'neo','password');");
        System.out.println("statement.getTableName() = " + statement.getTableName());

        Statement statement1 =
                insertParser.parse("insert into ccc valuse(1,'neo','password');");
        System.out.println("statement1.getTableName() = " + statement1.getTableName());
    }

}
