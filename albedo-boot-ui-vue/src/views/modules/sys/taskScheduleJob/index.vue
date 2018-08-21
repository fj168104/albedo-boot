<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
      <el-form :inline="true">
            </el-form>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="加载中..." border fit highlight-current-row style="width: 99%">
      <el-table-column align="center" fixed="right" label="操作" width="200" v-if="sys_taskScheduleJob_edit || sys_taskScheduleJob_delete">
        <template slot-scope="scope">
          <el-button v-if="sys_taskScheduleJob_edit" type="text" @click="handleEdit(scope.row)">编辑
          </el-button>
          <el-button v-if="sys_taskScheduleJob_lock" type="text" @click="handleLock(scope.row)">编辑
          </el-button>
          <el-button v-if="sys_taskScheduleJob_delete" type="text" @click="handleDelete(scope.row)">删除
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

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel()">取 消</el-button>
        <el-button type="primary" @click="save()">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { pageTaskScheduleJob, findTaskScheduleJob, saveTaskScheduleJob, removeTaskScheduleJob } from "./service";
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
      },
      validateUnique: (rule, value, callback) => {
        isValidateUnique(rule, value, callback, '/sys/taskScheduleJob/checkByProperty?id='+toStr(this.form.id))
      },
      dialogFormVisible: false,
      dialogStatus: 'create',
      textMap: {
        update: '编辑',
        create: '创建'
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

    dictCodes({codes:''}).then(response => {
    });
  },
  methods: {
    getList() {
      this.listLoading = true;
      this.listQuery.isAsc = false;
      this.listQuery.queryConditionJson = parseJsonItemForm([
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
          this.dialogFormVisible = true;
        });
      }
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
      };
      this.$refs['form']&&this.$refs['form'].resetFields();
    }
  }
};
</script>