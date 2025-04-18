const webpack = require('webpack');
const dotenv = require('dotenv');
const path = require('path');

// Load environment variables from .env file
dotenv.config({ path: path.resolve(__dirname, '.env') });

module.exports = {
  plugins: [
    new webpack.DefinePlugin({
      'global': 'window'
    })
  ]
};
