const {defineConfig} = require('@vue/cli-service');

module.exports = defineConfig({
    lintOnSave: false, // 禁用ESLint自动检查

    devServer: {
        port: 8081,
        proxy: {
            '/api': {
                target: 'http://localhost:8888',
                changeOrigin: true,
                pathRewrite: {'^/api': ''},
            },
        },
    },

    // 添加 webpack 的 resolve.fallback 配置
    configureWebpack: {
        resolve: {
            fallback: {
                crypto: require.resolve('crypto-browserify'), // 添加 crypto 的 polyfill
                stream: require.resolve('stream-browserify'), // 添加 stream 的 polyfill
                vm: require.resolve('vm-browserify'),
            },
        },
    },
});
