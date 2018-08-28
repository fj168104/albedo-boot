/**
 * Copyright &copy; 2018 <a href="https://github.com/somewhereMrli/albedo-boot">albedo-boot</a> All rights reserved.
 */
package com.albedo.java.modules.sys.domain;

import com.albedo.java.common.persistence.domain.IdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * 文件管理Entity 文件管理
 * @author somewhere
 * @version 2018-08-24
 */
@TableName(value = "sys_file_data_t")
@Data @ToString @NoArgsConstructor @AllArgsConstructor
public class FileData extends IdEntity<FileData, String> {

	private static final long serialVersionUID = 1L;
	/** F_NAME name_  :  名称 */
	public static final String F_NAME = "name";
	/** F_PATH path_  :  路径 */
	public static final String F_PATH = "path";
	/** F_SIZE size_  :  大小 */
	public static final String F_SIZE = "size";
	/** F_TYPE type_  :  类型 */
	public static final String F_TYPE = "type";

	/** F_SQL_NAME name_  :  名称 */
	public static final String F_SQL_NAME = "name_";
	/** F_SQL_PATH path_  :  路径 */
	public static final String F_SQL_PATH = "path_";
	/** F_SQL_SIZE size_  :  大小 */
	public static final String F_SQL_SIZE = "size_";
	/** F_SQL_TYPE type_  :  类型 */
	public static final String F_SQL_TYPE = "type_";

	//columns START
	/** name 名称 */@Size(max=32)@TableField("name_")
	private String name;
	/** path 路径 */@Size(max=255)@TableField("path_")
	private String path;
	/** size 大小 */@NotNull @TableField("size_")
	private Long size;
	/** type 类型 */@Size(max=60)@TableField("type_")
	private String type;
	//columns END

	public FileData(String id) {
		this.setId(id);
	}

	@Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
