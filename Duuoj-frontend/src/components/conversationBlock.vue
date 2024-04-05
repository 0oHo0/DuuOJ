<template>
  <div
    class="conversation-wrapper"
    :style="`height:${height}px`"
    id="conversation-wrapper"
  >
    <MsgItem
      v-for="(item, index) in list"
      :key="index"
      :msg="item.msg"
      :index="index"
      :contentType="item.contentType"
      :options="item.contentType == 0 ? answerOptions : userOptions"
      :showFeedback="item.contentType == 0 ? showFeedback : false"
      :feedbackList="feedbackList"
      :feedbackHandler="feedbackHandler"
    ></MsgItem>
  </div>
</template>

<script>
import MsgItem from "./msgItem.vue";

export default {
  name: "ConversationBlock",
  props: {
    list: Array, //消息列表
    height: [String, Number],
    userOptions: Object, //用户自定义配置
    answerOptions: Object, //回答者自定义配置
    showFeedback: Boolean, //是否显示反馈
    feedbackList: Array, //反馈可操作内容
    feedbackHandler: Function, //反馈处理函数
  },
  components: { MsgItem },
  data() {
    return {
      answerOptionsInner: {},
      userOptionsInner: {},
    };
  },
  computed: {},
  watch: {
    list: {
      deep: true,
      handler(n, o) {
        this.$nextTick(() => {
          let ele = document.getElementById("conversation-wrapper");
          ele.scrollTop = ele.scrollHeight;
        });
      },
    },
  },
  methods: {},
};
</script>
<style scoped lang="less">
.conversation-wrapper {
  width: 95%;
  padding: 10px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}
</style>
