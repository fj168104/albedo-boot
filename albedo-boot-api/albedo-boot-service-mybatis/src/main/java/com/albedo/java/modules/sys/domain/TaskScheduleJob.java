/**
 * Copyright &copy; 2015 <a href="http://www.bs-innotech.com/">bs-innotech</a> All rights reserved.
 */
package com.albedo.java.modules.sys.domain;

import com.albedo.java.common.persistence.domain.IdEntity;
import com.albedo.java.util.annotation.DictType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.text.StringEscapeUtils;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotBlank;

/**
 * 任务调度管理Entity 任务调度
 *
 * @author lj
 * @version 2017-01-23
 */

@TableName("sys_task_schedule_job_t")
@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class TaskScheduleJob extends IdEntity<TaskScheduleJob, String> {

    /**
     * F_NAME name_ : 名称
     */
    public static final String F_NAME = "name";
    /**
     * F_JOBGROUP job_group : 分组
     */
    public static final String F_JOBGROUP = "jobGroup";
    /**
     * F_STARTSTATUS start_status : 是否启动
     */
    public static final String F_JOBSTATUS = "jobStatus";
    /**
     * F_CRONEXPRESSION cron_expression : cron表达式
     */
    public static final String F_CRONEXPRESSION = "cronExpression";
    /**
     * F_BEANCLASS bean_class : 调用类名
     */
    public static final String F_BEANCLASS = "beanClass";
    /**
     * F_ISCONCURRENT is_concurrent : 是否当前任务
     */
    public static final String F_ISCONCURRENT = "isConcurrent";
    /**
     * F_SPRINGID spring_id : spring bean
     */
    public static final String F_SPRINGID = "springId";
    /**
     * F_SOURCEID sourceId : 业务编号
     */
    public static final String F_SOURCEID = "sourceId";
    /**
     * F_METHODNAME method_name : 调用方法名
     */
    public static final String F_METHODNAME = "methodName";
    /**
     * F_METHODPARAMS method_params : 调用方法参数 json
     */
    public static final String F_METHODPARAMS = "methodParams";



    public static final String F_DESCRIPTION = "description";

    public static final String F_SQL_SOURCEID = "source_id";
    public static final String F_SQL_JOBSTATUS = "job_status";


    private static final long serialVersionUID = 1L;
    // columns START
    /**
     * name 名称
     */
    @Size(max = 255)
    @TableField("name_")
    private String name;
    /**
     * group 分组
     */
    @Size(max = 255)
    @TableField("job_group")
    private String jobGroup;
    /**
     * jobStatus 任务状态
     */
    @TableField(F_SQL_JOBSTATUS)
    @DictType(name = "sys_yes_no")
    private String jobStatus;
    /**
     * cronExpression cron表达式
     */
    @NotBlank
    @Size(max = 255)
    @TableField("cron_expression")
    private String cronExpression;
    /**
     * beanClass 调用类名
     */
    @Size(max = 255)
    @TableField("bean_class")
    private String beanClass;
    /**
     * isConcurrent 是否当前任务
     */
    @TableField("is_concurrent")
    @DictType(name = "sys_yes_no")
    private Integer isConcurrent;
    /**
     * springId spring bean
     */
    @Size(max = 255)
    @TableField("spring_id")
    private String springId;
    /**
     * sourceId 业务编号
     */
    @Size(max = 32)
    @TableField(F_SQL_SOURCEID)
    private String sourceId;
    /**
     * methodName 调用方法名
     */
    @NotBlank
    @Size(max = 255)
    @TableField("method_name")
    private String methodName;
    @TableField("method_params")
    private String methodParams;

    // columns END
    public TaskScheduleJob(String id) {
        this.id = id;
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
