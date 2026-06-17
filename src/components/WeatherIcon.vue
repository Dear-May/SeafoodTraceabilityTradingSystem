<template>
  <i :class="weatherIconClass" :style="iconStyle" :title="weather"></i>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  weather: { type: String, default: '' },
  size: { type: Number, default: 20 },
  showText: { type: Boolean, default: false },
  temperature: { type: [String, Number], default: '' }
});

const weatherIconMap = {
  '晴': 'bi bi-cloud-sun-fill',
  '阴': 'bi bi-cloud-fog-fill',
  '雨': 'bi bi-cloud-rain-fill',
  '雾': 'bi bi-cloud-haze-fill',
  '多云': 'bi bi-cloud-sun-rain-fill',
  '霾': 'bi bi-cloud',
  '雪': 'bi bi-snow',
  '风': 'bi bi-wind'
};

const weatherIconClass = computed(() => {
  return weatherIconMap[props.weather] || 'bi bi-cloud';
});

const iconStyle = computed(() => ({
  fontSize: props.size + 'px',
  marginLeft: '4px'
}));
</script>
