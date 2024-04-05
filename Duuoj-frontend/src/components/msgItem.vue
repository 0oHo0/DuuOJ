<template>
  <div :class="`msg-pop-wrapper content-align-${innerOptions.position}`">
    <div
      :class="`avatar-wrapper ${contentType == 1 ? 'user' : 'answer'}`"
      v-if="innerOptions.position == 'left'"
    >
      <img
        class="avatar-img"
        v-if="innerOptions.avatar"
        :src="innerOptions.avatar"
      />
      <!-- <img class="avatar-default" v-else src="../assets/user.svg" /> -->
      <i class="avatar-default iconfont icon-user"></i>
    </div>
    <div
      :class="`msg-pop-inner ${
        innerOptions.customClass ? innerOptions.customClass : ''
      } ${contentType == 1 ? 'user' : ''}`"
    >
      <div v-html="msg" v-if="!slots.includes('content')"></div>
      <slot name="content" v-if="slots.includes('content')"></slot>
      <div class="feedback-bar-wrapper" v-if="showFeedback">
        <FeedbackBar
          v-if="!slots.includes('feedback')"
          :feedbackList="feedbackList"
          :feedbackHandler="feedbackHandler"
          :likeType="likeType"
          @feedback="feedbackHandlerInner"
        />
        <slot name="feedback" v-if="slots.includes('feedback')"></slot>
      </div>
    </div>
    <div
      :class="`avatar-wrapper ${contentType == 1 ? 'user' : 'answer'}`"
      v-if="innerOptions.position == 'right'"
    >
      <img
        class="avatar-img"
        v-if="innerOptions.avatar"
        :src="innerOptions.avatar"
      />
      <!-- <img class="avatar-default" v-else src="../assets/user.svg" /> -->
      <i class="avatar-default iconfont icon-user"></i>
    </div>
  </div>
</template>

<script>
import FeedbackBar from "./feedbackBar.vue";

export default {
  name: "MsgItem",
  props: {
    msg: undefined, //消息内容
    contentType: {
      //消息类型：0:回答者; 1:提问者
      type: [String, Number],
      default: 1,
    },
    index: [String, Number], //唯一识别本消息的index
    options: {
      type: Object,
      default: () => {
        return { avatar: undefined, position: "left", customClass: undefined };
      },
    },
    /**
     * 可配置项
     * avatar:String //头像
     * position:String //对齐位置，'left'/'right'
     * customClass:String //外部定义类
     */
    showFeedback: {
      // 是否显示反馈
      type: Boolean,
      defalut: false,
    },
    feedbackList: {
      // 反馈可操作内容：'like','diss','reAnswer','feedback'
      type: Array,
      default: () => ["like", "diss", "reAnswer", "feedback"],
    },
    likeType: {
      // 点赞状态, 1:like,0:no status,-1:dislike
      type: [String, Number],
      default: 0,
    },
    feedbackHandler: {
      //处理反馈回调函数, return {index,type,value}
      type: Function,
      // eslint-disable-next-line @typescript-eslint/no-empty-function
      default: () => {},
    },
    slots: {
      // 应用的slot: 'content','feedback'
      type: Array,
      default: () => [],
    },
  },

  components: { FeedbackBar },
  data() {
    return {
      answerSlot: false,
      userSlot: false,
      test: undefined,
    };
  },
  computed: {
    alignPosition() {
      return this.options.position;
    },
    innerOptions() {
      let def = {
        avatar: undefined,
        position: "left",
        customClass: undefined,
      };
      let opt = {};
      Object.keys(def).forEach((key) => {
        // eslint-disable-next-line no-prototype-builtins
        if (!this.options.hasOwnProperty(key)) {
          opt[key] = def[key];
        } else {
          opt[key] = this.options[key];
        }
      });
      return opt;
    },
  },
  watch: {
    slot(n, o) {
      this.answerSlot = n.includes("answerContent");
      this.userSlot = n.includes("userContent");
    },
  },

  methods: {
    feedbackHandlerInner(e) {
      this.feedbackHandler({
        index: this.index,
        type: e.type,
        value: e.value,
      });
    },
  },
};
</script>
<style scoped lang="less">
@import url("https://at.alicdn.com/t/c/font_4434170_wrbq13kkomm.css");

.msg-pop-wrapper {
  display: flex;
  width: 100%;
  margin: 10px 0px;

  &.content-align-left {
    justify-content: start;
  }

  &.content-align-right {
    justify-content: end;

    .msg-pop-inner {
      text-align: end;
    }
  }
}

.avatar-wrapper {
  width: 50px;
  height: 50px;
  margin-top: 4px;
  border-radius: 25px;
  color: #fff;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;

  &.user {
    background: #666;
  }

  &.answer {
    background: #00b3cc;
  }

  .avatar-img {
    height: 100%;
    width: 100%;
  }

  .avatar-default {
    font-size: 25px;
    //   line-height: 50px;
    text-align: center;
  }
}

.msg-pop-inner {
  background: #fff;
  padding: 20px;
  border-radius: 2px;
  margin: 0px 12px;
  flex: 1;

  &.user {
    background-color: rgba(0, 0, 0, 0);
  }
}

.feedback-bar-wrapper {
  width: 100%;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}
</style>
