package com.clj.ai.config;

import com.pgvector.PGvector;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PostgreSQL pgvector 类型处理器
 * 保证 VECTOR 列的参数与结果在 MyBatis 中正确转换为 {@link PGvector}
 */
@MappedTypes(PGvector.class)
public class PgVectorTypeHandler extends BaseTypeHandler<PGvector> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PGvector parameter, JdbcType jdbcType)
            throws SQLException {
        // PGvector 继承 PGobject，PG JDBC 驱动自动以 vector 类型发送
        ps.setObject(i, parameter);
    }

    @Override
    public PGvector getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toPGvector(rs.getString(columnName));
    }

    @Override
    public PGvector getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toPGvector(rs.getString(columnIndex));
    }

    @Override
    public PGvector getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toPGvector(cs.getString(columnIndex));
    }

    /**
     * 驱动返回 vector 列的文本形式（如 [0.1,0.2,...]），由 PGvector 解析为向量对象
     */
    private PGvector toPGvector(String value) throws SQLException {
        if (value == null) {
            return null;
        }
        return new PGvector(value);
    }
}
