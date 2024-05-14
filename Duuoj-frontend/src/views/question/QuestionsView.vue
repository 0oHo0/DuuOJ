<template>
  <div id="questionsView">
    <a-form
      :model="searchParams"
      layout="inline"
      style="display: flex; align-items: flex-end"
    >
      <a-form-item field="title" style="min-width: 240px; margin-right: 10px">
        <a-input
          v-model="searchParams.searchText"
          placeholder="搜索题目或内容"
          allow-clear
        >
          <template #prefix>
            <icon-search />
          </template>
        </a-input>
      </a-form-item>
      <a-form-item field="tags" style="min-width: 200px; margin-right: 10px">
        <a-input-tag
          v-model="searchParams.tags"
          :style="{ width: '200px' }"
          placeholder="请输入标签"
          size="medium"
          max-tag-count="3"
          allow-clear
        >
          <template #prefix>
            <icon-tags />
          </template>
        </a-input-tag>
      </a-form-item>
      <a-form-item style="">
        <a-button type="primary" @click="doSubmit">
          <template #icon>
            <icon-search />
          </template>
          搜索
        </a-button>
      </a-form-item>
      <a-form-item style="">
        <div class="radius" style="display: flex; justify-content: center">
          <div style="margin: 20px 30px 10px 10px">
            <el-progress
              type="circle"
              :percentage="
                (questionStatics.acceptedNum / questionStatics.totalNum) * 100
              "
            >
              <template #default="{ percentage }">
                <span class="percentage-value"
                  >{{ percentage.toFixed(0) }}%</span
                >
                <span class="percentage-label">当前进度</span>
              </template>
            </el-progress>
          </div>
          <div style="flex: content; margin-top: 10px">
            <el-text class="mx-1" type="success"
              >简单 {{ questionStatics.simpleAcceptedNum }}/{{
                questionStatics.simpleTotalNum
              }}
            </el-text>
            <el-progress
              :text-inside="true"
              :stroke-width="20"
              :percentage="
                (questionStatics.simpleAcceptedNum /
                  questionStatics.simpleTotalNum) *
                100
              "
              status="success"
            />
            <el-text class="mx-1" type="warning"
              >中等 {{ questionStatics.mediumAcceptedNum }}/{{
                questionStatics.mediumTotalNum
              }}
            </el-text>
            <el-progress
              :text-inside="true"
              :stroke-width="20"
              :percentage="
                (questionStatics.mediumAcceptedNum /
                  questionStatics.mediumTotalNum) *
                100
              "
              status="warning"
            />
            <el-text class="mx-1" type="danger"
              >困难 {{ questionStatics.hardAcceptedNum }}/{{
                questionStatics.hardTotalNum
              }}
            </el-text>
            <el-progress
              :text-inside="true"
              :stroke-width="20"
              :percentage="
                (questionStatics.hardAcceptedNum /
                  questionStatics.hardTotalNum) *
                100
              "
              status="exception"
            />
          </div>
        </div>
      </a-form-item>
      <a-form-item
        style="justify-content: space-between; align-items: flex-end"
      >
        <a-statistic
          title="今日活跃用户数"
          :value="activeUsers"
          show-group-separator
        >
          <template #suffix>
            <icon-arrow-rise />
          </template>
        </a-statistic>
      </a-form-item>
    </a-form>
    <a-divider size="0" />
    <div>
      <a-table
        :ref="tableRef"
        :columns="columns"
        :data="dataList"
        :pagination="{
          showTotal: true,
          pageSize: searchParams.pageSize,
          current: searchParams.current,
          // total,
        }"
        @page-change="onPageChange"
      >
        <template #status="{ record }">
          {{ record.status == 1 ? "✔" : "" }}
        </template>
        <template #tags="{ record }">
          <a-space wrap>
            <a-tag
              v-for="(tag, index) of record.tags"
              :key="index"
              color="green"
              >{{ tag }}
            </a-tag>
          </a-space>
        </template>
        <template #acceptedRate="{ record }">
          {{ ((record.acceptedNum / record.submitNum) * 100).toFixed(1) }}%
        </template>
        <template #createTime="{ record }">
          {{ moment(record.createTime).format("YYYY-MM-DD") }}
        </template>
        <template #optional="{ record }">
          <a-space>
            <a-button type="primary" @click="toQuestionPage(record)">
              做题
            </a-button>
          </a-space>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import {
  Page_Question_,
  Question,
  QuestionControllerService,
  QuestionQueryRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import * as querystring from "querystring";
import { useRouter } from "vue-router";
import moment from "moment";
import { SearchQueryRequest } from "../../../generated/models/SearchQueryRequest";
import axios from "@/plugins/axios";
import { QuestionStatisticsResponse } from "../../../generated/models/QuestionStatisticsResponse";

const tableRef = ref();
const activeUsers = ref(0);
const dataList = ref([]);
const total = ref(0);

const searchParams = ref<SearchQueryRequest>({
  searchText: "",
  pageSize: 8,
  current: 1,
  type: "",
  tags: [],
});
const questionStatics = ref<QuestionStatisticsResponse>({
  acceptedNum: 0,
  hardAcceptedNum: 0,
  hardTotalNum: 0,
  mediumAcceptedNum: 0,
  mediumTotalNum: 0,
  simpleAcceptedNum: 0,
  simpleTotalNum: 0,
  totalNum: 0,
});
const loadData = async () => {
  let res;
  res = await axios.get("http://localhost:8104/api/user/active");
  activeUsers.value = parseInt(res.data.data);
  if (searchParams.value.searchText == "") {
    res = await QuestionControllerService.listQuestionVoByPageUsingPost({
      ...searchParams.value,
      title: "",
    });
  } else {
    res = await QuestionControllerService.searchQuestionByEsUsingPost(
      searchParams.value
    );
  }
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    message.error("加载失败，" + res.message);
  }
};

/**
 * 监听 searchParams 变量，改变时触发页面的重新加载
 */
watchEffect(() => {
  loadData();
});

/**
 * 页面加载时，请求数据
 */
onMounted(async () => {
  await loadData();
  const res = await QuestionControllerService.questionStatisticsUsingPost();
  if (res.code === 0) {
    if (res.data.mediumTotalNum == 0) res.data.mediumTotalNum = 1;
    if (res.data.mediumTotalNum == 0) res.data.mediumTotalNum = 1;
    if (res.data.hardTotalNum == 0) res.data.hardTotalNum = 1;
    questionStatics.value = res.data;
  } else {
    message.error("加载失败，" + res.message);
  }
});

// {id: "1", title: "A+ D", content: "新的题目内容", tags: "["二叉树"]", answer: "新的答案", submitNum: 0,…}

const columns = [
  {
    title: "题号",
    dataIndex: "id",
  },
  {
    title: "状态",
    slotName: "status",
  },
  {
    title: "题目名称",
    dataIndex: "title",
  },
  {
    title: "标签",
    slotName: "tags",
  },
  {
    title: "通过率",
    slotName: "acceptedRate",
  },
  {
    title: "创建时间",
    slotName: "createTime",
  },
  {
    slotName: "optional",
  },
];

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

const router = useRouter();

/**
 * 跳转到做题页面
 * @param question
 */
const toQuestionPage = (question: Question) => {
  router.push({
    path: `/view/question/${question.id}`,
  });
};

/**
 * 确认搜索，重新加载数据
 */
const doSubmit = () => {
  // 这里需要重置搜索页号
  searchParams.value = {
    ...searchParams.value,
    current: 1,
  };
};
const getCssVarName = (type: string) => {
  return `--el-box-shadow${type ? "-" : ""}${type}`;
};
</script>

<style scoped>
#questionsView {
  max-width: 1280px;
  margin: 0 auto;
}

.percentage-value {
  display: block;
  margin-top: 10px;
  font-size: 28px;
}

.percentage-label {
  display: block;
  margin-top: 10px;
  font-size: 12px;
}

.radius {
  height: 160px;
  width: 380px;
  border: 2px solid var(--el-border-color);
  border-radius: 10px;
  margin: auto;
  box-shadow: var(--el-box-shadow-lighter);
}

.radius .el-progress--line {
  margin-bottom: 15px;
  max-width: 200px;
}
</style>
