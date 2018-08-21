<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
      <el-form :inline="true">
              <el-form-item label="标题">
              <el-input class="filter-item input-normal" v-model="listQuery.title"></el-input>
        </el-form-item>
        <el-form-item label="名称">
              <el-input class="filter-item input-normal" v-model="listQuery.name"></el-input>
        </el-form-item>
      </el-form>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="加载中..." border fit highlight-current-row style="width: 99%">
      <el-table-column align="center" label="标题">
        <template slot-scope="scope">
          <span>{{scope.title}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="作者">
        <template slot-scope="scope">
          <span>{{scope.author}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="名称">
        <template slot-scope="scope">
          <span>{{scope.name}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="邮箱">
        <template slot-scope="scope">
          <span>{{scope.email}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="手机">
        <template slot-scope="scope">
          <span>{{scope.phone}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="activated_">
        <template slot-scope="scope">
          <span>{{scope.activated}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="key">
        <template slot-scope="scope">
          <span>{{scope.langKey}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="activation_key">
        <template slot-scope="scope">
          <span>{{scope.activationKey}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="reset_key">
        <template slot-scope="scope">
          <span>{{scope.resetKey}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="reset_date">
        <template slot-scope="scope">
          <span>{{scope.resetDate}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="200" v-if="test_testBook_edit || test_testBook_delete">
        <template slot-scope="scope">
          <el-button v-if="test_testBook_edit" type="text" @click="handleEdit(scope.row)">编辑
          </el-button>
          <el-button v-if="test_testBook_lock" type="text" @click="handleLock(scope.row)">编辑
          </el-button>
          <el-button v-if="test_testBook_delete" type="text" @click="handleDelete(scope.row)">删除
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

        <el-form-item label="标题" prop="title" :rules="[
          
          {min: 0,max: 32,message: '长度在 0 到 32 个字符'},
          ]">
                <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="作者" prop="author" :rules="[
          {required: true,message: '请输入作者'},
          {min: 0,max: 50,message: '长度在 0 到 50 个字符'},
          ]">
                <el-input v-model="form.author"></el-input>
        </el-form-item>
        <el-form-item label="名称" prop="name" :rules="[
          
          {min: 0,max: 50,message: '长度在 0 到 50 个字符'},
          ]">
                <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email" :rules="[
          
          {min: 0,max: 100,message: '长度在 0 到 100 个字符'},
          {validator:validateUnique}]">
                <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item label="手机" prop="phone" :rules="[
          
          {min: 0,max: 32,message: '长度在 0 到 32 个字符'},
          ]">
                <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="activated_" prop="activated" :rules="[
          {required: true,message: '请输入activated_'},
          {min: 0,max: 1,message: '长度在 0 到 1 个字符'},
          ]">
                <el-input v-model="form.activated"></el-input>
        </el-form-item>
        <el-form-item label="key" prop="langKey" :rules="[
          
          {min: 0,max: 5,message: '长度在 0 到 5 个字符'},
          ]">
                <el-input v-model="form.langKey"></el-input>
        </el-form-item>
        <el-form-item label="activation_key" prop="activationKey" :rules="[
          
          {min: 0,max: 20,message: '长度在 0 到 20 个字符'},
          ]">
                <el-input v-model="form.activationKey"></el-input>
        </el-form-item>
        <el-form-item label="reset_key" prop="resetKey" :rules="[
          
          {min: 0,max: 20,message: '长度在 0 到 20 个字符'},
          ]">
                <el-input v-model="form.resetKey"></el-input>
        </el-form-item>
        <el-form-item label="reset_date" prop="resetDate" :rules="[
          
          
          ]">
              <el-date-picker v-model="form.resetDate" type="datetime" >
              </el-date-picker>
        </el-form-item>
        <el-form-item label="description_" prop="description" :rules="[
          
          {min: 0,max: 255,message: '长度在 0 到 255 个字符'},
          ]">
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
import { pageTestBook, findTestBook, saveTestBook, removeTestBook } from "./service";
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
  name: "table_test_testBook",
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
        title: undefined,
        author: undefined,
        name: undefined,
        email: undefined,
        phone: undefined,
        activated: undefined,
        langKey: undefined,
        activationKey: undefined,
        resetKey: undefined,
        resetDate: undefined,
        description: undefined,
      },
      validateUnique: (rule, value, callback) => {
        isValidateUnique(rule, value, callback, '/test/testBook/checkByProperty?id='+toStr(this.form.id))
      },
       statusOptions: undefined,
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
    this.test_testBook_edit = this.authorities.indexOf("test_testBook_edit") !== -1;
    this.test_testBook_lock = this.authorities.indexOf("test_testBook_lock") !== -1;
    this.test_testBook_delete = this.authorities.indexOf("test_testBook_delete") !== -1;

    dictCodes({codes:'sys_status,'}).then(response => {
      this.statusOptions = response.data[0];
    });
  },
  methods: {
    getList() {
      this.listLoading = true;
      this.listQuery.isAsc = false;
      this.listQuery.queryConditionJson = parseJsonItemForm([
      {fieldName: 'title',value:this.listQuery.title,operate:'like',attrType:'String'},
      {fieldName: 'name',value:this.listQuery.name,operate:'like',attrType:'String'},
      ])
      pageTestBook(this.listQuery).then(response => {
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
        findTestBook(row.id).then(response => {
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
          saveTestBook(this.form).then((data) => {
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
        "此操作将永久删除该测试书籍, 是否继续?",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        removeTestBook(row.id).then((data) => {
            if (data.status == MSG_TYPE_SUCCESS) {
              this.getList();
            }
          });
      });
    },
    handleLock(row) {
      lockTestBook(row.id).then((data) => {
        if (data.status == MSG_TYPE_SUCCESS) {
          this.getList();
        }
      });
    },
    resetForm() {
      this.form = {
        title: "",
        author: "",
        name: "",
        email: "",
        phone: "",
        activated: "",
        langKey: "",
        activationKey: "",
        resetKey: "",
        resetDate: "",
        description: "",
      };
      this.$refs['form']&&this.$refs['form'].resetFields();
    }
  }
};
</script>