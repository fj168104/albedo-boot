/**
 * Copyright &copy; 2018 <a href="https://github.com/somewhereMrli/albedo-boot">albedo-boot</a> All rights reserved.
 */
package com.albedo.java.vo.sys;

import com.albedo.java.vo.base.DataEntityVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 文件管理EntityVo 文件管理
 * @author somewhere
 * @version 2018-08-24
 */
@Data @ToString @NoArgsConstructor @AllArgsConstructor
public class FileDataVo extends DataEntityVo<String> {

	private static final long serialVersionUID = 1L;
	/** F_NAME name_  :  名称 */
	public static final String F_NAME = "name";
	/** F_PATH path_  :  路径 */
	public static final String F_PATH = "path";
	/** F_SIZE size_  :  大小 */
	public static final String F_SIZE = "size";
	/** F_TYPE type_  :  类型 */
	public static final String F_TYPE = "type";

	//columns START
	/** name 名称 */
 @Size(max=32)
	private String name;
	/** path 路径 */
 @Size(max=255)
	private String path;
	/** size 大小 */
 @NotNull
	private Long size;
	/** type 类型 */
 @Size(max=60)
	private String type;
	//columns END

	public FileDataVo(String id) {
		this.setId(id);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FileDataVo == false){ return false;}
		if(this == obj) {return true;}
		FileDataVo other = (FileDataVo)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}
