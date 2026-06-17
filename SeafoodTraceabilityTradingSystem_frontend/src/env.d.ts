declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

declare module 'vue3-puzzle-vcode' {
  import { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

declare module 'v-viewer' {
  import { Plugin } from 'vue'
  const plugin: Plugin
  export default plugin
}

declare module 'emoji-picker-element' {
  export default class EmojiPicker extends HTMLElement {}
}

declare module 'vue3-slide-verify' {
  import { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}
