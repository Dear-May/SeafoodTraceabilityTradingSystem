import { ref, onUnmounted } from 'vue';

/**
 * 短信/邮箱验证码倒计时 composable
 * 消除多个 View 中重复的倒计时逻辑
 */
export function useSmsCode(initialSeconds = 60) {
  const waitTime = ref(initialSeconds);
  const isButtonDisabled = ref(false);
  const buttonText = ref('获取验证码');
  let timer = null;

  const startCountdown = (seconds) => {
    isButtonDisabled.value = true;
    let remaining = seconds || initialSeconds;
    buttonText.value = `${remaining}秒后可以重新发送`;
    
    if (timer) clearInterval(timer);
    timer = setInterval(() => {
      remaining -= 1;
      if (remaining <= 0) {
        buttonText.value = '获取验证码';
        isButtonDisabled.value = false;
        waitTime.value = initialSeconds;
        if (timer) { clearInterval(timer); timer = null; }
      } else {
        buttonText.value = `${remaining}秒后可以重新发送`;
        waitTime.value = remaining;
      }
    }, 1000);
  };

  const stopCountdown = () => {
    if (timer) { clearInterval(timer); timer = null; }
    isButtonDisabled.value = false;
    buttonText.value = '获取验证码';
    waitTime.value = initialSeconds;
  };

  onUnmounted(() => {
    if (timer) clearInterval(timer);
  });

  return {
    waitTime,
    isButtonDisabled,
    buttonText,
    startCountdown,
    stopCountdown
  };
}

/**
 * 手机号验证工具
 */
export function isValidPhone(phone) {
  return /^1[3-9]\d{9}$/.test(phone);
}

/**
 * 邮箱验证工具
 */
export function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}
