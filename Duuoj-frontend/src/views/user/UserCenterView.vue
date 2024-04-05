<template>
  <div class="root-page-container">
    <div class="mhy-main-page mhy-account-center">
      <div class="mhy-layout">
        <!--顶部头像栏-->
        <div class="mhy-container mhy-account-center-header">
          <div
            class="mhy-avatar mhy-account-center-header__avatar mhy-avatar__xxl"
          >
            <img
              :src="store.state.user?.loginUser?.userAvatar"
              class="mhy-avatar__img"
            />
          </div>
          <div class="mhy-account-center-user">
            <div class="mhy-account-center-user__header">
              <div class="mhy-account-center-user__title">
                <span class="mhy-account-center-user__name">{{
                  store.state.user?.loginUser?.userName
                }}</span>
                <!---->
                <img
                  src="https://img-static.mihoyo.com/level/level1.png"
                  class="mhy-img-icon mhy-account-center-user__level mhy-account-center-user__level--self"
                />
                <!---->
              </div>
              <div class="mhy-account-center-header__buttons">
                <!---->
              </div>
            </div>
            <div class="mhy-account-center-user__audit">
              <span class="mhy-account-center-user__uid"
                >用户ID:{{ store.state.user?.loginUser?.id }}</span
              >
              <!---->
            </div>
            <div class="mhy-account-center-user__intro">
              <p>
                <i
                  class="el-icon-tickets"
                  style="color: #ad4e69; margin-right: 10px; font-size: 18px"
                ></i
                >{{ store.state.user?.loginUser?.userProfile }}
              </p>
            </div>
            <div class="mhy-account-center-user__intro">
              <p>
                <i
                  class="el-icon-location-information"
                  style="color: #00c3ff; margin-right: 10px; font-size: 18px"
                ></i
                >IP属地：未知
              </p>
            </div>
            <div class="mhy-account-center-header__data">
              <div class="mhy-account-center-header__data-item">
                <a
                  class="mhy-router-link mhy-account-center-header__data-num mhy-account-center-header__data-link"
                  >5</a
                >
                <div class="mhy-account-center-header__data-label">粉丝</div>
              </div>
              <div class="mhy-account-center-header__data-item">
                <a
                  class="mhy-router-link mhy-account-center-header__data-num mhy-account-center-header__data-link"
                >
                  10
                </a>
                <div class="mhy-account-center-header__data-label">关注</div>
              </div>
              <div class="mhy-account-center-header__data-item">
                <div class="mhy-account-center-header__data-num">45</div>
                <div class="mhy-account-center-header__data-label">获赞</div>
              </div>
            </div>
          </div>
        </div>

        <!--左侧菜单栏-->
        <div class="mhy-container mhy-side-menu mhy-account-center__menu">
          <header class="mhy-side-menu__header">个人中心</header>
          <ul class="mhy-side-menu__list">
            <li
              v-for="(item, index) in data.menus"
              :key="index"
              @click="selMenu(item)"
            >
              <a
                :class="
                  'mhy-router-link mhy-side-menu__item ' +
                  (activeIndex === item.path ? 'nuxt-link-active' : '')
                "
              >
                <i
                  :class="item.icon"
                  style="font-size: 18px; margin-right: 10px"
                ></i>
                <span>{{ item.name }}</span>
              </a>
            </li>
          </ul>
        </div>

        <!-- 右侧内容-->
        <div class="mhy-container mhy-account-center-content">
          <router-view></router-view>
          <!--          <UserInfoView></UserInfoView>-->
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import store from "@/store";
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import UserInfoView from "@/views/user/UserInfoView.vue";

const data = ref({
  activeIndex: "/user/center/user_info",
  menus: [
    {
      name: "个人简介",
      path: "/user/center/user_info",
      icon: "el-icon-document",
    },
    {
      name: "我的合集",
      path: "/personal/myCollect",
      icon: "el-icon-document",
    },
  ],
});
const router = useRouter();
const route = useRoute();
console.log(route.path);
onMounted(() => {
  router.push(data.value.activeIndex);
});

const selMenu = (item) => {
  data.value.activeIndex = item.path;
  router.push({ path: item.path });
};
const updateMyInfo = () => {
  router.push({ path: "/user/center/user_info" });
};
</script>

<style scoped>
.root-page-container {
  background: url(https://www.miyoushe.com/_nuxt/img/background.cd0a312.png)
    no-repeat 0 62px;
  background-size: 100%;
}

.mhy-main-page {
  padding-top: 30px;
  position: relative;
}

.mhy-layout {
  width: 1000px;
  margin: 0 auto;
  padding-left: 100px;
  padding-right: 100px;
  -webkit-box-sizing: content-box;
  box-sizing: content-box;
}

.mhy-layout {
  zoom: 1;
}

.mhy-account-center-header {
  padding: 20px 50px 24px;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  margin-bottom: 20px;
}

.mhy-container {
  background-color: #fff;
  border-radius: 4px;
}

.mhy-account-center-header__avatar {
  margin-right: 24px;
  -ms-flex-negative: 0;
  flex-shrink: 0;
}

.mhy-avatar__xxl {
  width: 120px;
  height: 120px;
}

.mhy-avatar {
  display: inline-block;
  position: relative;
}

.mhy-avatar__img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 1px solid #ebebeb;
  vertical-align: top;
}

.mhy-avatar__img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 1px solid #ebebeb;
  vertical-align: top;
}

img {
  border-style: none;
}

.mhy-account-center-user {
  -webkit-box-flex: 1;
  -ms-flex-positive: 1;
  flex-grow: 1;
  height: 100%;
}

.mhy-account-center-user__header {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-pack: justify;
  -ms-flex-pack: justify;
  justify-content: space-between;
}

.mhy-account-center-user__title {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
}

.mhy-account-center-user__name {
  font-size: 16px;
  line-height: 21px;
  font-weight: 600;
}

.mhy-account-center-user__level.mhy-img-icon {
  margin-left: 10px;
  height: 16px;
  -ms-flex-negative: 0;
  flex-shrink: 0;
}

.mhy-account-center-user__level--self {
  cursor: pointer;
}

.mhy-img-icon {
  height: 1em;
  fill: currentColor;
  overflow: hidden;
}

.mhy-account-center-header__buttons {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
}

.mhy-button.mhy-button-md.mhy-button-outlined {
  line-height: 32px;
}

.mhy-button.mhy-button-md {
  width: 86px;
  height: 34px;
  line-height: 34px;
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

input,
button,
textarea {
  color: inherit;
  font: inherit;
}

.mhy-account-center-user__audit {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
}

.mhy-account-center-user__uid {
  font-size: 12px;
  color: #ccc;
}

a {
  text-decoration: none;
}

.mhy-account-center-user__certification,
.mhy-account-center-user__intro {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  margin-top: 12px;
  color: #666;
  line-height: 18px;
}

.mhy-account-center-user__certification span,
.mhy-account-center-user__intro span {
  margin-top: 2px;
  -ms-flex-negative: 0;
  flex-shrink: 0;
  line-height: 1;
}

p {
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  margin: 0;
  padding: 0;
  outline: none;
}

.mhy-account-center-user__certification,
.mhy-account-center-user__intro {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  margin-top: 12px;
  color: #666;
  line-height: 18px;
}

.mhy-account-center-user__certification .ip-icon,
.mhy-account-center-user__intro .ip-icon {
  color: #76bfe3;
  font-size: 16px;
  margin-right: 8px;
}

.mhy-icon {
  font-size: inherit;
}

.iconfont {
  font-family: "iconfont" !important;
  font-size: 16px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.icon-ip:before {
  content: "";
}

.mhy-account-center-header__data {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  padding-top: 19px;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  -ms-flex-item-align: center;
  align-self: center;
  -ms-flex-negative: 0;
  flex-shrink: 0;
  margin-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.mhy-account-center-header__data-item {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  padding-right: 40px;
}

.mhy-account-center-header__data-link {
  cursor: pointer;
}

.mhy-account-center-header__data-num {
  color: #333;
  font-size: 20px;
}

.mhy-account-center-header__data-label {
  margin-left: 10px;
  color: #ccc;
}

.mhy-account-center-header__data-item {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  padding-right: 40px;
}

.mhy-account-center-header__data-link {
  cursor: pointer;
}

.mhy-account-center-header__data-num {
  color: #333;
  font-size: 20px;
}

.mhy-account-center-header__data-label {
  margin-left: 10px;
  color: #ccc;
}

.mhy-account-center-header__data-item {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  padding-right: 40px;
}

.mhy-account-center-header__data-num {
  color: #333;
  font-size: 20px;
}

.mhy-account-center-header__data-label {
  margin-left: 10px;
  color: #ccc;
}

/*左侧菜单栏*/
.mhy-side-menu {
  width: 280px;
  float: left;
}

.mhy-container {
  background-color: #fff;
  border-radius: 4px;
}

.mhy-side-menu__header {
  width: 100%;
  padding: 0 30px;
  line-height: 50px;
  border-bottom: 1px solid #ebebeb;
  font-size: 16px;
}

.mhy-side-menu__list {
  padding: 0 30px 10px;
}

ol,
ul {
  margin: 0;
  padding: 0;
  list-style: none;
}

ul,
li {
  list-style: none;
}

.mhy-account-center__menu li:nth-of-type(3),
.mhy-account-center__menu li:nth-of-type(5) {
  border-bottom: 1px solid #f0f0f0;
}

.mhy-side-menu .nuxt-link-active {
  color: #00c3ff;
}

.mhy-side-menu__item {
  padding: 0 20px;
  line-height: 50px;
  font-size: 14px;
  color: #666;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  cursor: pointer;
}

.mhy-side-menu .nuxt-link-active .mhy-icon {
  color: #00c3ff;
}

.mhy-side-menu__item .mhy-icon {
  width: 18px;
  font-size: 18px;
  vertical-align: top;
  display: inline-block;
  color: #d9d9d9;
  margin-right: 15px;
}

.mhy-icon {
  font-size: inherit;
}

.iconfont {
  font-family: "iconfont" !important;
  font-size: 16px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.icon-wodefatie:before {
  content: "";
}

/*右侧内容*/
.mhy-account-center-content {
  width: 700px;
  float: right;
}
</style>
