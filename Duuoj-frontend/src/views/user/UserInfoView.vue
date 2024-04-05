<template>
  <div class="mhy-container mhy-account-center-content">
    <div
      class="mhy-account-center-content-container mhy-account-center-collection"
    >
      <div class="mhy-account-center__subheader">
        <span>个人简介</span>
        <div class="mhy-account-center-collection-menu">
          <div
            class="mhy-button mhy-account-center-collection-menu__create mhy-button-outlined"
          >
            <button class="mhy-button__button" @click="updateModal">
              编辑
            </button>
          </div>
        </div>
      </div>
    </div>
    <div style="margin: 20px">
      <el-descriptions class="margin-top" :column="3" border>
        <el-descriptions-item>
          <template #label>
            <i class="el-icon-user"></i>
            头像
          </template>
          <div>
            <el-image
              :src="loginUser.userAvatar"
              style="width: 50px; height: 50px"
            ></el-image>
          </div>
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <i class="el-icon-user"></i>
            账户名
          </template>
          {{ loginUser.userAccount }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <i class="el-icon-user-solid"></i>
            昵称
          </template>
          {{ loginUser.userName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <i class="el-icon-tickets"></i>
            年龄
          </template>
          <el-tag size="small">{{ loginUser.age }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <i class="el-icon-tickets"></i>
            性别
          </template>
          <el-tag size="small"
            >{{ loginUser.gender == 1 ? "男" : "女" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <i class="el-icon-tickets"></i>
            邮箱Email
          </template>
          {{ loginUser.email }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <i class="el-icon-office-building"></i>
            个人描述
          </template>
          {{ loginUser.userProfile }}
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <!--编辑窗口-->
    <el-dialog
      title="修改信息"
      v-loading="loading"
      width="50%"
      :close-on-click-modal="true"
      center
      v-model="data.box"
    >
      <div>
        <el-form
          status-icon
          :rules="data.rules"
          ref="form"
          :model="data.form"
          label-width="120px"
        >
          <el-row>
            <el-col :span="12">
              <el-form-item label="头像：">
                <el-upload
                  class="avatar-uploader"
                  action=""
                  ref="upload"
                  :show-file-list="false"
                  :auto-upload="false"
                  :before-upload="beforeUpload"
                  :on-change="handleChange"
                  :on-remove="handleRemove"
                >
                  <el-avatar
                    v-if="data.form.avatar"
                    :src="data.form.avatar"
                    shape="circle"
                    :size="100"
                  />
                  <i v-else class="el-icon-plus avatar-uploader-icon" />
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="昵称" prop="nickname">
                <el-input
                  v-model="data.form.nickname"
                  placeholder="请输入昵称"
                  clearable
                >
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="data.form.gender">
                  <el-radio label="1">男</el-radio>
                  <el-radio label="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年龄" prop="age">
                <el-input
                  v-model="data.form.age"
                  placeholder="请输入年龄"
                  clearable
                >
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="邮箱" prop="email">
                <el-input
                  v-model="data.form.email"
                  placeholder="请输入邮箱"
                  clearable
                >
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="个人简介" prop="userProfile">
                <el-input
                  type="textarea"
                  resize="none"
                  v-model="data.form.userProfile"
                  placeholder="请输入个人简介"
                  clearable
                >
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <div style="text-align: center">
            <el-button type="primary" style="width: 100px" @click="submitFun"
              >提交
            </el-button>
            <el-button
              type="primary"
              plain
              style="width: 100px"
              @click="data.box = false"
              >取消
            </el-button>
          </div>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { getCurrentInstance, onMounted, ref } from "vue";
import store from "@/store";
import { LoginUserVO, UserControllerService } from "../../../generated";
import { Notification } from "@arco-design/web-vue";
import { nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";

const router = useRouter();

let loginUser = store.state.user.loginUser;
const data = ref({
  loading: false,
  box: false,
  form: {
    avatar: loginUser.userAvatar, //回显头像
    nickname: loginUser.userName,
    gender: loginUser.gender == "男" ? 1 : 2,
    file: null,
    age: loginUser.age,
    email: loginUser.email,
    userProfile: loginUser.userProfile,
  },
  rules: {
    nickname: [{ required: true, message: "请输入昵称", trigger: "change" }],
    gender: [{ required: true, message: "请选择性别", trigger: "change" }],
  },
});
const reload = () => {
  window.location.reload();
};
const beforeUpload = (file) => {
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    Notification.error("上传头像图片大小不能超过 2MB!");
  }
  return isLt2M;
};
const handleRemove = (file, fileList) => {
  data.value.form.file = null;
};
const handleChange = (file, fileList) => {
  console.info(fileList);
  data.value.form.file = file;
  let URL = window.URL || window.webkitURL;
  data.value.form.avatar = URL.createObjectURL(file.raw);
};
const updateModal = () => {
  //打开窗口
  // if (data.value.form) {
  //   data.value.form
  // }
  data.value.form.gender = data.value.form.gender + "";
  data.value.box = true;
};

const submitFun = async () => {
  //提交
  const res = await UserControllerService.updateMyUserUsingPost(
    data.value.form
  );
  if (res.code === 0) {
    Notification.info("修改成功");
    data.value.box = false;
    reload();
  } else {
    Notification.info("修改失败");
  }
};
</script>

<style scoped>
.mhy-account-center-content {
  width: 700px;
  float: right;
}

.mhy-container {
  background-color: #fff;
  border-radius: 4px;
}

p {
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  margin: 0;
  padding: 0;
  outline: none;
}

.mhy-account-center__subheader {
  padding: 0 30px;
  line-height: 50px;
  border-bottom: 1px solid #ebebeb;
  font-size: 16px;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-pack: justify;
  -ms-flex-pack: justify;
  justify-content: space-between;
}

.mhy-account-center-collection-menu {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  font-size: 14px;
}

.mhy-account-center-collection-menu .mhy-button {
  height: 28px;
  line-height: 28px;
  font-weight: 600;
}

.mhy-account-center-collection-menu__create {
  width: 88px;
}

.mhy-button {
  display: inline-block;
  cursor: pointer;
  -ms-flex-negative: 0;
  flex-shrink: 0;
}

.mhy-button-outlined .mhy-button__button {
  background-color: #fff;
  color: #00b2ff;
  border: 1px solid #00c3ff;
  border-radius: 4px;
  -webkit-transition-duration: 0.2s;
  -o-transition-duration: 0.2s;
  transition-duration: 0.2s;
  -webkit-transition-property: border-color, color;
  -o-transition-property: border-color, color;
  transition-property: border-color, color;
}

.mhy-button__button {
  display: -ms-inline-flexbox;
  display: inline-flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  -webkit-box-pack: center;
  -ms-flex-pack: center;
  justify-content: center;
  cursor: pointer;
  outline: none;
  font-size: inherit;
  color: inherit;
  width: 100%;
  height: 100%;
  background-color: transparent;
  border: none;
  border-radius: 0;
  font-weight: inherit;
  line-height: inherit;
}
</style>
