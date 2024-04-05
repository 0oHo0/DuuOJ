<template>
  <a-comment align="right" :avatar="store.state.user?.loginUser?.userAvatar">
    <template #actions>
      <a-button key="1" type="primary" @click="questionComment">评论</a-button>
    </template>
    <template #content>
      <a-input placeholder="Here is you content." v-model="comment" />
    </template>
  </a-comment>
  <div class="commentList">
    <div v-for="(comment, index) in comments" :key="comment">
      <a-comment
        :author="comment.userVO?.userName"
        :avatar="comment.userVO?.userAvatar"
        :content="comment.content"
        :datetime="formatDateTime(comment.createTime as string)"
      >
        <template #actions>
          <span
            class="action"
            key="heart"
            @click="onLikeChange(comment.likeFlag, comment.id)"
          >
            <span v-if="comment.likeFlag">
              <IconHeartFill :style="{ color: '#f53f3f' }" />
              {{ comment.likeNum }}
            </span>
            <span v-else>
              <IconHeart />
              {{ comment.likeNum }}
            </span>
          </span>
          <span class="action" @click="clickReply(index)">
            <IconMessage /> Reply
          </span>
        </template>
        <div v-if="replyFlag[index]">
          <a-comment
            align="right"
            :avatar="store.state.user?.loginUser?.userAvatar"
          >
            <template #actions>
              <a-button
                key="0"
                type="secondary"
                @click="replyFlag[index] = false"
              >
                取消
              </a-button>
              <a-button
                key="1"
                type="primary"
                @click="commentReply(comment.id, commentReplyContent, index)"
                >回复
              </a-button>
            </template>
            <template #content>
              <a-input
                placeholder="Here is you content."
                v-model="commentReplyContent"
              />
            </template>
          </a-comment>
        </div>
        <div v-for="commentReply in comment.commentReplies" :key="commentReply">
          <a-comment
            :author="commentReply.userVO?.userName"
            :avatar="commentReply.userVO?.userAvatar"
            :content="commentReply.content"
            :datetime="formatDateTime(commentReply.createTime as string)"
          ></a-comment>
        </div>
      </a-comment>
    </div>
    <a-pagination
      :show-total="true"
      :total="total"
      :current="searchParams.current"
      :page-size="searchParams.pageSize"
      @change="onPageChange"
    />
  </div>
</template>

<script lang="ts" setup>
import { IconHeart, IconMessage, IconStar } from "@arco-design/web-vue/es/icon";
import {
  defineProps,
  getCurrentInstance,
  onMounted,
  ref,
  watchEffect,
  withDefaults,
} from "vue";
import { CommentVO, QuestionControllerService } from "../../../generated";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";
import { Notification } from "@arco-design/web-vue";
import store from "@/store";
import { CommentQueryRequest } from "../../../generated/models/CommentQueryRequest";
import { IconHeartFill } from "@arco-design/web-vue/es/icon";

const like = ref([false, false, false, false, false]);
const indexNum = ref(-1);
const indexLike = ref(-1);
const replyFlag = ref([false, false, false, false, false]);
const comment = ref("");
const commentReplyContent = ref("");
const currentUrl = window.location.href;
// 使用 split 方法分割 URL，并取得最后一部分
const parts = currentUrl.split("/");
const lastPart = parts[parts.length - 1];
const comments = ref<Array<CommentVO>>();
const searchParams = ref<CommentQueryRequest>({
  // eslint-disable-next-line no-undef
  questionId: lastPart,
  current: 1,
  pageSize: 5,
});
const total = ref(0);
const loadData = async () => {
  const res =
    await QuestionControllerService.listQuestionCommentByPageUsingPost({
      ...searchParams.value,
      sortField: "createTime",
      sortOrder: "descend",
    });
  comments.value = res.data.records;
  total.value = res.data.total;
};

const questionComment = async () => {
  const res = await QuestionControllerService.questionCommentUsingPost({
    questionId: lastPart,
    content: comment.value,
  });
  if (res.code === 0) {
    const instance = getCurrentInstance();
    Notification.info({ content: "评论成功" });
    await loadData();
    // 调用 forceUpdate 方法
    instance?.proxy?.$forceUpdate();
    comment.value = "";
  } else {
    Notification.info({ content: "评论失败," + res.message });
  }
};
const clickReply = (index: number) => {
  indexNum.value = index;
  replyFlag.value[index] = true;
};
const commentReply = async (
  commentId: number,
  content: string,
  index: number
) => {
  const res = await QuestionControllerService.addCommentReplyUsingPost({
    commentId: commentId,
    content: content,
  });
  replyFlag.value[index] = false;
  if (res.code === 0) {
    const instance = getCurrentInstance();
    Notification.info({ content: "回复成功" });
    await loadData();
    // 调用 forceUpdate 方法
    instance?.proxy?.$forceUpdate();
    commentReplyContent.value = "";
  } else {
    Notification.info({ content: "回复失败," + res.message });
  }
};
onMounted(() => {
  loadData();
});
watchEffect(() => {
  loadData();
});
const onPageChange = (page: number) => {
  replyFlag.value = replyFlag.value.map(() => false);
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};
const formatDateTime = (originalDateTime: string): string => {
  // 创建 Date 对象并解析原始日期时间字符串
  const date = new Date(originalDateTime);
  // 使用 Date 对象的方法获取年、月、日、时、分、秒
  const year = date.getFullYear();
  const month = ("0" + (date.getMonth() + 1)).slice(-2);
  const day = ("0" + date.getDate()).slice(-2);
  const hours = ("0" + date.getHours()).slice(-2);
  const minutes = ("0" + date.getMinutes()).slice(-2);
  const seconds = ("0" + date.getSeconds()).slice(-2);
  // 返回格式化后的日期时间字符串
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};
const onLikeChange = async (likeFlag: boolean, commentId: number) => {
  if (!likeFlag) {
    //点赞
    const res = await QuestionControllerService.commentLikeUsingGet(commentId);
    if (res.code == 0) {
      await loadData();
    }
  } else {
    //取消点赞
    const res = await QuestionControllerService.commentDislikeUsingGet(
      commentId
    );
    if (res.code == 0) {
      await loadData();
    }
  }
};
</script>

<style scoped>
.action {
  display: inline-block;
  padding: 0 4px;
  color: var(--color-text-1);
  line-height: 24px;
  background: transparent;
  border-radius: 2px;
  cursor: pointer;
  transition: all 0.1s ease;
}

.action:hover {
  background: var(--color-fill-3);
}

.commentList {
  overflow-y: scroll;
  height: 600px;
  position: relative;
}
</style>
