/**
 * Copyright &copy; 2015 <a href="http://www.bs-innotech.com/">bs-innotech</a> All rights reserved.
 */
package com.albedo.java.common.persistence.repository;

import com.albedo.java.common.persistence.domain.BaseEntity;

import java.io.Serializable;
import java.util.Optional;

/**
 * TreeRepository
 *
 * @author admin
 * @version 2017-01-01
 */
public interface DataRepository<T extends BaseEntity, PK extends Serializable> extends BaseRepository<T, PK> {

    /**
     * 查询主键
     * @param id
     * @return
     */
    T findOneById(PK id);
}
