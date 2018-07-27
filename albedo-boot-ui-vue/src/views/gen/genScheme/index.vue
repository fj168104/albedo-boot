
<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
      <el-form :inline="true">
        <el-form-item label="名称">
          <el-input class="filter-item input-normal" v-model="listQuery.name"></el-input>
        </el-form-item>
        <el-form-item label="表名">
          <el-input class="filter-item input-normal" v-model="listQuery.genTableName"></el-input>
        </el-form-item>
        <el-form-item label="功能名称">
          <el-input class="filter-item input-normal" v-model="listQuery.functionName"></el-input>
        </el-form-item>
        <el-form-item label="功能作者">
          <el-input class="filter-item input-normal" v-model="listQuery.functionAuthor"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">搜索</el-button>
          <el-button v-if="gen_genScheme_edit" class="filter-item" style="margin-left: 10px;" @click="handleEdit" type="primary" icon="edit">添加</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="加载中..." border fit highlight-current-row style="width: 99%">
      <el-table-column align="center" label="名称">
        <template slot-scope="scope">
          <span>{{scope.row.name}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="表名">
        <template slot-scope="scope">
          <span>{{scope.row.genTableName}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="基础包名">
        <template slot-scope="scope">
          <span>
            {{scope.row.packageName}}
          </span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="模块">
        <template slot-scope="scope">
          <span>{{scope.row.moduleName}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="功能名称">
        <template slot-scope="scope">
          <span>{{scope.row.functionName}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="功能作者">
        <template slot-scope="scope">
          <span>{{scope.row.functionAuthor}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" class-name="status-col" label="状态">
        <template slot-scope="scope">
          <el-tag>{{scope.row.status}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作" width="200">
        <template slot-scope="scope">
          <el-button v-if="gen_genScheme_edit" size="small" type="success" @click="handleEdit(scope.row)">编辑
          </el-button>
          <el-button v-if="gen_genScheme_delete" size="small" type="danger" @click="handleDelete(scope.row)">删除
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
        <el-form-item label="方案名称" prop="name" :rules="[{required: true,message: '请输入方案名称'}]">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="模块分类" prop="category"
                      inline-message="生成结构：(包名)/(模块名)/(分层(dao,entity,service,web))/(子模块名)/(java类)"
                      :rules="[{required: true,message: '请选择模块分类'}]">
          <AvueCrudSelect v-model="form.category" :dic="categoryList" ></AvueCrudSelect>
        </el-form-item>
        <el-form-item label="生成包路径" prop="packageName"
                      :rules="[{required: true,message: '请输入生成包路径'}]">
          <el-input v-model="form.packageName"></el-input>
        </el-form-item>
        <el-form-item label="生成模块名" prop="moduleName"
                      :rules="[{required: true,message: '请输入生成模块名'}]">
          <el-input v-model="form.moduleName"></el-input>
        </el-form-item>
        <el-form-item label="生成子模块名" prop="subModuleName"
                      :rules="[{required: true,message: '请输入生成子模块名'}]">
          <el-input v-model="form.subModuleName"></el-input>
        </el-form-item>
        <el-form-item label="生成功能描述" prop="functionName"
                      :rules="[{required: true,message: '请输入生成功能描述'}]">
          <el-input v-model="form.functionName"></el-input>
        </el-form-item>
        <el-form-item label="生成功能名" prop="functionNameSimple"
                      :rules="[{required: true,message: '请输入生成功能名'}]">
          <el-input v-model="form.functionNameSimple"></el-input>
        </el-form-item>
        <el-form-item label="生成功能作者" prop="functionAuthor"
                      :rules="[{required: true,message: '请输入生成功能作者'}]">
          <el-input v-model="form.functionAuthor"></el-input>
        </el-form-item>
        <el-form-item label="业务表名" prop="genTableId"
                      :rules="[{required: true,message: '请选择业务表名'}]">
          <AvueCrudSelect v-model="form.genTableId" :dic="tableList" ></AvueCrudSelect>
        </el-form-item>
        <el-form-item label="生成选项" prop="genCode">
          <el-checkbox v-model="form.genCode" label="是否生成代码"></el-checkbox>
          <el-checkbox v-model="form.replaceFile" label="是否替换现有文件"></el-checkbox>
        </el-form-item>
        <el-form-item label="同步模块" prop="syncModule">
          <el-checkbox v-model="form.syncModule" label="是否同步模块数据"></el-checkbox>
        </el-form-item>
        <el-form-item label="同步模块" prop="parentModuleId">
          <el-input v-model="form.parentModuleName" placeholder="选择模块" @focus="handleModule()" readonly></el-input>
          <input type="hidden" v-model="form.parentModuleId" />
        </el-form-item>
        <el-form-item label="状态" prop="status" :rules="[{required: true,message: '请选择状态' }]">
          <AvueCrudRadio v-model="form.status" :dic="statusOptions"></AvueCrudRadio>
        </el-form-item>
        <el-form-item label="备注" prop="description">
          <el-input type="textarea" v-model="form.description" placeholder=""></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel()">取 消</el-button>
        <el-button type="primary" @click="save()">保 存</el-button>
      </div>
    </el-dialog>

    <el-dialog title="选择模块" :visible.sync="dialogModuleVisible">
      <el-input placeholder="输入关键字进行过滤"
                v-model="filterFormText">
      </el-input>
      <el-tree class="filter-tree" ref="formTree" :data="treeModuleData"
               check-strictly node-key="id" highlight-current @node-click="getNodeFormData"
               :filter-node-method="filterNode" default-expand-all>
      </el-tree>
    </el-dialog>
  </div>
</template>

<script>
  import {findGenScheme, pageGenScheme, removeGenScheme, saveGenScheme} from "./service";
  // import { parseTime } from '@/utils'
  import {mapGetters} from "vuex";
  import {DATA_STATUS} from "@/const/common";
  import {objectToString, validateNull} from "../../../util/validate";
  import {dictCodes} from "../../../api/dataSystem";
  import {MSG_TYPE_SUCCESS} from "../../../const/common";
  import {parseJsonItemForm, parseTreeData} from "../../../util/util";
  import {baseUrl} from "../../../config/env";
  import {getToken} from "../../../util/auth";
  import {fetchModuleMenu} from "../../sys/module/service";

  export default {
  components: {
  },
  name: "table_gen_genScheme",
  data() {
    return{
      ctx:baseUrl,
      showUpload:false,
      headers: {
        Authorization: getToken()
      },
      treeModuleData: [],
      checkedKeys: [],
      defaultProps: {
        children: "children",
        label: "label"
      },
      list: null,
      total: null,
      listLoading: true,
      listQuery: {
        page: 1,
        size: 20
      },
      viewTypeList:[],
      categoryList:[],
      tableList:[],
      form: {
        name: undefined,
        genTableName: undefined,
        packageName: undefined,
        moduleName: undefined,
        subModuleName: undefined,
        functionName: undefined,
        functionNameSimple: undefined,
        functionAuthor: undefined,
        genTableId: undefined,
        genCode: undefined,
        replaceFile: undefined,
        syncModule: undefined,
        parentModuleName: undefined,
        parentModuleId: undefined,
        status: undefined,
        description: undefined
      },
      statusOptions: [],
      filterFormText: '',
      dialogFormVisible: false,
      dialogModuleVisible: false,
      genSchemeAdd: false,
      genSchemeUpd: false,
      genSchemeDel: false,
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
  watch: {
    filterFormText(val) {
      this.$refs['formTree'].filter(val);
    }
  },
  created() {
    this.getList();
    this.gen_genScheme_edit = this.authorities.indexOf("gen_genScheme_edit") !== -1;
    this.gen_genScheme_delete = this.authorities.indexOf("gen_genScheme_delete") !== -1;
    dictCodes({codes:'sys_status'}).then(response => {
      this.statusOptions = response.data;
    });
  },
  methods: {
    getList() {
      this.listLoading = true;
      this.listQuery.isAsc = false;
      this.listQuery.queryConditionJson = parseJsonItemForm([{
        fieldName: 'loginId',value:this.listQuery.loginId
      },{
        fieldName: 'email',value:this.listQuery.email
      }])
      pageGenScheme(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    },
    getNodeData(data) {
      this.dialogModuleVisible = false;
      this.form.orgId = data.id;
      this.form.orgName = data.label;
    },

    handleModule() {
      fetchModuleMenu({extId: this.form.id}).then(response => {
        this.treeModuleData = parseTreeData(response.data);
        this.dialogModuleVisible = true;
      })
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
        findGenScheme({id:row.id}).then(response => {
          if(response.status==MSG_TYPE_SUCCESS) {
            var data = response.data;
            this.viewTypeList = data.viewTypeList
            this.categoryList = data.categoryList
            this.tableList = data.tableList
            this.form = response.data;
            this.form = data.genSchemeVo;
            this.form.password = "";
            this.form.status = objectToString(this.form.status)
            this.dialogFormVisible = true;
          }
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
          saveGenScheme(this.form).then((data) => {
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
        "此操作将永久删除该用户(用户名:" + row.loginId + "), 是否继续?",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        removeGenScheme(row.id).then((data) => {
            if (data.status == MSG_TYPE_SUCCESS) {
              this.getList();
            }
          });
      });
    },
    resetForm() {
      this.form = {
        name: undefined,
        genTableName: undefined,
        packageName: undefined,
        moduleName: undefined,
        subModuleName: undefined,
        functionName: undefined,
        functionNameSimple: undefined,
        functionAuthor: undefined,
        genTableId: undefined,
        genCode: undefined,
        replaceFile: undefined,
        syncModule: undefined,
        parentModuleName: undefined,
        parentModuleId: undefined,
        status: undefined,
        description: undefined
      };
      this.$refs['form']&&this.$refs['form'].resetFields();
    },
    /**
     * upload success
     *
     * [param] jsonData   服务器返回数据，已进行json转码
     * [param] field
     */
    cropUploadSuccess(rs, field) {
      console.log(rs)
      this.$store.commit('SET_AVATAR', rs.data);
      this.form.avatar=rs.data;
    }
  }
};
</script>
