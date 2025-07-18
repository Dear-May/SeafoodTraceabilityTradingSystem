<template>
  <div class="bubble-background">
    <canvas ref="bubbleCanvas" class="bubble-canvas"></canvas>
  </div>
</template>

<script setup>
import {onBeforeUnmount, onMounted, ref} from 'vue';

// 创建 canvas 元素的引用
const bubbleCanvas = ref(null);
let animationFrameId;

const bubbles = [];

// 初始化气泡参数
function initBubbles(numBubbles, canvasWidth, canvasHeight) {
  bubbles.length = 0; // 清空数组
  for (let i = 0; i < numBubbles; i++) {
    bubbles.push({
      x: Math.random() * canvasWidth,
      y: Math.random() * canvasHeight,
      radius: Math.random() * 10 + 5, // 气泡半径在 5 - 15 之间
      speed: Math.random() * 1.5 + 0.5, // 气泡上升速度在 0.5 - 2 之间
      alpha: Math.random() * 0.4 + 0.6, // 气泡透明度在 0.6 - 1 之间
      drift: Math.random() * 2 - 1, // 气泡左右漂移的速度
      phase: Math.random() * Math.PI * 2 // 气泡运动的初始相位，用于增加弧形运动
    });
  }
}

const drawBubbles = () => {
  const canvas = bubbleCanvas.value;
  const ctx = canvas.getContext('2d');

  // 设置 canvas 的宽度和高度
  const width = window.innerWidth;
  const height = window.innerHeight;
  canvas.width = width;
  canvas.height = height;

  // 初始化气泡
  initBubbles(60, width, height);

  function draw() {
    // 清除画布
    ctx.clearRect(0, 0, width, height);

    // 绘制每一个气泡
    bubbles.forEach((bubble) => {
      // 创建渐变以模拟气泡的立体效果
      const gradient = ctx.createRadialGradient(
          bubble.x, bubble.y, bubble.radius * 0.1,
          bubble.x, bubble.y, bubble.radius
      );
      gradient.addColorStop(0, `rgba(255, 255, 255, ${bubble.alpha})`); // 中心亮色，高光效果
      gradient.addColorStop(0.5, `rgba(135, 206, 250, ${bubble.alpha * 0.6})`); // 中间略深的蓝色，模拟渐变
      gradient.addColorStop(1, `rgba(0, 119, 190, 0)`); // 边缘透明，渐变效果

      // 绘制气泡
      ctx.beginPath();
      ctx.arc(bubble.x, bubble.y, bubble.radius, 0, Math.PI * 2, false);
      ctx.fillStyle = gradient;
      ctx.fill();
      ctx.closePath();

      // 更新气泡的位置
      bubble.y -= bubble.speed; // 气泡上升
      bubble.x += Math.sin(bubble.phase) * 0.5; // 气泡左右漂移，形成弧线运动
      bubble.phase += 0.02; // 更新相位，使气泡呈现弧形轨迹

      // 如果气泡超出了顶部，则重新设置到底部
      if (bubble.y < -bubble.radius) {
        bubble.y = height + bubble.radius;
        bubble.x = Math.random() * width;
        bubble.radius = Math.random() * 10 + 5;
        bubble.speed = Math.random() * 1.5 + 0.5;
        bubble.alpha = Math.random() * 0.4 + 0.6; // 透明度增加，使气泡更加明显
        bubble.drift = Math.random() * 2 - 1;
        bubble.phase = Math.random() * Math.PI * 2;
      }
    });

    // 循环绘制
    animationFrameId = requestAnimationFrame(draw);
  }

  draw();
};

// 在组件挂载后绘制气泡动画
onMounted(() => {
  drawBubbles();
});

// 清除动画帧以防止内存泄漏
onBeforeUnmount(() => {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId);
  }
});
</script>

<style scoped>
.bubble-background {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.bubble-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%; /* 使用100%高度确保 canvas 显示 */
  z-index: 1;
  pointer-events: none; /* 防止 canvas 遮挡任何点击事件 */
}
</style>
