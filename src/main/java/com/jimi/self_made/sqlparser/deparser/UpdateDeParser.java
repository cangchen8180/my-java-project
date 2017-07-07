package com.jimi.self_made.sqlparser.deparser;

import java.util.regex.Pattern;

import com.jimi.self_made.sqlparser.statement.Statement;

/**
 * update语句解析器
 * 实例：
 * <tt>update tb_general_dict set name = 'neo' where id = 1;</tt>
 * Created by lixinjian on 17/6/27.
 */
public class UpdateDeParser extends AbstractSQLDeParser {

    private static final String OPERATE_TYPE = "update";

    private static final String SQL_REGEX = "[u|U][p|P][d|D][a|A][t|T][e|E]\\s*(.+)\\s*[s|S][e|E][t|T]\\s.*(;)*";
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
        UpdateDeParser updateParser = new UpdateDeParser();
        Statement statement =
                updateParser.parse("update tb_general_dict SET name = 'neo' where id = 1;");
        System.out.println("statement.getTableName() = " + statement.getTableName());
        System.out.println("statement.sql = " + statement.getContent());
    }

}
