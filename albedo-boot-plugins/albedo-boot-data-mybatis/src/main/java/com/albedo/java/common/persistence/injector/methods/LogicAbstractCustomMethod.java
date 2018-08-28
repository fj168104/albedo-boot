package com.albedo.java.common.persistence.injector.methods;

import com.albedo.java.common.persistence.injector.SqlInjectorUtil;
import com.albedo.java.util.PublicUtil;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.injector.LogicAbstractMethod;

import java.util.Iterator;
import java.util.List;

public abstract class LogicAbstractCustomMethod extends LogicAbstractMethod {

    @Override
    protected String sqlWordConvert(String column) {
        return SqlInjectorUtil.sqlWordConvert(configuration, column);
    }


    protected String sqlWhereEntityWrapper(TableInfo table, String columnPrefix) {
        StringBuilder where = new StringBuilder(128);
        where.append("<choose><when test=\"ew!=null and !ew.emptyOfWhere\">");
        where.append("<trim prefix=\"WHERE\" prefixOverrides=\"AND|OR\">");
        where.append("<if test=\"ew.entity!=null\">");
        if (StringUtils.isNotEmpty(table.getKeyProperty())) {
            where.append("\n<if test=\"ew.entity.").append(table.getKeyProperty()).append("!=null\">\n");
            if(PublicUtil.isNotEmpty(columnPrefix)){
                where.append(columnPrefix).append(".");
            }
            where.append(table.getKeyColumn()).append("=#{ew.entity.").append(table.getKeyProperty()).append("}");
            where.append("\n</if>");
        }

        List<TableFieldInfo> fieldList = table.getFieldList();
        Iterator var4 = fieldList.iterator();

        while(var4.hasNext()) {
            TableFieldInfo fieldInfo = (TableFieldInfo)var4.next();
            where.append(this.convertIfTag(fieldInfo, "ew.entity.", false));
            where.append(" AND ").append(this.sqlCondition(fieldInfo.getCondition(), fieldInfo.getColumn(), "ew.entity." + fieldInfo.getEl()));
            where.append(this.convertIfTag(fieldInfo, true));
        }

        where.append("</if>");
        where.append(this.getLogicDeleteSql(true, table, columnPrefix));
        where.append("<if test=\"ew.sqlSegment!=null and ew.sqlSegment!=''\"> AND ${ew.sqlSegment}</if>");
        where.append("</trim>");
        where.append("</when><otherwise>WHERE ");
        where.append(this.getLogicDeleteSql(false, table, columnPrefix));
        where.append("</otherwise></choose>");
        return where.toString();
    }

    /**
     * <p>
     * SQL 更新 set 语句
     * </p>
     *
     * @param table 表信息
     * @return sql and 片段
     */
    public String getLogicDeleteSql(boolean startWithAnd, TableInfo table, String columnPrefix) {
        StringBuilder sql = new StringBuilder();
        List<TableFieldInfo> fieldList = table.getFieldList();
        Iterator var5 = fieldList.iterator();

        while(var5.hasNext()) {
            TableFieldInfo fieldInfo = (TableFieldInfo)var5.next();
            if (fieldInfo.isLogicDelete()) {
                if (startWithAnd) {
                    sql.append(" AND ");
                }
                if(PublicUtil.isNotEmpty(columnPrefix)){
                    sql.append(columnPrefix).append(".");
                }
                sql.append(fieldInfo.getColumn()).append("!=");
                if (StringUtils.isCharSequence(fieldInfo.getPropertyType())) {
                    sql.append("'").append(fieldInfo.getLogicDeleteValue()).append("'");
                } else {
                    sql.append(fieldInfo.getLogicDeleteValue());
                }
            }
        }

        return sql.toString();
    }
}
