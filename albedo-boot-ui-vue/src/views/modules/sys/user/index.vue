
<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
      <el-form :inline="true">
        <el-form-item label="用户名">
          <el-input class="filter-item input-normal" v-model="listQuery.loginId"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input class="filter-item input-normal" v-model="listQuery.email"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">查询</el-button>
          <el-button v-if="sys_user_edit" class="filter-item" style="margin-left: 10px;" @click="handleEdit" type="primary" icon="edit">添加</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="加载中..." border fit highlight-current-row style="width: 99%">

      <el-table-column align="center" label="所属组织">
        <template slot-scope="scope">
          <span>{{scope.row.orgName}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="用户名">
        <template slot-scope="scope">
          <span>
            <img v-if="scope.row.avatar" class="user-avatar" style="width: 20px; height: 20px; border-radius: 50%;" :src="getFilePath(scope.row.avatar)">
            {{scope.row.loginId}}
          </span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="邮件">
        <template slot-scope="scope">
          <span>
            {{scope.row.email}}
          </span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="手机号">
        <template slot-scope="scope">
          <span>{{scope.row.phone}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="角色">
        <template slot-scope="scope">
          <span v-for="role in scope.row.roles">{{role.name}} </span>
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

      <el-table-column align="center" fixed="right" label="操作" v-if="sys_user_edit || sys_user_lock || sys_user_delete">
        <template slot-scope="scope">
          <el-button v-if="sys_user_edit" icon="icon-edit" title="编辑" type="text" @click="handleEdit(scope.row)">
          </el-button>
          <el-button v-if="sys_user_lock" :icon="scope.row.status=='正常' ? 'icon-lock' : 'icon-unlock'" :title="scope.row.status=='正常' ? '锁定' : '解锁'" type="text" @click="handleLock(scope.row)">
          </el-button>
          <el-button v-if="sys_user_delete" icon="icon-delete" title="删除" type="text" @click="handleDelete(scope.row)">
          </el-button>
        </template>
      </el-table-column>

    </el-table>

    <div v-show="!listLoading" class="pagination-container">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.page" :page-sizes="[10,20,30, 50]" :page-size="listQuery.size" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>
    <el-dialog title="选择机构" :visible.sync="dialogOrgVisible">
      <el-tree class="filter-tree" :data="treeOrgData" :default-checked-keys="checkedKeys"
               check-strictly node-key="id" highlight-current
               :props="defaultProps" @node-click="getNodeData" default-expand-all>
      </el-tree>
    </el-dialog>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :model="form" ref="form" label-width="100px">
        <el-form-item label="头像" prop="avatar">
          <my-upload field="uploadFile" @crop-upload-success="cropUploadSuccess" v-model="showUpload"
                     :width="300" :height="300" :url="ctx+'/file/upload'" :headers="headers" img-format="png"></my-upload>
          <img :src="getFilePath(form.avatar)" class="header-img" />
          <input type="hidden" v-model="form.avatar" />
          <el-button type="primary" @click="showUpload = !showUpload" size="mini">选择
            <i class="el-icon-upload el-icon--right"></i>
          </el-button>
        </el-form-item>
        <el-form-item label="所属部门" prop="orgName" :rules="[{required: true,message: '请选择部门'}]">
          <el-input v-model="form.orgName" placeholder="选择部门" @focus="handleOrg()" readonly></el-input>
          <input type="hidden" v-model="form.orgId" />
        </el-form-item>

        <el-form-item label="用户名" prop="loginId" :rules="[
          {required: true,message: '请输入账户'},
          {min: 3,max: 20,message: '长度在 3 到 20 个字符'},
          {validator:validateUnique}
        ]">
          <el-input v-model="form.loginId" placeholder="请输用户名"></el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password" :rules="[{validator: validatePass}]">
          <el-input type="password" v-model="form.password" :placeholder="this.dialogStatus == 'create' ? '请输入密码' : '若不修改密码，请留空'" ></el-input>
        </el-form-item>

        <el-form-item label="确认密码" placeholder="请再次输入密码" prop="confirmPassword" :rules="[{validator: validateConfirmPass}]">
          <el-input type="password" v-model="form.confirmPassword"></el-input>
        </el-form-item>

        <el-form-item label="手机号" prop="phone" :rules="[{validator:validatePhone}]">
          <el-input v-model="form.phone" placeholder="验证码登录使用"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email" :rules="[{ type: 'email',message: '请填写正确邮箱' }]">
          <el-input v-model="form.email"></el-input>
        </el-form-item>

        <el-form-item label="角色" prop="roleIdList" :rules="[{required: true,message: '请选择角色' }]">
          <el-select class="filter-item" v-model="form.roleIdList" placeholder="请选择" multiple>
            <el-option v-for="item in rolesOptions" :key="item.id" :label="item.name"
                       :value="item.id">
              <span style="float: left">{{ item.name }}</span>
              <!--<span style="float: right; color: #8492a6; font-size: 13px">{{ item.roleCode }}</span>-->
            </el-option>
          </el-select>
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
  </div>
</template>

<script>
  import {pageUser, findUser, saveUser, removeUser, lockUser} from "./service";
import waves from "@/directive/waves/index.js";
import myUpload from "vue-image-crop-upload";
// import { parseTime } from '@/utils'
import { mapGetters } from "vuex";
import ElRadioGroup from "element-ui/packages/radio/src/radio-group";
import ElOption from "element-ui/packages/select/src/option";
import {DATA_STATUS} from "@/const/common";
import {fetchOrgTree} from "../org/service";
import {comboRoleList} from "../role/service";
import {
  isValidateMobile,
  isValidateUnique,
  objectToString, toStr,
  validateNull
} from "@/util/validate";
import {dictCodes} from "@/api/dataSystem";
import {MSG_TYPE_SUCCESS} from "@/const/common";
import {parseJsonItemForm, parseTreeData, getCtxFile} from "@/util/util";
import {baseUrl} from "@/config/env";
import {getToken} from "@/util/auth";

export default {
  components: {
    ElOption,
    ElRadioGroup,
    'my-upload': myUpload
  },
  name: "table_sys_user",
  directives: {
    waves
  },
  data() {
    return{
      ctx:baseUrl,
      showUpload:false,
      headers: {
        Authorization: getToken()
      },
      treeOrgData: [],
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
        avatar: undefined,
        loginId: undefined,
        password: undefined,
        confirmPassword: undefined,
        status: undefined,
        roleIdList: [],
        orgId: undefined,
        phone: undefined,
        email: undefined,
        description: undefined
      },
      getFilePath(path){
        return getCtxFile(path)
      },
      validateUnique: (rule, value, callback,test) => {
        console.log(test)
        console.log(rule)
        isValidateUnique(rule, value, callback, '/sys/user/checkByProperty?id='+toStr(this.form.id))
      },
      validatePhone: (rule, value, callback) => {
        isValidateMobile(rule, value, callback)
      },
      validatePass: (rule, value, callback) => {
        if(validateNull(this.form.id)){
          if (value === '') {
            callback(new Error('请输入密码'));
            return;
          }
        }
        callback();
      },
      validateConfirmPass: (rule, value, callback) => {
        if(!validateNull(this.form.password)){
          if (value === '') {
            callback(new Error('请再次输入密码'));
            return;
          } else if (value !== this.form.password) {
            callback(new Error('两次输入密码不一致!'));
            return;
          }
        }
        callback();
      },
      statusOptions: [],
      rolesOptions: [],
      dialogFormVisible: false,
      dialogOrgVisible: false,
      userAdd: false,
      userUpd: false,
      userDel: false,
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
    this.sys_user_edit = this.authorities.indexOf("sys_user_edit") !== -1;
    this.sys_user_lock = this.authorities.indexOf("sys_user_lock") !== -1;
    this.sys_user_delete = this.authorities.indexOf("sys_user_delete") !== -1;
    comboRoleList().then(response => {
      this.rolesOptions = response.data;
    });
    dictCodes({codes:'sys_status'}).then(response => {
      this.statusOptions = response.data[0];
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
      pageUser(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    },
    getNodeData(data) {
      this.dialogOrgVisible = false;
      this.form.orgId = data.id;
      this.form.orgName = data.label;
    },
    handleOrg() {
      fetchOrgTree().then(response => {
        this.treeOrgData = parseTreeData(response.data);
        this.dialogOrgVisible = true;
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
        findUser(row.id).then(response => {
          this.form = response.data;
          this.form.password="";
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
          saveUser(this.form).then((data) => {
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
        removeUser(row.id).then((data) => {
            if (data.status == MSG_TYPE_SUCCESS) {
              this.getList();
            }
          });
      });
    },
    handleLock(row) {
      lockUser(row.id).then((data) => {
        if (data.status == MSG_TYPE_SUCCESS) {
          this.getList();
        }
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
    },
    /**
     * upload success
     *
     * [param] jsonData   服务器返回数据，已进行json转码
     * [param] field
     */
    cropUploadSuccess(rs, field) {
      if(rs.status == MSG_TYPE_SUCCESS){
        this.form.avatar=rs.data[0].id;
      }
    }
  }
};
</script>
