
<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
      <el-form :inline="true">
        <el-form-item label="状态">
          <AvueCrudCheckbox v-model="listQuery.status" :dic="statusOptions"></AvueCrudCheckbox>
        </el-form-item>
        <el-form-item label="是否系统数据">
          <AvueCrudCheckbox v-model="listQuery.sysData" :dic="sysDataOptions"></AvueCrudCheckbox>
        </el-form-item>
        <el-form-item label="名称">
          <el-input class="filter-item input-normal" v-model="listQuery.name"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">查询</el-button>
          <el-button class="filter-item" style="margin-left: 10px;" @click="handleEdit" type="primary" icon="edit" v-if="sys_role_edit">添加
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="加载中..." border fit highlight-current-row style="width: 99%">

      <el-table-column align="center" label="所属组织">
        <template slot-scope="scope">
          <span>{{scope.row.orgName}}</span>
        </template>
      </el-table-column>

      <el-table-column label="角色名称">
        <template slot-scope="scope">
          <span>{{scope.row.name}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="是否系统数据">
        <template slot-scope="scope">
          <span>{{scope.row.sysData}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" class-name="status-col" label="数据范围">
        <template slot-scope="scope">
          <el-tag>{{scope.row.dataScope}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" class-name="status-col" label="状态">
        <template slot-scope="scope">
          <el-tag>{{scope.row.status}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="创建时间">
        <template slot-scope="scope">
          <span>{{scope.row.createdDate}}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="220">
        <template slot-scope="scope">
          <el-button v-if="sys_role_edit" icon="icon-edit" title="编辑" type="text" @click="handleEdit(scope.row)">
          </el-button>
          <el-button v-if="sys_role_lock" :icon="scope.row.status=='正常' ? 'icon-lock' : 'icon-unlock'" :title="scope.row.status=='正常' ? '锁定' : '解锁'" type="text" @click="handleLock(scope.row)">
          </el-button>
          <el-button v-if="sys_role_delete" icon="icon-delete" title="删除" type="text" @click="handleDelete(scope.row)">
          </el-button>
        </template>
      </el-table-column>

    </el-table>

    <div v-show="!listLoading" class="pagination-container">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.page" :page-sizes="[10,20,30, 50]" :page-size="listQuery.size" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="所属部门" prop="orgName">
          <el-input v-model="form.orgName" placeholder="选择部门" @focus="handleOrg()" readonly></el-input>
          <input type="hidden" v-model="form.orgId" />
        </el-form-item>
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="编码" prop="en">
          <el-input v-model="form.en"></el-input>
        </el-form-item>
        <el-form-item label="系统数据" prop="sysData">
          <AvueCrudRadio v-model="form.sysData" :dic="sysDataOptions"></AvueCrudRadio>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <AvueCrudRadio v-model="form.status" :dic="statusOptions"></AvueCrudRadio>
        </el-form-item>
        <el-form-item label="数据范围" prop="dataScope">
          <AvueCrudSelect v-model="form.dataScope" @input="handleDataScopeChange" :dic="dataScopeOptions" ></AvueCrudSelect>
        </el-form-item>
        <el-row :gutter="20" :span="24">
          <el-col :span="12">
            <el-form-item label="操作权限" prop="moduleIdList">
            <el-tree class="filter-tree" :data="treeModuleData" ref="treeModule" node-key="id"
                     show-checkbox default-expand-all :default-checked-keys="form.moduleIdList" @check="getNodeTreeModuleData">
            </el-tree>
            </el-form-item>
          </el-col>
          <el-col :span="10" v-show="formTreeOrgDataVisible">
            <el-form-item label="机构权限" prop="orgIdList">
            <el-tree class="filter-tree" ref="treeOrg" :data="treeOrgData" node-key="id"
                     show-checkbox default-expand-all :default-checked-keys="form.orgIdList" @check="getNodeTreeOrgData">
            </el-tree>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="form.description" placeholder=""></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel('form')">取 消</el-button>
        <el-button type="primary" @click="save('form')">保 存</el-button>
      </div>
    </el-dialog>

    <el-dialog title="选择机构" :visible.sync="dialogOrgVisible">
      <el-tree class="filter-tree" :data="treeOrgData" :default-checked-keys="checkedKeys"
               check-strictly node-key="id" highlight-current
               :props="defaultProps" @node-click="getNodeData" default-expand-all>
      </el-tree>
    </el-dialog>

  </div>
</template>

<script>
  import {pageRole, findRole, saveRole, removeRole, lockRole} from "./service";
  import {mapGetters} from "vuex";
  import waves from "@/directive/waves/index.js";
  import {fetchOrgTree} from "../org/service";
  import {objectToString, validateNull, validateNotNull} from "@/util/validate";
  import {parseTreeData} from "@/util/util";
  import {MSG_TYPE_SUCCESS} from "@/const/common";
  import {dictCodes} from "@/api/dataSystem";
  import {fetchModuleTree} from "../module/service";

  export default {
  name: "table_sys_role",
  directives: {
    waves
  },
  data() {
    return {
      treeModule:[],
      treeOrg:[],
      treeOrgData: [],
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
      form: {
        name: undefined,
        en: undefined,
        type: undefined,
        sysData: undefined,
        dataScope: undefined,
        moduleIdList: [],
        orgIdList: [],
        status: undefined,
        sort: undefined
      },
      roleId: undefined,
      roleCode: undefined,
      rules: {
        name: [
          {
            required: true,
            message: "角色名称不能为空",
            trigger: "blur"
          },
          {
            min: 3,
            max: 20,
            message: "长度在 3 到 20 个字符",
            trigger: "blur"
          }
        ],
        en: [
          {
            min: 3,
            max: 20,
            message: "长度在 3 到 20 个字符",
            trigger: "blur"
          }
        ],
        dataScope: [
          {
            required: true,
            message: "数据范围不能为空",
            trigger: "blur"
          }
        ],
        moduleIdList: [
          {
            required: true,
            message: "操作权限不能为空",
            trigger: "blur"
          }
        ],
        status: [
          {
            required: true,
            message: "状态不能为空",
            trigger: "blur"
          }
        ],
        sysData: [
          {
            required: true,
            message: "是否系统数据不能为空",
            trigger: "blur"
          }
        ]
      },
      statusOptions: [],
      dataScopeOptions: [],
      sysDataOptions: [],
      typeOptions: [],
      dialogFormVisible: false,
      dialogOrgVisible: false,
      formTreeOrgDataVisible: false,
      dialogStatus: "",
      textMap: {
        update: "编辑",
        create: "创建"
      },
      tableKey: 0
    };
  },
  created() {
    this.getList();
    this.sys_role_edit = this.authorities.indexOf("sys_role_edit") !== -1;
    this.sys_role_lock = this.authorities.indexOf("sys_role_lock") !== -1;
    this.sys_role_delete = this.authorities.indexOf("sys_role_delete") !== -1;

    fetchModuleTree().then(rs => {
      this.treeModuleData = parseTreeData(rs.data);
    })
    fetchOrgTree().then(response => {
      this.treeOrgData = parseTreeData(response.data);
    })
    dictCodes({codes:'sys_yes_no,sys_status,sys_role_scope'}).then(rs => {
      this.sysDataOptions = rs.data[0];
      this.statusOptions = rs.data[1];
      this.dataScopeOptions = rs.data[2];
    });
  },
  computed: {
    ...mapGetters(["authorities"])
  },
  methods: {
    getList() {
      this.listLoading = true;
      pageRole(this.listQuery).then(response => {
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
      this.dialogStatus = row && validateNotNull(row.id)? "update" : "create";
      if(this.dialogStatus == "create"){
        this.dialogFormVisible = true;
      }else{
        findRole(row.id).then(response => {
          this.form = response.data;
          this.formTreeOrgDataVisible = (this.form.dataScope == 5);
          if(validateNull(this.form.orgIdList)){
            this.form.orgIdList = []
          }
          this.form.status=objectToString(this.form.status)
          this.form.sysData=objectToString(this.form.sysData)
          this.form.dataScope=objectToString(this.form.dataScope)
          this.dialogFormVisible = true;
          if(this.$refs.treeModule){
            this.$refs.treeModule.setCheckedKeys(this.form.moduleIdList);
            this.$refs.treeOrg.setCheckedKeys(this.form.orgIdList);
          }
        });
      }
    },
    handleLock(row) {
      lockRole(row.id).then((data) => {
        if (data.status == MSG_TYPE_SUCCESS) {
          this.getList();
        }
      });
    },
    handleOrg() {
      fetchOrgTree().then(response => {
        this.treeOrgData = parseTreeData(response.data);
        this.dialogOrgVisible = true;
      })
    },
    handleDataScopeChange(value){
      this.formTreeOrgDataVisible = (value == 5);
    },
    getNodeTreeModuleData(data, obj) {
      this.form.moduleIdList = obj.checkedKeys;
    },
    getNodeTreeOrgData(data, obj) {
      this.form.orgIdList = obj.checkedKeys;
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    getNodeData(data) {
      this.dialogOrgVisible = false;
      this.form.orgId = data.id;
      this.form.orgName = data.label;
    },
    handleDelete(row) {
      this.$confirm(
        "此操作将永久删除该角色(角色名:" + row.name + "), 是否继续?",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        removeRole(row.id)
          .then((data) => {
            if (data.status == MSG_TYPE_SUCCESS) {
              this.getList();
            }
          });
      });
    },
    cancel(formName) {
      this.dialogFormVisible = false;
      this.$refs[formName].resetFields();
    },
    save(formName) {
      const set = this.$refs;
      set[formName].validate(valid => {
        if (valid) {
          saveRole(this.form).then((data) => {
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
    resetForm() {
      this.form = {
        name: undefined,
        en: undefined,
        type: undefined,
        sysData: undefined,
        dataScope: undefined,
        moduleIdList: [],
        orgIdList: [],
        status: undefined,
        sort: undefined
      };
      if(this.$refs['form']){
        this.$refs['form'].resetFields();
        this.$refs.treeOrg.setCheckedKeys([]);
        this.$refs.treeModule.setCheckedKeys([]);
      }
    }
  }
};
</script>
