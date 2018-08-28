package com.albedo.java.common.persistence;

import com.albedo.java.util.PublicUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.Map;

/**
 * @author somewhere
 * @date 2018/03/08
 */
public class PageQuery<T> extends Page<T> {
    private static final String PAGE = "page";
    private static final String LIMIT = "limit";
    private static final String ORDER_BY_FIELD = "orderByField";
    private static final String IS_ASC = "isAsc";

    public PageQuery(Pageable pageable) {

        super(pageable.getPageNumber()
                , pageable.getPageSize());

        if (pageable.getSort()!=null) {
            Iterator<Sort.Order> iterator = pageable.getSort().iterator();
            while (iterator.hasNext()){
                Sort.Order order = iterator.next();
                if(order.getDirection().isAscending()){
                    if(PublicUtil.isEmpty(this.ascs())) {
                        this.setAscs(Lists.newArrayList());
                    }
                    ArrayUtils.add(this.ascs(), order.getProperty());
                }else if(order.getDirection().isDescending()){
                    if(PublicUtil.isEmpty(this.descs())) {
                        this.setDescs(Lists.newArrayList());
                    }
                    ArrayUtils.add(this.descs(), order.getProperty());
                }
            }
        }

    }
}
