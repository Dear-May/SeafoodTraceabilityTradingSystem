<script setup>
import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router";

async function handleClick() {
  try {
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id'); // 提取 id
    const source = urlParams.get('source'); // 提取 source
    if (id && source) {
      urlParams.delete('id');
      urlParams.delete('source');
      try {
        const response = await axios.post('/api/callback/ThirdLogin/getUserBaseInfo', {
          id: id,
          source: source
        }, {
          headers: {
            'Content-Type': 'application/json'
          },
          withCredentials: true
        });
        if (response.data.username !== null) {
          await getUserBaseInfo(response.data.username);
          await router.push('/');
        }
      } catch (err) {
        ElMessage.error('获取用户信息失败，请稍后再试！');
      }
    } else {
      await router.push('/login');
    }
  } catch (error) {
    console.log("获取url参数失败")
  }
}

async function getUserBaseInfo(username) {
  try {
    const response = await axios.post('/api/user/getUserBaseInfo', {
      username: username
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.id !== null) {
      sessionStorage.setItem('userBaseInfo', JSON.stringify(response.data)); // 存储用户信息到sessionStorage
      document.cookie = `userBaseInfo=${encodeURIComponent(JSON.stringify(response.data))}; path=/ max-age=604800`; // 存储用户信息到cookie
    }
  } catch (err) {
    ElMessage.error('获取用户信息失败，请稍后再试！');
  }
}

handleClick();
</script>
