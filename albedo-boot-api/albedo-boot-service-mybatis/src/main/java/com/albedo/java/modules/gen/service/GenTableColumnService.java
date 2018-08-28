package com.albedo.java.modules.gen.service;

import com.albedo.java.common.persistence.service.DataVoService;
import com.albedo.java.modules.gen.domain.GenTableColumn;
import com.albedo.java.modules.gen.repository.GenTableColumnRepository;
import com.albedo.java.util.base.Assert;
import com.albedo.java.vo.gen.GenTableColumnVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing genTables.
 *
 * @author somewhere
 */
@Service
public class GenTableColumnService extends
    DataVoService<GenTableColumnRepository, GenTableColumn, String, GenTableColumnVo> {

    List<GenTableColumn> findAllByGenTableIdOrderBySort(String id){
        return findAll(new QueryWrapper<GenTableColumn>().eq(GenTableColumn.F_GENTABLEID,id)
            .orderByAsc(GenTableColumn.F_SORT));
    }


    public void deleteByTableId(String id, String currentAuditor) {
        List<GenTableColumn> genTableColumnList = findAllByGenTableIdOrderBySort(id);
        Assert.assertNotNull(genTableColumnList, "id " + id + " genTableColumn 不能为空");
        super.deleteBatchIds(genTableColumnList.stream().map(item->item.getId()).collect(Collectors.toList()));

    }


}
