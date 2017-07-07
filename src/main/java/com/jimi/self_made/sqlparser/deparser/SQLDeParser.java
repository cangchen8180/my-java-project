package com.jimi.self_made.sqlparser.deparser;

import com.jimi.self_made.sqlparser.statement.Statement;

/**
 * sql解析器，基类
 * Created by lixinjian on 17/6/27.
 */
public interface SQLDeParser {

    /**
     * 是否能解析
     *
     * @param sql
     *
     * @return <tt>true</tt>能
     */
    boolean canParse(String sql);

    /**
     * 解析
     *
     * @param sql
     *
     * @return 语句实体，具体内容参{@linkplain Statement}
     */
    Statement parse(String sql);
}
