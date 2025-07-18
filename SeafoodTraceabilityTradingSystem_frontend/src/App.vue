<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
window.ResizeObserver = class ResizeObserver extends window.ResizeObserver {
  constructor(callback) {
    let timer = null;
    const debouncedCallback = function () {
      let context = this;
      let args = arguments;
      clearTimeout(timer);
      timer = setTimeout(function () {
        callback.apply(context, args);
      }, 16);
    };
    super(debouncedCallback);
  }
};
</script>

<style lang="less">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

/* 自定义滚动条样式 */
::-webkit-scrollbar {
  width: 12px; /* 滚动条的宽度 */
  height: 12px; /* 横向滚动条的高度 */
}

::-webkit-scrollbar-track {
  background: #f1f1f1; /* 滚动条的轨道背景颜色 */
  border-radius: 10px; /* 圆角 */
}

::-webkit-scrollbar-thumb {
  background: #96CDCD; /* 滚动条的颜色 */
  border-radius: 10px; /* 圆角 */
  border: 3px solid #f1f1f1; /* 给滚动条增加一个边框间距 */
}

::-webkit-scrollbar-thumb:hover {
  background: #668B8B; /* 悬停时滚动条的颜色 */
}

</style>
