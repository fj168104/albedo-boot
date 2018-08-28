package com.albedo.java.modules.sys.service;

import com.albedo.java.modules.sys.domain.TaskScheduleJob;
import com.albedo.java.modules.sys.util.TaskUtils;
import com.albedo.java.util.excel.ImportExcel;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author somewhere
 * @Description: 计划任务执行处 无状态
 * @date 2014年4月24日 下午5:05:47
 */
public class QuartzJobFactory implements Job {
    private static Logger log = LoggerFactory.getLogger(ImportExcel.class);
    @Override
    public void execute(JobExecutionContext context) {
        TaskScheduleJob taskScheduleJob = (TaskScheduleJob) context.getMergedJobDataMap().get("taskScheduleJob");
        TaskUtils.invokMethod(taskScheduleJob);
    }
}
