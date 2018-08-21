
<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
      <el-button-group>
        <el-button type="primary" v-if="sys_org_edit" icon="plus" @click="handlerAdd">添加</el-button>
        <el-button type="primary" v-if="sys_org_edit" icon="edit" @click="handlerEdit">编辑</el-button>
        <el-button type="primary" v-if="sys_org_delete" icon="delete" @click="handleDelete">删除</el-button>
      </el-button-group>
    </div>

    <el-row :gutter="20">
      <el-col :span="8" style='margin-top:15px;'>
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
      <el-col :span="16" style='margin-top:15px;'>
        <el-card class="box-card">
          <el-form :label-position="labelPosition" label-width="80px" :model="form" ref="form">
            <el-form-item label="上级组织" prop="parentName">
              <el-input v-model="form.parentName" :disabled="formEdit" placeholder="选择部门" @focus="handleOrg()" readonly></el-input>
              <input type="hidden" v-model="form.parentId" />
            </el-form-item>
            <el-form-item label="名称" prop="name" :rules="[{required: true,message: '请输入名称'}]">
              <el-input v-model="form.name" :disabled="formEdit" ></el-input>
            </el-form-item>
            <el-form-item label="类型" prop="type" :rules="[{required: true,message: '请选择类型'}]">
              <AvueCrudRadio v-model="form.type" :dic="typeOptions" :disabled="formEdit" :rules="[{required: true,message: '请选择类型'}]"></AvueCrudRadio>
            </el-form-item>
            <el-form-item label="等级" prop="grade" :rules="[{required: true,message: '请选择等级'}]">
              <AvueCrudSelect v-model="form.grade" :dic="gradeOptions" :disabled="formEdit"></AvueCrudSelect>
            </el-form-item>
            <el-form-item label="排序" prop="sort" :rules="[{type: 'number',message: '序号必须为数字'}]">
              <el-input-number v-model="form.sort" :step="5"></el-input-number>
            </el-form-item>
            <el-form-item label="状态" prop="status" :rules="[{required: true,message: '请选择状态'}]">
              <AvueCrudRadio v-model="form.status" :dic="statusOptions" :disabled="formEdit"></AvueCrudRadio>
            </el-form-item>
            <el-form-item label="描述" prop="description">
              <el-input type="textarea" v-model="form.description" :disabled="formEdit"></el-input>
            </el-form-item>
            <el-form-item v-if="formStatus != ''">
              <el-button type="primary" @click="save">保存</el-button>
              <el-button @click="cancel">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    <el-dialog title="选择机构" :visible.sync="dialogOrgVisible">
      <el-tree class="filter-tree" :data="treeOrgData"
               check-strictly node-key="id" highlight-current @node-click="getNodeFormData" default-expand-all>
      </el-tree>
    </el-dialog>
  </div>
</template>

<script>
  import { fetchOrgTree, findOrg, saveOrg, removeOrg } from "./service";
  import { mapGetters } from 'vuex'
  import {parseTreeData} from "@/util/util";
  import {dictCodes} from "@/api/dataSystem";
  import {objectToString} from "@/util/validate";
  import {MSG_TYPE_SUCCESS} from "@/const/common";
  export default {
    name: 'menu',
    data() {
      return {
        treeOrgData: [],
        dialogOrgVisible: false,
        list: null,
        total: null,
        formEdit: true,
        filterText: '',
        formStatus: '',
        showElement: false,
        statusOptions: [],
        typeOptions: [],
        gradeOptions: [],
        listQuery: {
          name: undefined
        },
        treeData: [],
        labelPosition: 'right',
        form: {
          name: undefined,
          parentId: undefined,
          parentName: undefined,
          code: undefined,
          type: undefined,
          grade: undefined,
          sort: undefined,
          status: undefined,
          description: undefined
        },
        currentId: 0,
        sys_org_edit: false,
        sys_org_delete: false
      }
    },
    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      }
    },
    created() {
      this.getList()
      this.sys_org_edit = this.authorities.indexOf("sys_org_edit") !== -1;
      this.sys_org_delete = this.authorities.indexOf("sys_org_delete") !== -1;

      dictCodes({codes:'sys_status,sys_org_type,sys_org_grade'}).then(rs => {
        this.statusOptions = rs.data[0];
        this.typeOptions = rs.data[1];
        this.gradeOptions = rs.data[2];
      });
    },
    computed: {
      ...mapGetters([
        'authorities'
      ])
    },
    methods: {
      getList() {
        fetchOrgTree({all:true}).then(response => {
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
        findOrg(data.id).then(response => {
          this.form = response.data
          this.form.status=objectToString(this.form.status)
        })
        this.currentId = data.id
        this.showElement = true
      },

      getNodeFormData(data){
        this.dialogOrgVisible = false;
        this.form.parentId = data.id;
        this.form.parentName = data.label;
      },
      handlerEdit() {
        this.formEdit = false
        this.formStatus = "update";
      },
      handlerAdd() {
        this.resetForm()
        this.currentId == ''
        this.formEdit = false
        this.formStatus = "create";
      },
      handleOrg() {
        fetchOrgTree({extId: this.currentId}).then(response => {
          this.treeOrgData = parseTreeData(response.data);
          this.dialogOrgVisible = true;
        })
      },
      handleDelete() {
        this.$confirm('此操作将永久删除, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          removeOrg(this.currentId).then(() => {
            if (data.status == MSG_TYPE_SUCCESS) {
              this.getList()
              this.resetForm()
              this.cancel()
            }

          })
        })
      },
      save() {
        const set = this.$refs;
        set['form'].validate(valid => {
          if (valid) {
            saveOrg(this.form).then(() => {
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
        this.formEdit = true
        this.formStatus = ''
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

