<template>
  <!--  <div id="box">-->
  <!--    &lt;!&ndash; 设置自动播放，否则不会显示视频流画面 &ndash;&gt;-->
  <!--    <video id="video" ref="video" autoplay></video>-->
  <!--    <canvas style="display: contents;"></canvas>-->
  <!--    <div id="btn">-->
  <!--      <button ref="button_one" @click="publish">开始直播</button>-->
  <!--      <button ref="button_two" @click="close">停止直播</button>-->
  <!--      <button ref="button_three" @click="stopAudio">关闭声音</button>-->
  <!--      <button ref="button_four" @click="startAudio">开启声音</button>-->
  <!--      <button ref="button_five" @click="play">播放直播</button>-->
  <!--    </div>-->
  <!--    <video id="video2" ref="video2" autoplay></video>-->
  <!--  </div>-->
  <div>
    <el-button type="primary">测试</el-button>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';

const videoStream = ref(null);
const videoElement = ref(null);
const pc = ref(null);
const audioTrack = ref(null);
const audioSender = ref(null);
const button_one = ref(null);
const button_two = ref(null);
const button_three = ref(null);
const button_four = ref(null);
const button_five = ref(null);

onMounted(() => {
  button_one.disabled = false;
  button_two.disabled = true;
  button_three.disabled = true;
  button_four.disabled = true;
  button_five.disabled = true;
});

async function publish() {
  if (pc.value !== null && pc.value !== undefined) {
    console.log("已开始推流");
    return;
  }
  const httpURL = "http://localhost:1985/rtc/v1/publish/";
  const webRTCURL = "webRTC://localhost/live/10";
  const constraints = {
    audio: {
      echoCancellation: true,    // 回声消除
      noiseSuppression: true,    // 降噪
      autoGainControl: true     // 自动增益
    },
    video: {
      frameRate: {min: 30},                // 最小帧率
      width: {min: 640, ideal: 1080}, // 宽度
      height: {min: 360, ideal: 720},  // 高度
      aspectRadio: 16 / 9                        // 宽高比
    }
  };

  videoStream.value = await navigator.mediaDevices.getUserMedia(constraints);
  videoElement.value = document.querySelector("#video");
  videoElement.value.srcObject = videoStream.value;
  videoElement.value.volume = 0;
  pc.value = new RTCPeerConnection();

  pc.value.addTransceiver("audio", {direction: "recvonly"});
  pc.value.addTransceiver("video", {direction: "recvonly"});
  videoStream.value.getTracks().forEach((track) => {
    pc.value.addTrack(track);
  });

  const offer = await pc.value.createOffer();
  await pc.value.setLocalDescription(offer);
  const data = {
    "api": httpURL,
    "streamurl": webRTCURL,
    "sdp": offer.sdp
  };

  httpApi(httpURL, data).then(async (data) => {
    console.log("answer", data);
    await pc.value.setRemoteDescription(new RTCSessionDescription({type: 'answer', sdp: data.sdp}));
    button_one.disabled = true;
    button_two.disabled = false;
    button_three.disabled = false;
    button_five.disabled = false;
  }).catch((data) => {
    if (data.code === 400) {
      console.log("SDP交换失败");
    }
  });
}

async function play() {
  const httpURL = "http://localhost:1985/rtc/v1/play/";
  const webRTCURL = "webRTC://localhost/live/888";
  const localPc = new RTCPeerConnection();
  const stream = new MediaStream();
  const videoElement2 = document.querySelector("#video2");

  localPc.ontrack = (event) => {
    stream.addTrack(event.track);
    console.log(event.track);
    videoElement2.srcObject = stream;
  };

  localPc.addTransceiver("audio", {direction: "recvonly"});
  localPc.addTransceiver("video", {direction: "recvonly"});

  const offer = await localPc.createOffer();
  await localPc.setLocalDescription(offer);
  const data = {
    "api": httpURL,
    "streamurl": webRTCURL,
    "sdp": offer.sdp
  };

  httpApi(httpURL, data).then(async (data) => {
    console.log("answer", data);
    await localPc.setRemoteDescription(new RTCSessionDescription({type: 'answer', sdp: data.sdp}));
    button_five.disabled = true;
  }).catch((data) => {
    if (data.code === 400) {
      console.log("SDP交换失败");
    }
  });
}

function close() {
  if (pc.value !== null && pc.value !== undefined) {
    pc.value.close();
    pc.value = null;
    button_one.disabled = false;
    button_two.disabled = true;
    button_three.disabled = true;
    button_four.disabled = true;
    button_five.disabled = true;
  }
}

function stopAudio() {
  if (pc.value !== null && pc.value !== undefined) {
    pc.value.getSenders().forEach((sender) => {
      if (sender.track !== null && sender.track.kind === "audio") {
        audioTrack.value = sender.track;
        audioSender.value = sender;
        audioSender.value.replaceTrack(null);
        button_three.disabled = true;
        button_four.disabled = false;
      }
    });
  }
}

function startAudio() {
  if (pc.value !== null && pc.value !== undefined) {
    if (audioSender.value.track === null) {
      audioSender.value.replaceTrack(audioTrack.value);
      button_three.disabled = false;
      button_four.disabled = true;
    }
  }
}

function httpApi(httpURL, data) {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    xhr.open('POST', httpURL, true);
    xhr.setRequestHeader('Content-type', 'application/json');
    xhr.send(JSON.stringify(data));
    xhr.onload = () => {
      if (xhr.readyState !== xhr.DONE) reject(xhr);
      if (xhr.status !== 200 && xhr.status !== 201) reject(xhr);
      const responseData = JSON.parse(xhr.responseText);
      if (responseData.code === 0) {
        resolve(responseData);
      } else {
        reject(responseData);
      }
    };
  });
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  border: 0;
  box-sizing: border-box;
}

#box {
  width: 100%;
  text-align: center;
}

video {
  background-color: black;
  width: 500px;
  height: 400px;
  object-fit: cover;
}

#btn {
  width: 80%;
  height: 100px;
  display: flex;
  margin: 10px 10%;
}

button {
  flex: 1;
  height: 100px;
  background-color: aqua;
  border-radius: 20px;
  margin-left: 10px;
}

button:nth-child(1) {
  margin-left: 0;
}
</style>
推流/拉流示例代码