
<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
      <el-form :inline="true">
        <el-form-item label="表名">
          <el-input style="width: 200px;" class="filter-item" v-model="listQuery.name"></el-input>
        </el-form-item>
        <el-form-item label="说明">
          <el-input style="width: 200px;" class="filter-item" v-model="listQuery.comments"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">搜索</el-button>
          <el-button v-if="gen_genTable_edit" class="filter-item" style="margin-left: 10px;" @click="handleEdit" type="primary" icon="edit">添加</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="加载中..." border fit highlight-current-row style="width: 99%">

      <el-table-column align="center" label="表名">
        <template slot-scope="scope">
          <span>{{scope.row.name}}</span>
        </template>
      </el-table-column>


      <el-table-column align="center" label="说明">
        <template slot-scope="scope">
          <span>
            {{scope.row.comments}}
          </span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="类名">
        <template slot-scope="scope">
          <span>{{scope.row.className}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="父表名">
        <template slot-scope="scope">
          <span>{{scope.row.parentTable}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="类名">
        <template slot-scope="scope">
          <span>{{scope.row.className}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="创建时间">
        <template slot-scope="scope">
          <span>{{scope.row.createdDate}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" class-name="status-col" label="状态">
        <template slot-scope="scope">
          <el-tag>{{scope.row.status}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作" width="200">
        <template slot-scope="scope">
          <el-button v-if="gen_genTable_edit" size="small" type="success" @click="handleEdit(scope.row)">编辑
          </el-button>
          <el-button v-if="gen_genTable_delete" size="small" type="danger" @click="handleDelete(scope.row)">删除
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
        <el-tabs>
          <el-tab-pane label="基本信息">
            <el-form-item label="名称" prop="loginId" :rules="[{required: true,message: '请输入名称'}]">
              <el-input v-model="form.name" placeholder="请输名称"></el-input>
            </el-form-item>
            <el-form-item label="说明" prop="phone" :rules="[{required: true,message: '请输入说明'}]">
              <el-input v-model="form.comments"></el-input>
            </el-form-item>
            <el-form-item label="类名" prop="phone" :rules="[{required: true,message: '请输入类名'}]">
              <el-input v-model="form.className"></el-input>
            </el-form-item>
            <el-form-item label="父表表名" prop="phone" :rules="[{required: true,message: '请选择父表表名'}]">
              <AvueCrudSelect v-model="form.parentTable" :dic="tableList"></AvueCrudSelect>
            </el-form-item>
            <el-form-item label="当前表外键" prop="phone" :rules="[{required: true,message: '请输入说明'}]">
              <AvueCrudSelect v-model="form.parentTableFk" :dic="columnList"></AvueCrudSelect>
            </el-form-item>
            <el-form-item label="状态" prop="status" :rules="[{required: true,message: '请选择状态' }]">
              <AvueCrudRadio v-model="form.status" :dic="statusOptions"></AvueCrudRadio>
            </el-form-item>
            <el-form-item label="备注" prop="description">
              <el-input type="textarea" v-model="form.description"></el-input>
            </el-form-item>


          </el-tab-pane>
          <el-tab-pane label="字段列表">
            <table id="contentTable" class="el-table">
              <thead>
              <tr>
                <th title="数据库字段名">列名</th>
                <th title="默认读取数据库字段备注">标题</th>
                <th title="描述字段">说明</th>
                <th title="数据库中设置的字段类型及长度">物理类型</th>
                <th title="实体对象的属性字段类型">Java类型</th>
                <th title="实体对象的属性字段（对象名.属性名|属性名2|属性名3，例如：用户user.id|name|loginName，属性名2和属性名3为Join时关联查询的字段）">
                  Java属性名称 <i class="icon-question-sign"></i></th>
                <th title="是否是数据库主键">主键</th>
                <th title="字段是否可为空值，不可为空字段自动进行空值验证">可空</th>
                <th title="字段是否唯一，唯一空字段自动进行唯一性验证">唯一</th>
                <th title="选中后该字段被加入到insert语句里">插入</th>
                <th title="选中后该字段被加入到update语句里">编辑</th>
                <th title="选中后该字段被加入到查询列表里">列表</th>
                <th title="选中后该字段被加入到查询条件里">查询</th>
                <th title="该字段为查询字段时的查询匹配放松">查询匹配方式</th>
                <th title="字段在表单中显示的类型">显示表单类型</th>
                <th title="显示表单类型设置为“下拉框、复选框、点选框”时，需设置字典的类型">字典类型</th>
                <th>排序</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for=" (column,i) in form.columnFormList" :class="column.status=='-1'? 'error':''" :title="column.status=='-1'? '已删除的列，保存之后消失！':''">
                <td>
                  <el-input v-model="form.columnFormList[i].title"></el-input>
                </td>
                <td>
                  <el-input v-model="form.columnFormList[i].comments"></el-input>
                </td>
                <td>
                  <AvueCrudSelect v-model="form.columnFormList[i].javaType" :dic="javaTypeList"></AvueCrudSelect>
                </td>
                <td>
                  <el-input v-model="form.columnFormList[i].javaField"></el-input>
                </td>
                <td>
                  <el-checkbox v-model="form.columnFormList[i].isPk" ></el-checkbox>
                </td>
                <td>
                  <el-checkbox v-model="form.columnFormList[i].isNull" ></el-checkbox>
                </td>
                <td>
                  <el-checkbox v-model="form.columnFormList[i].isUnique" ></el-checkbox>
                </td>
                <td>
                  <el-checkbox v-model="form.columnFormList[i].isInsert" ></el-checkbox>
                </td>
                <td>
                  <el-checkbox v-model="form.columnFormList[i].isEdit" ></el-checkbox>
                </td>
                <td>
                  <el-checkbox v-model="form.columnFormList[i].isList" ></el-checkbox>
                </td>
                <td>
                  <el-checkbox v-model="form.columnFormList[i].isQuery" ></el-checkbox>
                </td>
                <td>
                  <AvueCrudSelect v-model="form.columnFormList[i].queryType" :dic="queryTypeList"></AvueCrudSelect>
                </td>
                <td>
                  <AvueCrudSelect v-model="form.columnFormList[i].showType" :dic="showTypeList"></AvueCrudSelect>
                </td>
                <td>
                  <el-input v-model="form.columnFormList[i].dictType"></el-input>
                </td>
                <td>
                  <el-input v-model="form.columnFormList[i].sort"></el-input>
                </td>
              </tr>
              </tbody>
            </table>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel()">取 消</el-button>
        <el-button type="primary" @click="save()">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { pageGenTable, findGenTable, saveGenTable, removeGenTable } from "./service";
import { mapGetters } from "vuex";
import ElRadioGroup from "element-ui/packages/radio/src/radio-group";
import ElOption from "element-ui/packages/select/src/option";
import {DATA_STATUS} from "@/const/common";
import {
  isValidateMobile,
  isValidateUnique,
  objectToString, toStr,
  validateNull
} from "../../../util/validate";
import {dictCodes} from "../../../api/dataSystem";
import {MSG_TYPE_SUCCESS} from "../../../const/common";
import {parseJsonItemForm, parseTreeData} from "../../../util/util";
import {baseUrl} from "../../../config/env";
import {getToken} from "../../../util/auth";

export default {
  components: {
    ElOption,
    ElRadioGroup
  },
  name: "table_get_table",
  data() {
    return{
      list: null,
      total: null,
      listLoading: true,
      listQuery: {
        page: 1,
        size: 20
      },
      javaTypeList:{},
      queryTypeList:{},
      showTypeList:{},
      tableList:{},
      columnList:{},
      form: {
        name: undefined,
        comments: undefined,
        className: undefined,
        parentTable: undefined,
        parentTableFk: undefined,
        columnFormList: [],
        status: undefined,
        description: undefined
      },
      statusOptions: [],
      dialogFormVisible: false,
      dialogStatus: 'create',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      isDisabled: {
        0: false,
        1: true
      },
      tableKey: 0
    };
  },
  computed: {
    ...mapGetters(['authorities'])
  },
  filters: {
    statusFilter(status) {
      return '<span class="m-badge ' + DATA_STATUS[status].class + ' m-badge--wide">' + status + '</span>';
    }
  },
  created() {
    this.getList();
    this.gen_genTable_edit = this.authorities.indexOf("gen_genTable_edit") !== -1;
    this.gen_genTable_delete = this.authorities.indexOf("gen_genTable_delete") !== -1;
    dictCodes({codes:'sys_status'}).then(response => {
      this.statusOptions = response.data;
    });
  },
  methods: {
    getList() {
      this.listLoading = true;
      this.listQuery.isAsc = false;
      this.listQuery.queryConditionJson = parseJsonItemForm([{
        fieldName: 'name',value:this.listQuery.name
      },{
        fieldName: 'comments',value:this.listQuery.comments
      }])
      pageGenTable(this.listQuery).then(response => {
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
        findGenTable(row.id).then(response => {
          this.form = response.data;
          this.javaTypeList = response.javaTypeList
          this.queryTypeList = response.queryTypeList
          this.showTypeList = response.showTypeList
          console.log(response.tableList)
          this.tableList = response.tableList
          this.columnList = response.columnList
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
          // this.form.password = undefined;
          saveGenTable(this.form).then((data) => {
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
        "此操作将永久删除该表(表名:" + row.name + "), 是否继续?",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        removeGenTable(row.id).then((data) => {
            if (data.status == MSG_TYPE_SUCCESS) {
              this.getList();
            }
          });
      });
    },
    resetForm() {
      this.form = {
        id: undefined,
        loginId: "",
        password: "",
        roleIdList: [],
        status: "",
        orgId: "",
        phone: "",
        email: "",
        description: undefined
      };
      this.$refs['form']&&this.$refs['form'].resetFields();
    }
  }
};
</script>
