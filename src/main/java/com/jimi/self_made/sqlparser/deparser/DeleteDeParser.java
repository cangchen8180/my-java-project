package com.jimi.self_made.sqlparser.deparser;

import java.util.regex.Pattern;

import com.jimi.self_made.sqlparser.statement.Statement;

/**
 * delete语句解析器
 * 实例：
 * <tt>delete from tb_general_dict where id = 1 ;</tt>
 * Created by lixinjian on 17/6/27.
 */
public class DeleteDeParser extends AbstractSQLDeParser {

    private static final String OPERATE_TYPE = "delete";

    private static final String SQL_REGEX =
            "[d|D][e|E][l|L][e|E][t|T][e|E]\\s*[f|F][r|R][o|O][m|M]\\s*([0-9a-zA-Z_`]+)\\s*.*(;)*";
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
        DeleteDeParser deleteParser = new DeleteDeParser();
        Statement statement =
                deleteParser.parse("delete from tb_general_dict where id = 1 ;");
        System.out.println("statement.getTableName() = " + statement.getTableName());
    }

}
