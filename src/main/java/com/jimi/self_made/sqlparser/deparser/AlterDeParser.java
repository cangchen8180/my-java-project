package com.jimi.self_made.sqlparser.deparser;

import java.util.regex.Pattern;

import com.jimi.self_made.sqlparser.statement.Statement;

/**
 * alter语句解析器
 * 实例：
 * <tt>alter table tb_general_dict add index `index_value_categoryCode` (`value`, `category_code`);</tt>
 * Created by lixinjian on 17/6/27.
 */
public class AlterDeParser extends AbstractSQLDeParser {

    private static final String OPERATE_TYPE = "alter";

    private static final String SQL_REGEX =
            "[a|A][l|L][t|T][e|E][r|R]\\s*[t|T][a|A][b|B][l|L][e|E]\\s*([0-9a-zA-Z_`]+)\\s*.*(;)*";
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
        AlterDeParser deleteParser = new AlterDeParser();
        Statement statement =
                deleteParser
                        .parse("alter TABLE tb_general_dict add index `index_value_categoryCode` (`value`, "
                                + "`category_code`);");
        System.out.println("statement.getTableName() = " + statement.getTableName());
    }

}
