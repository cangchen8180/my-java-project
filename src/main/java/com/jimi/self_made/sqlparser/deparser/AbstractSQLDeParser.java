package com.jimi.self_made.sqlparser.deparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.jimi.self_made.sqlparser.exception.SQLParserException;
import com.jimi.self_made.sqlparser.statement.Statement;

/**
 * Created by lixinjian on 17/6/27.
 */
public abstract class AbstractSQLDeParser implements SQLDeParser {

    @Override
    public boolean canParse(String sql) {
        if (StringUtils.isBlank(sql)) {
            return false;
        }

        return sql.startsWith(getOperateType());
    }

    @Override
    public Statement parse(String sql) {
        if (StringUtils.isBlank(sql)) {
            return null;
        }

        //        sql = StringUtils.upperCase(sql);

        Matcher matcher = getPattern().matcher(sql);
        if (!matcher.find()) {
            throw new SQLParserException("sql not matched. parser=" + this.getClass().getSimpleName());
        }

        return buildStatement(sql, matcher);
    }

    /**
     * 正则实例
     *
     * @return
     */
    protected abstract Pattern getPattern();

    /**
     * 语句类型
     *
     * @return
     */
    protected abstract String getOperateType();

    /**
     * 构建语句实体
     *
     * @param sql
     * @param matcher
     *
     * @return
     */
    protected Statement buildStatement(String sql, Matcher matcher) {
        Statement statement = new Statement();
        statement.setOperateType(getOperateType());
        String tableName = matcher.group(1).replace("`", "");
        statement.setTableName(tableName);
        statement.setContent(sql);

        return statement;
    }
}
