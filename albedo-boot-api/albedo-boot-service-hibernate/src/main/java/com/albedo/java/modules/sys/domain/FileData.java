/**
 * Copyright &copy; 2018 <a href="https://github.com/somewhereMrli/albedo-boot">albedo-boot</a> All rights reserved.
 */
package com.albedo.java.modules.sys.domain;

import com.albedo.java.common.persistence.domain.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import javax.validation.constraints.Size;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


/**
 * 文件管理Entity 文件管理
 * @author somewhere
 * @version 2018-08-24
 */
@Entity
@Table(name = "sys_file_data_t")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data @ToString @NoArgsConstructor @AllArgsConstructor
public class FileData extends IdEntity<String> {

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
	/** name 名称 */@Size(max=32)@Column(name = "name_", unique = false, nullable = true, length = 32)
	private String name;
	/** path 路径 */@Size(max=255)@Column(name = "path_", unique = false, nullable = true, length = 255)
	private String path;
	/** size 大小 */@NotNull @Column(name = "size_", unique = false, nullable = false)
	private Long size;
	/** type 类型 */@Size(max=60)@Column(name = "type_", unique = false, nullable = true, length = 60)
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
