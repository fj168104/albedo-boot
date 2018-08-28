/**
 * Copyright &copy; 2015 <a href="http://www.bs-innotech.com/">bs-innotech</a> All rights reserved.
 */
package com.albedo.java.modules.sys.service;

import com.albedo.java.common.persistence.service.BaseService;
import com.albedo.java.modules.sys.domain.PersistentAuditEvent;
import com.albedo.java.modules.sys.repository.PersistenceAuditEventRepository;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * 操作日志Service 操作日志
 *
 * @author admin
 * @version 2017-01-03
 */
@Service
public class PersistenceAuditEventService extends BaseService<PersistenceAuditEventRepository, PersistentAuditEvent, Long> {

    public List<PersistentAuditEvent> findByAuditEventDateAfter(Date from) {
        return findAll(new QueryWrapper<PersistentAuditEvent>().gt(PersistentAuditEvent.F_SQL_AUDITEVENTDATE, from));

    }

    public Iterable<PersistentAuditEvent> findByPrincipal(String principal) {
        return findAll(new QueryWrapper<PersistentAuditEvent>().eq(PersistentAuditEvent.F_SQL_PRINCIPAL, principal));

    }

    public Iterable<PersistentAuditEvent> findByPrincipalAndAuditEventDateAfter(String principal, Date from) {
        return findAll(new QueryWrapper<PersistentAuditEvent>()
            .eq(PersistentAuditEvent.F_SQL_PRINCIPAL, principal)
            .gt(PersistentAuditEvent.F_SQL_AUDITEVENTDATE, from)
        );
    }

    public Iterable<PersistentAuditEvent> findByPrincipalAndAuditEventDateAfterAndAuditEventType(String principal, Date from, String type) {
        return findAll(new QueryWrapper<PersistentAuditEvent>()
            .eq(PersistentAuditEvent.F_SQL_PRINCIPAL, principal)
            .gt(PersistentAuditEvent.F_SQL_AUDITEVENTDATE, from)
            .eq(PersistentAuditEvent.F_SQL_AUDITEVENTTYPE, type)
        );
    }

}
