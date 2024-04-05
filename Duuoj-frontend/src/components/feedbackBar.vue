<template>
  <div class="feedback-bar-wrapper-inner">
    <div class="feedback-bar-left">
      <i
        :class="`iconfont like-icon interactable ${
          likeType == 1 ? 'icon-like-fill filled' : 'icon-like'
        }`"
        id="feedback-like"
        v-if="showLike"
        :theme="likeType == 1 ? 'filled' : 'outlined'"
        @click="handleOperation('like')"
      ></i>
      <i
        :class="`iconfont diss-icon interactable ${
          likeType == -1 ? 'icon-unlike-fill filled' : 'icon-unlike'
        }`"
        id="feedback-diss"
        v-if="showDiss"
        :theme="likeType == -1 ? 'filled' : 'outlined'"
        @click="handleOperation('diss')"
      ></i>
    </div>
    <div class="feedback-bar-right">
      <div class="feedback-bar-right-regenerate interactable">
        <i
          class="iconfont icon-retweet regenerate-icon"
          v-if="showRe"
          @click="handleOperation('reAnswer')"
        ></i>
        <span
          class="regenerate-text"
          v-if="showRe"
          @click="handleOperation('reAnswer')"
          >重新生成</span
        >
      </div>
      <div class="feedback-bar-right-fb interactable">
        <i
          class="iconfont icon-mail fb-icon"
          v-if="showFB"
          @click="handleOperation('feedback')"
        ></i>
        <span class="fb-text" v-if="showFB" @click="handleOperation('feedback')"
          >问题反馈</span
        >
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "FeedbackBar",
  props: {
    feedbackList: {
      type: Array,
      default: () => ["like", "diss", "reAnswer", "feedback"],
    },
    feedbackHandler: {
      type: Function,
      // eslint-disable-next-line @typescript-eslint/no-empty-function
      default: () => {},
    },
    likeType: {
      type: [Number, String],
      default: 0,
    }, //点赞状态, 1:like,0:no status,-1:dislike
  },
  components: {},
  data() {
    return {
      showLike: true,
      showDiss: true,
      showRe: true,
      showFB: true,
      likeTypeInner: 0,
    };
  },
  computed: {},
  watch: {
    feedbackList: {
      deep: true,
      handler(n, o) {
        this.showLike = n.includes("like");
        this.showDiss = n.includes("diss");
        this.showRe = n.includes("reAnswer");
        this.showFB = n.includes("feedback");
      },
    },
  },
  created() {
    this.setStatus();
    this.likeTypeInner = Number(this.likeType);
  },
  methods: {
    setStatus() {
      this.showLike = this.feedbackList.includes("like");
      this.showDiss = this.feedbackList.includes("diss");
      this.showRe = this.feedbackList.includes("reAnswer");
      this.showFB = this.feedbackList.includes("feedback");
    },
    handleOperation(e) {
      if (e == "feedback" || e == "reAnswer") {
        this.$emit("feedback", { type: e });
      } else if (e == "like") {
        this.likeTypeInner = this.likeTypeInner === 1 ? 0 : 1;
        this.$emit("feedback", { type: e, value: this.likeTypeInner });
      } else if (e == "diss") {
        this.likeTypeInner = this.likeTypeInner === -1 ? 0 : -1;
        this.$emit("feedback", { type: e, value: this.likeTypeInner });
      }
    },
  },
};
</script>
<style scoped lang="less">
@import url("https://at.alicdn.com/t/c/font_4434170_wrbq13kkomm.css");

.feedback-bar-wrapper-inner {
  width: 100%;
  display: flex;
  justify-content: space-between;
}

.feedback-bar-left {
  display: flex;
  align-items: center;

  .like-icon {
    margin-right: 10px;
  }

  .filled {
    color: orange;
  }
}

.feedback-bar-right {
  cursor: pointer;
  display: flex;

  .regenerate-icon {
    margin-right: 8px;
  }

  .regenerate-text {
    margin-right: 12px;
  }

  .fb-icon {
    margin: 0px 8px;
  }
}

.interactable {
  cursor: pointer;
  color: #ccc;
  transition: all 0.2s;
}

.interactable:hover {
  color: #000;
}
</style>
