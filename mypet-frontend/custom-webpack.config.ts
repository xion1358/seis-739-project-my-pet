const webpack = require('webpack');
const dotenv = require('dotenv');
const path = require('path');

// Load environment variables from .env file
dotenv.config({ path: path.resolve(__dirname, '.env') });

module.exports = {
  plugins: [
    new webpack.DefinePlugin({
      'process.env.API_URL': JSON.stringify(process.env["API_URL"]),
      'process.env.SERVER_USERNAME': JSON.stringify(process.env["SERVER_USERNAME"]),
      'process.env.SERVER_PASSWORD': JSON.stringify(process.env["SERVER_PASSWORD"])
    })
  ]
};
