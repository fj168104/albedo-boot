<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
      <el-form :inline="true">
              <el-form-item label="名称">
              <el-input class="filter-item input-normal" v-model="listQuery.name"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查询</el-button>
          <el-button v-if="sys_taskScheduleJob_edit" class="filter-item" style="margin-left: 10px;" @click="handleEdit" type="primary" icon="edit">添加</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="加载中..." border fit highlight-current-row style="width: 99%">
      <el-table-column align="center" label="名称">
        <template slot-scope="scope">
          <span>{{scope.row.name}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="分组">
        <template slot-scope="scope">
          <span>{{scope.row.group}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="任务状态">
        <template slot-scope="scope">
          <span>{{scope.row.jobStatus}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="cron表达式">
        <template slot-scope="scope">
          <span>{{scope.row.cronExpression}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="包名+类名">
        <template slot-scope="scope">
          <span>{{scope.row.beanClass}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="当前任务">
        <template slot-scope="scope">
          <span>{{scope.row.isConcurrent}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="spring bean">
        <template slot-scope="scope">
          <span>{{scope.row.springId}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="源编号">
        <template slot-scope="scope">
          <span>{{scope.row.sourceId}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="方法名">
        <template slot-scope="scope">
          <span>{{scope.row.methodName}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="方法参数">
        <template slot-scope="scope">
          <span>{{scope.row.methodParams}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" v-if="sys_taskScheduleJob_edit || sys_taskScheduleJob_delete">
        <template slot-scope="scope">
          <el-button v-if="sys_taskScheduleJob_edit" icon="icon-edit" title="编辑" type="text" @click="handleEdit(scope.row)">
          </el-button>
          <el-button v-if="sys_taskScheduleJob_lock" :icon="scope.row.status=='正常' ? 'icon-lock' : 'icon-unlock'" :title="scope.row.status=='正常' ? '锁定' : '解锁'" type="text" @click="handleLock(scope.row)">
          </el-button>
          <el-button v-if="sys_taskScheduleJob_delete" icon="icon-delete" title="删除" type="text" @click="handleDelete(scope.row)">
          </el-button>
        </template>
      </el-table-column>

    </el-table>

    <div v-show="!listLoading" class="pagination-container">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.page" :page-sizes="[10,20,30, 50]" :page-size="listQuery.size" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :model="form" ref="form" label-width="100px">

        <el-form-item label="名称" prop="name" :rules="[{required: true,message: '请输入名称'},{min: 0,max: 255,message: '长度在 0 到 255 个字符'},]">
                <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="分组" prop="group" :rules="[{required: true,message: '请输入分组'},{min: 0,max: 255,message: '长度在 0 到 255 个字符'},]">
                <el-input v-model="form.group"></el-input>
        </el-form-item>
        <el-form-item label="任务状态" prop="jobStatus" :rules="[{required: true,message: '请输入任务状态'},{min: 0,max: 255,message: '长度在 0 到 255 个字符'},]">
              <AvueCrudSelect v-model="form.jobStatus" :dic="jobStatusOptions"></AvueCrudSelect>
        </el-form-item>
        <el-form-item label="cron表达式" prop="cronExpression" :rules="[{required: true,message: '请输入cron表达式'},{min: 0,max: 255,message: '长度在 0 到 255 个字符'},]">
                <el-input v-model="form.cronExpression"></el-input>
        </el-form-item>
        <el-form-item label="包名+类名" prop="beanClass" :rules="[{min: 0,max: 255,message: '长度在 0 到 255 个字符'},]">
                <el-input v-model="form.beanClass"></el-input>
        </el-form-item>
        <el-form-item label="当前任务" prop="isConcurrent" :rules="[{min: 0,max: 255,message: '长度在 0 到 255 个字符'},]">
              <AvueCrudRadio v-model="form.isConcurrent" :dic="isConcurrentOptions"></AvueCrudRadio>
        </el-form-item>
        <el-form-item label="spring bean" prop="springId" :rules="[{min: 0,max: 255,message: '长度在 0 到 255 个字符'},]">
                <el-input v-model="form.springId"></el-input>
        </el-form-item>
        <el-form-item label="源编号" prop="sourceId" :rules="[{min: 0,max: 32,message: '长度在 0 到 32 个字符'},]">
                <el-input v-model="form.sourceId"></el-input>
        </el-form-item>
        <el-form-item label="方法名" prop="methodName" :rules="[{required: true,message: '请输入方法名'},{min: 0,max: 255,message: '长度在 0 到 255 个字符'},]">
                <el-input v-model="form.methodName"></el-input>
        </el-form-item>
        <el-form-item label="方法参数" prop="methodParams" :rules="[{min: 0,max: 512,message: '长度在 0 到 512 个字符'},]">
                <el-input v-model="form.methodParams"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="description" :rules="[{min: 0,max: 255,message: '长度在 0 到 255 个字符'},]">
                <el-input type="textarea" v-model="form.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel()">取 消</el-button>
        <el-button type="primary" @click="save()">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { pageTaskScheduleJob, findTaskScheduleJob, saveTaskScheduleJob, lockTaskScheduleJob, removeTaskScheduleJob } from "./service";
import { mapGetters } from "vuex";
import {DATA_STATUS} from "@/const/common";
import {
  isValidateUnique,
  objectToString, toStr,
  validateNull
} from "@/util/validate";
import {dictCodes} from "@/api/dataSystem";
import {MSG_TYPE_SUCCESS} from "@/const/common";
import {parseJsonItemForm} from "@/util/util";

export default {
  components: {
  },
  name: "table_sys_taskScheduleJob",
  data() {
    return{
      list: null,
      total: null,
      listLoading: true,
      listQuery: {
        page: 1,
        size: 20
      },
      form: {
        name: undefined,
        group: undefined,
        jobStatus: undefined,
        cronExpression: undefined,
        beanClass: undefined,
        isConcurrent: undefined,
        springId: undefined,
        sourceId: undefined,
        methodName: undefined,
        methodParams: undefined,
        description: undefined,
      },
      validateUnique: (rule, value, callback) => {
        isValidateUnique(rule, value, callback, '/sys/taskScheduleJob/checkByProperty?id='+toStr(this.form.id))
      },
      jobStatusOptions: undefined,
      isConcurrentOptions: undefined,
      statusOptions: undefined,
      dialogFormVisible: false,
      dialogStatus: 'create',
      textMap: {
        update: '编辑任务调度',
        create: '创建任务调度'
      },
      tableKey: 0
    };
  },
  computed: {
    ...mapGetters(['authorities'])
  },
  filters: {
  },
  created() {
    this.getList();
    this.sys_taskScheduleJob_edit = this.authorities.indexOf("sys_taskScheduleJob_edit") !== -1;
    this.sys_taskScheduleJob_lock = this.authorities.indexOf("sys_taskScheduleJob_lock") !== -1;
    this.sys_taskScheduleJob_delete = this.authorities.indexOf("sys_taskScheduleJob_delete") !== -1;

    dictCodes({codes:'sys_yes_no,sys_yes_no,sys_status,'}).then(response => {
      this.jobStatusOptions = response.data[0];
      this.isConcurrentOptions = response.data[1];
      this.statusOptions = response.data[2];
    });
  },
  methods: {
    getList() {
      this.listLoading = true;
      this.listQuery.isAsc = false;
      this.listQuery.queryConditionJson = parseJsonItemForm([
      {fieldName: 'name',value:this.listQuery.name,operate:'like',attrType:'String'},
      ])
      pageTaskScheduleJob(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    },
    handleFilter() {
      this.listQuery.page = 1;
      this.getList();
    },
    handleSizeChange(val) {
      this.listQuery.size = val;
      this.getList();
    },
    handleCurrentChange(val) {
      this.listQuery.page = val;
      this.getList();
    },
    handleEdit(row) {
      this.resetForm();
      this.dialogStatus = row && !validateNull(row.id)? "update" : "create";
      if(this.dialogStatus == "create"){
        this.dialogFormVisible = true;
      }else{
        findTaskScheduleJob(row.id).then(response => {
          this.form = response.data;
          this.form.status=objectToString(this.form.status)
          this.form.isConcurrent=objectToString(this.form.isConcurrent)
          this.dialogFormVisible = true;
        });
      }
    },
    handleLock: function (row) {
      lockTaskScheduleJob(row.id).then((data) => {
        if (data.status == MSG_TYPE_SUCCESS) {
          this.getList();
        }
      });
    },
    cancel() {
      this.dialogFormVisible = false;
      this.$refs['form'].resetFields();
    },
    save() {
      const set = this.$refs;
      set['form'].validate(valid => {
        if (valid) {
          saveTaskScheduleJob(this.form).then((data) => {
            if (data.status == MSG_TYPE_SUCCESS) {
              this.getList();
              this.cancel('form')
            }
          });
        } else {
          return false;
        }
      });
    },
    handleDelete(row) {
      this.$confirm(
        "此操作将永久删除该任务调度, 是否继续?",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        removeTaskScheduleJob(row.id).then((data) => {
            if (data.status == MSG_TYPE_SUCCESS) {
              this.getList();
            }
          });
      });
    },
    handleLock(row) {
      lockTaskScheduleJob(row.id).then((data) => {
        if (data.status == MSG_TYPE_SUCCESS) {
          this.getList();
        }
      });
    },
    resetForm() {
      this.form = {
        name: "",
        group: "",
        jobStatus: "",
        cronExpression: "",
        beanClass: "",
        isConcurrent: "",
        springId: "",
        sourceId: "",
        methodName: "",
        methodParams: "",
        description: "",
      };
      this.$refs['form']&&this.$refs['form'].resetFields();
    }
  }
};
</script>
