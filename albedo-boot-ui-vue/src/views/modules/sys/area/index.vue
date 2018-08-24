
<template>
  <div class="app-container calendar-list-container">
    <el-row :gutter="20">
      <el-col :span="5">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>区域</span>
            <el-button type="text" class="card-heard-btn" icon="icon-filesearch" title="搜索" @click="searchTree=(searchTree ? false:true)"></el-button>
            <el-button type="text" class="card-heard-btn" icon="icon-reload" title="刷新" @click="getTree()"></el-button>
          </div>
          <el-input v-show="searchTree"
                    placeholder="输入关键字进行过滤"
                    v-model="filterText">
          </el-input>
          <el-tree
            class="filter-tree"
            :data="treeData"
            ref="leftTree"
            node-key="id"
            highlight-current
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
            @node-click="getNodeData"
            default-expand-all >
          </el-tree>
        </el-card>
      </el-col>
      <el-col :span="19">
        <div class="filter-container">
          <el-form :inline="true">
            <el-form-item label="名称">
              <el-input class="filter-item input-normal" v-model="listQuery.name"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查询</el-button>
              <el-button v-if="sys_area_edit" class="filter-item" style="margin-left: 10px;" @click="handleEdit" type="primary" icon="edit">添加</el-button>
            </el-form-item>
          </el-form>
        </div>
        <el-table :data="list" :default-sort = "{prop: 'lastModifiedDate', order: 'descending'}" v-loading="listLoading" element-loading-text="加载中..."
                  border fit highlight-current-row style="width: 99%">
          <el-table-column align="center" label="名称">
            <template slot-scope="scope">
              <span><i :class="scope.row.iconCls"></i>{{scope.row.name}}</span>
            </template>
          </el-table-column>

          <el-table-column align="center" label="编码">
            <template slot-scope="scope">
              <span>{{scope.row.code}}</span>
            </template>
          </el-table-column>

          <el-table-column align="center" label="简称">
            <template slot-scope="scope">
              <span>{{scope.row.shortName}}</span>
            </template>
          </el-table-column>

          <el-table-column align="center" label="等级">
            <template slot-scope="scope">
              <span>{{scope.row.level}}</span>
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
          <el-table-column prop="lastModifiedDate" align="center" label="更新时间" :sortable="true">
            <template slot-scope="scope">
              <span>{{scope.row.lastModifiedDate}}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作">
            <template slot-scope="scope">
              <el-button v-if="sys_area_edit" icon="icon-edit" title="编辑" type="text" @click="handleEdit(scope.row)">
              </el-button>
              <el-button v-if="sys_area_lock" :icon="scope.row.status=='正常' ? 'icon-lock' : 'icon-unlock'" :title="scope.row.status=='正常' ? '锁定' : '解锁'" type="text" @click="handleLock(scope.row)">
              </el-button>
              <el-button v-if="sys_area_delete" icon="icon-delete" title="删除" type="text" @click="handleDelete(scope.row)">
              </el-button>
            </template>
          </el-table-column>

        </el-table>
        <div v-show="!listLoading" class="pagination-container">
          <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.page" :page-sizes="[10,20,30, 50]" :page-size="listQuery.size" layout="total, sizes, prev, pager, next, jumper" :total="total">
          </el-pagination>
        </div>
      </el-col>
    </el-row>
    <el-dialog title="选择区域" :visible.sync="dialogAreaVisible">
      <el-input placeholder="输入关键字进行过滤"
                v-model="filterFormText">
      </el-input>
      <el-tree class="filter-tree" ref="formTree" :data="treeAreaData"
               check-strictly node-key="id" highlight-current @node-click="getNodeFormData"
               :filter-node-method="filterNode" default-expand-all>
      </el-tree>
    </el-dialog>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :label-position="labelPosition" label-width="80px" :model="form" ref="form">
        <el-form-item label="上级区域" prop="parentName">
          <el-input v-model="form.parentName" placeholder="选择区域" @focus="handleArea()" readonly></el-input>
          <input type="hidden" v-model="form.parentId" />
        </el-form-item>
        <el-form-item label="名称" prop="name" :rules="[{required: true,message: '请输入名称'}]">
          <el-input v-model="form.name" ></el-input>
        </el-form-item>
        <el-form-item label="简称" prop="shortName">
          <el-input v-model="form.shortName" ></el-input>
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" ></el-input>
        </el-form-item>
        <el-form-item label="等级" prop="level">
          <AvueCrudSelect v-model="form.level" :dic="levelOptions"></AvueCrudSelect>
        </el-form-item>
        <el-form-item label="排序" prop="sort" :rules="[{type: 'number',message: '序号必须为数字'}]">
          <el-input-number v-model="form.sort" :step="5"></el-input-number>
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
  import {fetchAreaTree, findArea, saveArea, removeArea, pageArea, lockArea} from "./service";
  import { mapGetters } from 'vuex'
  import {parseJsonItemForm, parseTreeData} from "@/util/util";
  import {dictCodes} from "@/api/dataSystem";
  import {isValidateUnique, objectToString, toStr, validateNotNull} from "@/util/validate";
  import {MSG_TYPE_SUCCESS} from "@/const/common";
  export default {
    name: 'area',
    data() {
      return {
        treeAreaData: [],
        dialogAreaVisible: false,
        dialogFormVisible: false,
        list: null,
        total: null,
        listLoading: true,
        listQuery: {
          page: 1,
          size: 20
        },
        formEdit: true,
        filterText: '',
        filterFormText: '',
        formStatus: '',
        statusOptions: [],
        levelOptions: [],
        searchTree: false,
        treeData: [],
        labelPosition: 'right',
        form: {
          name: undefined,
          parentId: undefined,
          parentName: undefined,
          shortName: undefined,
          level: undefined,
          code: undefined,
          sort: undefined,
          status: undefined,
          description: undefined
        },
        validateUnique: (rule, value, callback) => {
          isValidateUnique(rule, value, callback, '/sys/area/checkByProperty?id='+toStr(this.form.id))
        },
        dialogStatus: 'create',
        textMap: {
          update: '编辑',
          create: '创建'
        },
        sys_area_edit: false,
        sys_area_delete: false,
        currentNode: {}
      }
    },
    watch: {
      filterText(val) {
        this.$refs['leftTree'].filter(val);
      },
      filterFormText(val) {
        this.$refs['formTree'].filter(val);
      }
    },
    created() {
      this.getTree()
      this.getList()
      this.sys_area_edit = this.authorities.indexOf("sys_area_edit") !== -1;
      this.sys_area_lock = this.authorities.indexOf("sys_area_lock") !== -1;
      this.sys_area_delete = this.authorities.indexOf("sys_area_delete") !== -1;

      dictCodes({codes:'sys_status,sys_area_type'}).then(rs => {
        this.statusOptions = rs.data[0];
        this.levelOptions = rs.data[1];
      });
    },
    computed: {
      ...mapGetters([
        'authorities'
      ])
    },
    methods: {
      getList() {
        this.listLoading = true;
        this.listQuery.isAsc = false;
        this.listQuery.queryConditionJson = parseJsonItemForm([{
          fieldName: 'name',value:this.listQuery.name
        },{
          fieldName: 'parentId',value:this.listQuery.parentId
        }])
        pageArea(this.listQuery).then(response => {
          this.list = response.data;
          this.total = response.total;
          this.listLoading = false;
        });
      },
      getTree() {
        fetchAreaTree({all:true,ltLevel:2}).then(response => {
          this.treeData = parseTreeData(response.data);
        })
      },
      filterNode(value, data) {
        if (!value) return true
        return data.label.indexOf(value) !== -1
      },
      getNodeData(data) {
        this.listQuery.parentId = data.id
        this.currentNode = data;
        this.getList()
      },
      getNodeFormData(data){
        this.dialogAreaVisible = false;
        this.form.parentId = data.id;
        this.form.parentName = data.label;
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
          this.form.parentId = this.currentNode.id
          this.form.parentName = this.currentNode.label;
        }else{
          findArea(row.id).then(response => {
            this.form = response.data;
            this.form.status=objectToString(this.form.status)
            this.form.isShow=objectToString(this.form.isShow)
            this.dialogFormVisible = true;
          });
        }
      },
      handleLock(row) {
        lockArea(row.id).then((data) => {
          if (data.status == MSG_TYPE_SUCCESS) {
            this.getList();
          }
        });
      },
      handleArea() {
        fetchAreaTree({extId: this.form.id}).then(response => {
          this.treeAreaData = parseTreeData(response.data);
          this.dialogAreaVisible = true;
        })
      },
      handleDelete(row) {
        this.$confirm('此操作将永久删除, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          removeArea(row.id).then(() => {
            if (data.status == MSG_TYPE_SUCCESS) {
              this.getList();
              this.getTree();
            }
          })
        })
      },
      save() {
        const set = this.$refs;
        set['form'].validate(valid => {
          if (valid) {
            saveArea(this.form).then(() => {
              this.getList()
              this.getTree();
              this.dialogFormVisible = false;
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
          parentId: undefined,
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

