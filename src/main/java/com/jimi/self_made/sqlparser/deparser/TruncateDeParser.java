package com.jimi.self_made.sqlparser.deparser;

import java.util.regex.Pattern;

import com.jimi.self_made.sqlparser.statement.Statement;

/**
 * truncate语句解析器
 * 实例：
 * <tt>truncate table `tb_leads_04`;</tt>
 * Created by lixinjian on 17/6/27.
 */
public class TruncateDeParser extends AbstractSQLDeParser {

    private static final String OPERATE_TYPE = "truncate";

    private static final String SQL_REGEX =
            "[t|T][r|R][u|U][n|N][c|C][a|A][t|T][e|E]\\s*[t|T][a|A][b|B][l|L][e|E]\\s*([0-9a-zA-Z_`]+)\\s*(;)*";
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
        TruncateDeParser truncateParser = new TruncateDeParser();
        Statement statement = truncateParser.parse("truncate table `tb_business_info_07`");
        System.out.println("statement.getTableName() = " + statement.getTableName());
    }
}
