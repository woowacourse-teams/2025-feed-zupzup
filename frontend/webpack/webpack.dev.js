import { merge } from "webpack-merge";
import common from "./webpack.common.js";

export default merge(common, {
  mode: "development",
  devtool: "inline-source-map",
  devServer: {
    static: "./dist",
    port: 3000,
    open: {
      app: {
        name: "google chrome",
      },
    },
    hot: true,
    historyApiFallback: true,
  },
});
