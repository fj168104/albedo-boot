/**
 * Copyright &copy; 2018 <a href="https://github.com/somewhereMrli/albedo-boot">albedo-boot</a> All rights reserved.
 */
package com.albedo.java.modules.sys.service;

import com.albedo.java.common.persistence.service.DataVoService;
import com.albedo.java.modules.sys.domain.FileData;
import com.albedo.java.modules.sys.repository.FileDataRepository;
import com.albedo.java.vo.sys.FileDataVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 文件管理Service 文件管理
 * @author somewhere
 * @version 2018-08-24
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class FileDataService extends DataVoService<FileDataRepository, FileData, String, FileDataVo>{


}
