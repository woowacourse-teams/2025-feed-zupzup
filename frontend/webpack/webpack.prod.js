import { merge } from "webpack-merge";
import common from "./webpack.common.js";
import path from "path";
import { fileURLToPath } from "url";
import { dirname } from "path";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

export default merge(common, {
  mode: "production",
  output: {
    filename: "bundle.[contenthash].js",
    path: path.resolve(__dirname, "../dist"),
    clean: true,
  },
  devtool: "source-map",
});
