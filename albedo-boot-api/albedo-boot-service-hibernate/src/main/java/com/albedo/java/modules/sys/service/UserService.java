package com.albedo.java.modules.sys.service;

import com.albedo.java.common.persistence.DynamicSpecifications;
import com.albedo.java.common.persistence.SpecificationDetail;
import com.albedo.java.common.persistence.service.DataVoService;
import com.albedo.java.modules.sys.domain.Org;
import com.albedo.java.modules.sys.domain.Role;
import com.albedo.java.modules.sys.domain.User;
import com.albedo.java.modules.sys.repository.OrgRepository;
import com.albedo.java.modules.sys.repository.PersistentTokenRepository;
import com.albedo.java.modules.sys.repository.RoleRepository;
import com.albedo.java.modules.sys.repository.UserRepository;
import com.albedo.java.util.BeanVoUtil;
import com.albedo.java.util.DateUtil;
import com.albedo.java.util.PublicUtil;
import com.albedo.java.util.RandomUtil;
import com.albedo.java.util.domain.PageModel;
import com.albedo.java.util.domain.QueryCondition;
import com.albedo.java.util.exception.RuntimeMsgException;
import com.albedo.java.vo.sys.UserExcelVo;
import com.albedo.java.vo.sys.UserVo;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService extends DataVoService<UserRepository, User, String, UserVo>{

    private final PersistentTokenRepository persistentTokenRepository;

    private final RoleRepository roleRepository;
    private final OrgRepository orgRepository;
    private final CacheManager cacheManager;

    public UserService(PersistentTokenRepository persistentTokenRepository, RoleRepository roleRepository, OrgRepository orgRepository, CacheManager cacheManager) {
        this.persistentTokenRepository = persistentTokenRepository;
        this.roleRepository = roleRepository;
        this.orgRepository = orgRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    public UserVo copyBeanToVo(User user) {
        UserVo userResult = new UserVo();
        super.copyBeanToVo(user, userResult);
        userResult.setRoleNames(user.getRoleNames());
        if (user.getOrg() != null) {
            userResult.setOrgName(user.getOrg().getName());
        }
        return userResult;
    }

    @Override
    public void copyVoToBean(UserVo userVo, User user) {
        super.copyVoToBean(userVo, user);
        user.setRoleIdList(userVo.getRoleIdList());
    }

    public Optional<UserVo> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return repository.findOneByActivationKey(key)
                .map(user -> {
                    // activate given user for the registration key.
                    user.setActivated(true);
                    user.setActivationKey(null);
                    repository.save(user);
                    log.debug("Activated user: {}", user);
                    return copyBeanToVo(user);
                });
    }

    @Override
    public void save(UserVo userVo) {
        User user = copyVoToBean(userVo);
        if (user.getLangKey() == null) {
            // default language
            user.setLangKey("zh-cn");
        } else {
            user.setLangKey(user.getLangKey());
        }
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(ZonedDateTime.now());
        user.setActivated(true);
        user = repository.save(user);
        cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId());
        log.debug("Save Information for User: {}", user);

    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Optional<UserVo> getUserWithAuthoritiesByLogin(String login) {
        return repository.findOneByLoginId(login).map(u -> {
            u.getRoles().size();
            return copyBeanToVo(u);
        });
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public UserVo getUserWithAuthorities(String id) {
        User user = repository.findOneById(id);
        user.getRoles().size(); // eagerly load the association
        return copyBeanToVo(user);
    }

    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        LocalDate now = LocalDate.now();
        persistentTokenRepository.findByTokenDateBefore(DateUtil.addMonths(PublicUtil.getCurrentDate(), -1)).stream().forEach(token -> {
            log.debug("Deleting token {}", token.getSeries());
            User user = token.getUser();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
            cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId());
        });
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
//        ZonedDateTime now = ZonedDateTime.now();
        List<User> users = repository.findAllByActivatedIsFalseAndCreatedDateBefore(DateUtil.addDays(PublicUtil.getCurrentDate(), -3));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLoginId());
            repository.delete(user);
            cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId());
        }
    }


    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public UserVo findResult(String id) {
        User user = repository.findOneById(id);
        return copyBeanToVo(user);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageModel findPage(PageModel<User> pm, List<QueryCondition> queryConditions) {
        SpecificationDetail<User> spec = DynamicSpecifications.buildSpecification(pm.getQueryConditionJson(), queryConditions,
                QueryCondition.ne(User.F_STATUS, User.FLAG_DELETE), QueryCondition.ne(User.F_ID, "1"));
        Page<User> page = repository.findAll(spec, pm);
        pm.setPageInstance(page);
        return pm;
    }


    public void changePassword(String loginId, String newPassword, String avatar) {
        Optional.of(loginId)
            .flatMap(repository::findOneByLoginId)
            .ifPresent(user -> {
                user.setPassword(newPassword);
                user.setAvatar(avatar);
                cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId());
                log.debug("Changed password for User: {}", user);
                save(user);
            });
    }

    @Override
    public void lockOrUnLock(List<String> idList) {
        super.lockOrUnLock(idList);
        repository.findAllById(idList).forEach(user ->
            cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId()));
    }

    @Override
    public void delete(List<String> idList) {
        super.delete(idList);
        repository.findAllById(idList).forEach(user ->
            cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId()));
    }

    public Optional<User> findOneByLoginId(String loginId) {
        return repository.getOneByLoginId(loginId);
    }

    public void save(@Valid UserExcelVo userExcelVo) {
        User user = new User();
        BeanUtils.copyProperties(userExcelVo, user);
        Org org = orgRepository.findOne(
            DynamicSpecifications.bySearchQueryCondition(QueryCondition.eq(Org.F_NAME, userExcelVo.getOrgName()))).get();
        if(org!=null){
            user.setOrgId(org.getId());
        }
        Role role = roleRepository.findOne(DynamicSpecifications.bySearchQueryCondition(QueryCondition.eq(Role.F_NAME, userExcelVo.getRoleNames()))).get();
        if(role==null){
            throw new RuntimeMsgException("无法获取角色"+userExcelVo.getRoleNames()+"信息");
        }
        user.setRoleIdList(Lists.newArrayList(role.getId()));
        save(user);
    }

    public UserVo findExcelOneVo() {
        User user = repository.findOneByIdNotAndStatus("1", User.FLAG_NORMAL);
        return BeanVoUtil.copyPropertiesByClass(user, UserVo.class);
    }
}
