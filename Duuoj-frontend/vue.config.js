const { defineConfig } = require("@vue/cli-service");
const MonacoWebpackPlugin = require("monaco-editor-webpack-plugin");

module.exports = defineConfig({
  transpileDependencies: true,
  chainWebpack(config) {
    config.plugin("monaco").use(new MonacoWebpackPlugin());
  },
  devServer: {
    // 项目启动端口之后会变成3000
    port: 8081,
  },
  // plugins: [vue()],
  // base: "./",
  // resolve: {
  //   alias: {
  //     /** @ 符号指向 src 目录 */
  //     "@": resolve(__dirname, "./src"),
  //   },
  // },
  // server: {
  //   proxy: {
  //     "/api": {
  //       target: "http://127.0.0.1:8104/api",
  //       changeOrigin: true,
  //       rewrite: (path) => path.replace(/^\/api/, ""),
  //     },
  //   },
  // },
});
