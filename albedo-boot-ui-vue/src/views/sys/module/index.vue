
<template>
  <div class="app-container calendar-list-container">
    <el-row :gutter="20">
      <el-col :span="6" style='margin-top:15px;'>
        <el-input
          placeholder="输入关键字进行过滤"
          v-model="filterText">
        </el-input>
        <el-tree
          class="filter-tree"
          :data="treeData"
          ref="tree"
          node-key="id"
          highlight-current
          :filter-node-method="filterNode"
          @node-click="getNodeData"
          default-expand-all
        >
        </el-tree>
      </el-col>
      <el-col :span="18" style='margin-top:15px;'>
        <div class="filter-container">
            <el-form :inline="true">
              <el-form-item label="名称">
                <el-input style="width: 200px;" class="filter-item" v-model="listQuery.name"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">搜索</el-button>
                <el-button v-if="sys_module_edit" class="filter-item" style="margin-left: 10px;" @click="handleEdit" type="primary" icon="edit">添加</el-button>
              </el-form-item>
            </el-form>
        </div>
        <el-card class="box-card">
          <el-table :data="list" v-loading="listLoading" element-loading-text="加载中..." border fit highlight-current-row style="width: 99%">
            <el-table-column align="center" label="名称">
              <template slot-scope="scope">
                <span>{{scope.row.name}}</span>
              </template>
            </el-table-column>

            <el-table-column align="center" label="类型">
              <template slot-scope="scope">
                <span>{{scope.row.type}}</span>
              </template>
            </el-table-column>

            <el-table-column align="center" label="权限">
              <template slot-scope="scope">
                <span>{{scope.row.permission}}</span>
              </template>
            </el-table-column>

            <el-table-column align="center" label="请求方法">
              <template slot-scope="scope">
                <span>{{scope.row.requestMethod}}</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="链接">
              <template slot-scope="scope">
                <span>{{scope.row.url}}</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="序号">
              <template slot-scope="scope">
                <span>{{scope.row.sort}}</span>
              </template>
            </el-table-column>
            <el-table-column align="center" class-name="status-col" label="状态">
              <template slot-scope="scope">
                <el-tag>{{scope.row.status}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column align="center" label="更新时间">
              <template slot-scope="scope">
                <span>{{scope.row.lastModifiedDate}}</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="200">
              <template slot-scope="scope">
                <el-button v-if="sys_module_edit" size="small" type="success" @click="handleEdit(scope.row)">编辑
                </el-button>
                <el-button v-if="sys_module_delete" size="small" type="danger" @click="handleDelete(scope.row)">删除
                </el-button>
              </template>
            </el-table-column>

          </el-table>
          <div v-show="!listLoading" class="pagination-container">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.page" :page-sizes="[10,20,30, 50]" :page-size="listQuery.limit" layout="total, sizes, prev, pager, next, jumper" :total="total">
            </el-pagination>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-dialog title="选择模块" :visible.sync="dialogModuleVisible">
      <el-tree class="filter-tree" :data="treeModuleData"
               check-strictly node-key="id" highlight-current @node-click="getNodeFormData" default-expand-all>
      </el-tree>
    </el-dialog>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :label-position="labelPosition" label-width="80px" :model="form" ref="form">
        <el-form-item label="上级模块" prop="parentName">
          <el-input v-model="form.parentName" placeholder="选择模块" @focus="handleModule()" readonly></el-input>
          <input type="hidden" v-model="form.parentId" />
        </el-form-item>
        <el-form-item label="名称" prop="name" :rules="[{required: true,message: '请输入名称'}]">
          <el-input v-model="form.name" ></el-input>
        </el-form-item>
        <el-form-item label="服务名称" prop="microservice">
          <el-input v-model="form.microservice" ></el-input>
        </el-form-item>
        <el-form-item label="图标" prop="iconCls">
          <el-input v-model="form.iconCls" ></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="type" :rules="[{required: true,message: '请选择类型'}]">
          <AvueCrudRadio v-model="form.type" :dic="typeOptions" :rules="[{required: true,message: '请选择类型'}]"></AvueCrudRadio>
        </el-form-item>
        <el-form-item label="权限" prop="permission">
          <el-input v-model="form.permission" ></el-input>
        </el-form-item>
        <el-form-item label="请求方法" prop="grade" :rules="[{required: true,message: '请选择等级'}]">
          <AvueCrudSelect v-model="form.grade" :dic="methodOptions"></AvueCrudSelect>
        </el-form-item>
        <el-form-item label="操作目标" prop="target">
          <el-input v-model="form.target" ></el-input>
        </el-form-item>
        <el-form-item label="前端组件" prop="component">
          <el-input v-model="form.component" ></el-input>
        </el-form-item>
        <el-form-item label="链接" prop="url">
          <el-input v-model="form.url" ></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sort" :rules="[{type: 'number',message: '序号必须为数字'}]">
          <el-input v-model="form.sort" placeholder="请输入排序"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status" :rules="[{required: true,message: '请选择状态'}]">
          <AvueCrudRadio v-model="form.status" :dic="statusOptions"></AvueCrudRadio>
        </el-form-item>
        <el-form-item label="描述" prop="description">
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
  import { fetchModuleTree, findModule, saveModule, removeModule } from "./moduleService";
  import { mapGetters } from 'vuex'
  import {parseTreeData} from "../../../util/util";
  import {dictCodes} from "../../../api/dataSystem";
  import {objectToString, validateNotNull} from "../../../util/validate";
  import {MSG_TYPE_SUCCESS} from "../../../const/common";
  export default {
    name: 'menu',
    data() {
      return {
        treeModuleData: [],
        dialogModuleVisible: false,
        dialogFormVisible: false,
        list: null,
        total: null,
        listLoading: true,
        listQuery: {
          page: 1,
          limit: 20
        },
        formEdit: true,
        filterText: '',
        formStatus: '',
        showElement: false,
        statusOptions: [],
        typeOptions: [],
        methodOptions: [],
        treeData: [],
        labelPosition: 'right',
        form: {
          name: undefined,
          parentId: undefined,
          parentName: undefined,
          microservice: undefined,
          type: undefined,
          iconCls: undefined,
          permission: undefined,
          requestMethod: undefined,
          target: undefined,
          url: undefined,
          sort: undefined,
          status: undefined,
          description: undefined
        },
        dialogStatus: 'create',
        textMap: {
          update: '编辑',
          create: '创建'
        },
        currentId: 0,
        sys_module_edit: false,
        sys_module_delete: false
      }
    },
    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      }
    },
    created() {
      this.getList()
      this.sys_module_edit = this.authorities.indexOf("sys_module_edit") !== -1;
      this.sys_module_delete = this.authorities.indexOf("sys_module_delete") !== -1;

      dictCodes({codes:'sys_status,sys_module_type,sys_request_method'}).then(rs => {
        this.statusOptions = rs.data[0];
        this.typeOptions = rs.data[1];
        this.methodOptions = rs.data[2];
      });
    },
    computed: {
      ...mapGetters([
        'authorities'
      ])
    },
    methods: {
      getList() {
        fetchModuleTree({all:true}).then(response => {
          this.treeData = parseTreeData(response.data);
        })
      },
      filterNode(value, data) {
        if (!value) return true
        return data.label.indexOf(value) !== -1
      },
      getNodeData(data) {
        if(this.formStatus == 'create'){
          this.formEdit=true;
          this.formStatus='';
        }
        findModule(data.id).then(response => {
          this.form = response.data
          this.form.status=objectToString(this.form.status)
        })
        this.currentId = data.id
        this.showElement = true
      },

      getNodeFormData(data){
        this.dialogModuleVisible = false;
        this.form.parentId = data.id;
        this.form.parentName = data.label;
      },

      handleFilter() {
        this.listQuery.page = 1;
        this.getList();
      },
      handleSizeChange(val) {
        this.listQuery.limit = val;
        this.getList();
      },
      handleCurrentChange(val) {
        this.listQuery.page = val;
        this.getList();
      },
      handleEdit(row) {
        this.resetTemp();
        this.dialogStatus = row && validateNotNull(row.id)? "update" : "create";
        if(this.dialogStatus == "create"){
          this.dialogFormVisible = true;
        }else{
          findModule(row.id).then(response => {
            this.form = response.data;
            this.form.status=objectToString(this.form.status)
            this.dialogFormVisible = true;
          });
        }
      },
      handleModule() {
        fetchModuleTree({extId: this.currentId}).then(response => {
          this.treeModuleData = parseTreeData(response.data);
          this.dialogModuleVisible = true;
        })
      },
      handleDelete() {
        this.$confirm('此操作将永久删除, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          removeModule(this.currentId).then(() => {
            if (data.status == MSG_TYPE_SUCCESS) {
              this.getList();
            }
          })
        })
      },
      save() {
        const set = this.$refs;
        set['form'].validate(valid => {
          if (valid) {
            saveModule(this.form).then(() => {
              this.getList()
              this.$notify({
                title: '成功',
                message: '保存成功',
                type: 'success',
                duration: 2000
              })
            })
          } else {
            return false;
          }
        });
      },
      cancel() {
        this.dialogFormVisible = false;
        this.$refs['form'].resetFields();
      },
      resetForm() {
        this.form = {
          permission: undefined,
          name: undefined,
          menuId: undefined,
          parentId: this.currentId,
          url: undefined,
          icon: undefined,
          sort: undefined,
          component: undefined,
          type: undefined,
          method: undefined
        }
        if(this.$refs['form']){
          this.$refs['form'].resetFields();
        }
      }
    }
  }
</script>

