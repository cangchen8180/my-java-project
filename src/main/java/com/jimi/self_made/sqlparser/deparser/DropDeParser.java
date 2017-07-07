package com.jimi.self_made.sqlparser.deparser;

import java.util.regex.Pattern;

import com.jimi.self_made.sqlparser.statement.Statement;

/**
 * drop语句解析器
 * 实例：
 * <tt>drop table `tb_leads_00`;</tt>
 * Created by lixinjian on 17/6/27.
 */
public class DropDeParser extends AbstractSQLDeParser {

    private static final String OPERATE_TYPE = "drop";

    private static final String SQL_REGEX =
            "[d|D][r|R][o|O][p|P]\\s*[t|T][a|A][b|B][l|L][e|E]\\s*([0-9a-zA-Z_`]+)\\s*.*(;)*";
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
        DropDeParser dropParser = new DropDeParser();
        Statement statement =
                dropParser.parse("drop TABLE `tb_leads_00`;");
        System.out.println("statement.getTableName() = " + statement.getTableName());
    }

}
