package com.albedo.java.modules.sys.service;

import com.albedo.java.common.persistence.DynamicSpecifications;
import com.albedo.java.common.persistence.SpecificationDetail;
import com.albedo.java.common.persistence.service.DataVoService;
import com.albedo.java.modules.sys.domain.Org;
import com.albedo.java.modules.sys.domain.Role;
import com.albedo.java.modules.sys.domain.User;
import com.albedo.java.modules.sys.repository.UserRepository;
import com.albedo.java.util.BeanVoUtil;
import com.albedo.java.util.PublicUtil;
import com.albedo.java.util.RandomUtil;
import com.albedo.java.util.domain.PageModel;
import com.albedo.java.util.domain.QueryCondition;
import com.albedo.java.util.exception.RuntimeMsgException;
import com.albedo.java.vo.sys.UserExcelVo;
import com.albedo.java.vo.sys.UserVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing users.
 *
 * @author somewhere
 */
@Service
public class UserService extends DataVoService<UserRepository, User, String, UserVo> {


    private final OrgService orgService;

    private final RoleService roleService;

    private final CacheManager cacheManager;

    public UserService(OrgService orgService, RoleService roleService, CacheManager cacheManager) {
        this.orgService = orgService;
        this.roleService = roleService;
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

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public UserVo findOneVo(String id) {
        User relationOne = findRelationOne(id);
        relationOne.setRoles(roleService.selectListByUserId(id));
        return copyBeanToVo(relationOne);
    }

    @Override
    public void save(UserVo userVo) {
        User user = PublicUtil.isNotEmpty(userVo.getId()) ? repository.selectById(userVo.getId()) : new User();
        copyVoToBean(userVo, user);
        if (user.getLangKey() == null) {
            // default language
            user.setLangKey("zh-cn");
        } else {
            user.setLangKey(user.getLangKey());
        }
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(PublicUtil.getCurrentDate());
        user.setActivated(true);
        super.saveOrUpdate(user);
        if (PublicUtil.isNotEmpty(user.getRoleIdList())) {
            repository.deleteUserRoles(user.getId());
            repository.addUserRoles(user);
        }
        cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId());
        log.debug("Save Information for User: {}", user);

    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Optional<UserVo> getUserWithAuthoritiesByLogin(String login) {
        return Optional.of(copyBeanToVo(repository.selectUserByLoginId(login)));
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public UserVo getUserWithAuthorities(String id) {
        User user = repository.selectById(id);
        return copyBeanToVo(user);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public UserVo findVo(String id) {
        User user = repository.selectById(id);
        return copyBeanToVo(user);
    }


    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageModel<User> findPage(PageModel<User> pm, List<QueryCondition> andQueryConditions, List<QueryCondition> orQueryConditions) {
        //拼接查询动态对象
        SpecificationDetail<User> spec = DynamicSpecifications.bySearchQueryCondition(
                andQueryConditions,
                QueryCondition.ne(User.F_STATUS, User.FLAG_DELETE),
                QueryCondition.ne(User.F_ID, "1"));
        spec.orAll(orQueryConditions);
        //自定义sql分页查询
        findRelationPage(pm, spec);


        return pm;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public PageModel<User> findPage(PageModel<User> pm, List<QueryCondition> authQueryConditions) {
        //拼接查询动态对象
        SpecificationDetail<User> spec = DynamicSpecifications.
                buildSpecification(pm.getQueryConditionJson(),
//                        QueryCondition.ne(User.F_STATUS, User.FLAG_DELETE),
                        QueryCondition.ne(User.F_ID,  "1"));
        spec.setPersistentClass(getPersistentClass()).orAll(authQueryConditions);
        //动态生成sql分页查询
        findRelationPage(pm, spec);

        return pm;
    }

    public void changePassword(String loginId, String newPassword, String avatar) {
        Optional.of(repository.selectOne(new QueryWrapper<User>().eq(User.F_LOGINID, loginId))).ifPresent(
            user -> {
                user.setPassword(newPassword);
                user.setAvatar(avatar);
                repository.updateById(user);
                cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId());
                log.debug("Changed password for User: {}", user);
            }
        );
    }

    public Optional<User> findOneByLoginId(String loginId) {
        User user = null;
        try {
            user = repository.selectUserByLoginId(loginId);
        }catch (Exception e){
            log.error("{}",e);
            cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId());
            user = repository.selectUserByLoginId(loginId);
        }
        return user!=null ? Optional.of(user) : Optional.empty();
    }

    @Override
    public void lockOrUnLock(List<String> idList) {
        super.lockOrUnLock(idList);
        repository.selectBatchIds(idList).forEach(user ->
            cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId()));
    }


    @Override
    public Integer deleteBatchIds(Collection<String> idList) {
        repository.selectBatchIds(idList).forEach(user ->
                cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId()));
        Integer rs = repository.deleteBatchIds(idList);
        return rs;
    }

    public void save(@Valid UserExcelVo userExcelVo) {
        User user = new User();
        BeanVoUtil.copyProperties(userExcelVo, user);
        Org org = orgService.findOne(new QueryWrapper<Org>().eq(Org.F_SQL_NAME, userExcelVo.getOrgName()));
        if(org!=null){
            user.setOrgId(org.getId());
        }
        Role role = roleService.findOne(new QueryWrapper<Role>().eq(Role.F_SQL_NAME, userExcelVo.getRoleNames()));
        if(role==null){
            throw new RuntimeMsgException("无法获取角色"+userExcelVo.getRoleNames()+"信息");
        }
        user.setRoleIdList(Lists.newArrayList(role.getId()));
        saveOrUpdate(user);
    }

    public UserVo findExcelOneVo() {
        User user = findOne(new QueryWrapper<User>().ne(User.F_SQL_ID, "1"));
        return BeanVoUtil.copyPropertiesByClass(user, UserVo.class);
    }
}
