<template>
  <div class="conversation-wrapper" :style="`height:${height}`">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: "ConversationWrapper",
  props: {},
  components: {},
  data() {
    return {
      height: Number, //对话区域高度
    };
  },
  computed: {},
  watch: {},
  methods: {},
};
</script>
<style scoped lang="less">
.conversation-wrapper {
  width: 100%;
  padding: 24px;
  display: flex;
  flex-direction: column;
}
</style>
