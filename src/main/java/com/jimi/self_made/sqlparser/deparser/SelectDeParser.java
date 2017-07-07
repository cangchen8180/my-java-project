package com.jimi.self_made.sqlparser.deparser;

import java.util.regex.Pattern;

import com.jimi.self_made.sqlparser.statement.Statement;

/**
 * select语句解析器
 * 实例：
 * <tt>select * from tb_leads_00 where id = 1;</tt>
 * <tt>select * from tb_leads_00;</tt>
 * Created by lixinjian on 17/6/27.
 */
public class SelectDeParser extends AbstractSQLDeParser {

    private static final String OPERATE_TYPE = "select";

    private static final String SQL_REGEX =
            "[s|S][e|E][l|L][e|E][c|C][t|T]\\s.+[f|F][r|R][o|O][m|M]\\s*([0-9a-zA-Z_`]+)"
                    + "(\\s*[w|W][h|H][e|E][r|R][e|E]\\s*(.*))*(;)*";
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
        SelectDeParser selectParser = new SelectDeParser();
        Statement statement =
                selectParser.parse("select * from tb_leads_00 where id = 1;");
        System.out.println("statement.getTableName() = " + statement.getTableName());
        Statement statement1 =
                selectParser.parse("select * FROM tb_leads_00;");
        System.out.println("statement1.getTableName() = " + statement1.getTableName());
    }

}
