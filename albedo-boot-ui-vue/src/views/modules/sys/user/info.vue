
<template>
  <div class="app-container calendar-list-container">
    <el-row>
      <el-col :span="12">
        <div class="grid-content bg-purple">
          <el-form :model="form" ref="form" label-width="100px" class="demo-ruleForm">
            <el-form-item label="头像">
              <el-upload
                class="avatar-uploader"
                :headers="headers"
                name="uploadFile"
                :action="ctx+'/file/upload'"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload">
                <img v-if="imageUrl" :src="imageUrl" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
            </el-form-item>
            <el-form-item label="用户名" prop="username">
              <el-input type="text" :value="userInfo.loginId" disabled></el-input>
            </el-form-item>
            <el-form-item label="原密码" prop="oldPassword" :rules="[{validator: validatePass}]">
              <el-input type="password" v-model="form.oldPassword" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="newPassword" :rules="[{validator: validatePass}]">
              <el-input type="password" v-model="form.newPassword" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword" :rules="[{validator: validateConfirmPass}]">
              <el-input type="password" v-model="form.confirmPassword" auto-complete="off" ></el-input>
            </el-form-item>
            <!--<el-form-item label="手机号" prop="phonecropUploadSuccess">-->
              <!--<el-input v-model="form.phone" placeholder="验证码登录使用"></el-input>-->
            <!--</el-form-item>-->
            <el-form-item>
              <el-button type="primary" @click="submitForm('form')">提交</el-button>
              <el-button @click="resetForm('form')">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>
  </div>
</template>


<script>
import { mapState } from "vuex";
import myUpload from "vue-image-crop-upload";
import { getToken } from "@/util/auth";
import ElFormItem from "element-ui/packages/form/src/form-item.vue";
import {MSG_TYPE_SUCCESS} from "@/const/common";
import {isValidateMobile, validateNull} from "@/util/validate";
import {getCtxFile} from "@/util/util";
import {changePassword} from "./service";
import {baseUrl} from "@/config/env";

export default {
  components: {
    ElFormItem,
    'my-upload': myUpload
  },
  data: function () {
    return {
      ctx: baseUrl,
      headers: {
        Authorization: getToken()
      },
      fileList: [],
      show: false,
      imageUrl: undefined,
      form: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: '',
        avatar: ''
      },
      validatePhone: (rule, value, callback) => {
        isValidateMobile(rule, value, callback)
      },
      validatePass: (rule, value, callback) => {
        console.log(value)
        if (value === '') {
          callback(new Error('请输入密码'));
          return;
        }
        callback();
      },
      validateConfirmPass: (rule, value, callback) => {
        console.log(this.form.newPassword)
        if (value === '') {
          callback(new Error('请再次输入密码'));
          return;
        } else if (value !== this.form.newPassword) {
          callback(new Error('两次输入密码不一致!'));
          return;
        }
        callback();
      }
    }
  },
  created() {
    this.form.avatar = this.userInfo.avatar
    this.imageUrl = getCtxFile(this.form.avatar);
  },
  computed: {
    ...mapState({
      userInfo: state => state.user.userInfo
    })
  },
  methods: {
    handleAvatarSuccess(rs, file) {
      console.log(rs)
      console.log(file)
      if(rs.status == MSG_TYPE_SUCCESS){
        this.imageUrl = getCtxFile(rs.data[0].id);
        this.form.avatar=rs.data[0].id;
      }
    },
    beforeAvatarUpload(file) {
      console.log(file)
        const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
        const isLt2M = file.size / 1024 / 1024 < 2;

        if (!isJPG) {
          this.$message.error('上传头像图片只能是 JPG/PNG 格式!');
        }
        if (!isLt2M) {
          this.$message.error('上传头像图片大小不能超过 2MB!');
        }
        return isJPG && isLt2M;
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          changePassword(this.form).then((data) => {
            if (data.status == MSG_TYPE_SUCCESS) {
              this.$confirm(
                "修改成功，建议您重新登录",
                "提示",
                {
                  confirmButtonText: "确定",
                  // cancelButtonText: "取消",
                  type: "warning"
                }
              ).then(() => {
                this.$store.dispatch('LogOut').then(() => {
                  location.reload() // 为了重新实例化vue-router对象 避免bug
                });
              });
            }
          });
        } else {
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    },
    toggleShow() {
      this.show = !this.show
    },
    /**
     * upload success
     *
     * [param] jsonData   服务器返回数据，已进行json转码
     * [param] field
     */
    cropUploadSuccess(rs, field) {
      if(rs.status == MSG_TYPE_SUCCESS){
        this.$store.commit('SET_AVATAR', rs.data[0].id);
      }
    }
  }
};
</script>
