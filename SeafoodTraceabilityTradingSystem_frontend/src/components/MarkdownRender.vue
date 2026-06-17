<template>
  <div class="markdown-content" v-html="renderedHTML"></div>
</template>

<script setup>
import { computed } from 'vue'
import { marked } from 'marked'

// 配置 marked 选项
marked.setOptions({
  breaks: true,       // 支持 GitHub 风格的换行
  gfm: true,          // 启用 GitHub 风格 Markdown
})

const props = defineProps({
  content: {
    type: String,
    default: ''
  }
})

const renderedHTML = computed(() => {
  if (!props.content) return ''
  try {
    return marked.parse(props.content)
  } catch (e) {
    return props.content
  }
})
</script>

<style scoped>
.markdown-content {
  line-height: 1.8;
  word-break: break-word;
}
.markdown-content :deep(h1),
.markdown-content :deep(h2),
.markdown-content :deep(h3),
.markdown-content :deep(h4) {
  margin-top: 1em;
  margin-bottom: 0.5em;
  font-weight: 600;
}
.markdown-content :deep(p) {
  margin-bottom: 0.5em;
}
.markdown-content :deep(ul),
.markdown-content :deep(ol) {
  padding-left: 1.5em;
  margin-bottom: 0.5em;
}
.markdown-content :deep(li) {
  margin-bottom: 0.25em;
}
.markdown-content :deep(code) {
  background: #f5f5f5;
  padding: 0.2em 0.4em;
  border-radius: 3px;
  font-size: 0.9em;
}
.markdown-content :deep(pre) {
  background: #f5f5f5;
  padding: 1em;
  border-radius: 4px;
  overflow-x: auto;
}
.markdown-content :deep(pre code) {
  background: none;
  padding: 0;
}
.markdown-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
}
.markdown-content :deep(blockquote) {
  border-left: 4px solid #409eff;
  padding-left: 1em;
  margin: 0.5em 0;
  color: #666;
}
.markdown-content :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 0.5em 0;
}
.markdown-content :deep(th),
.markdown-content :deep(td) {
  border: 1px solid #ddd;
  padding: 0.5em;
  text-align: left;
}
.markdown-content :deep(th) {
  background: #f5f7fa;
  font-weight: 600;
}
.markdown-content :deep(a) {
  color: #409eff;
  text-decoration: none;
}
.markdown-content :deep(a:hover) {
  text-decoration: underline;
}
</style>
