

/**
 *
 * http配置
 *
 */

import axios from 'axios'
import store from '../store'
import { getToken, setToken, removeToken } from '@/util/auth'
import { Message } from 'element-ui'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css'
import {MSG_TYPE_ERROR, MSG_TYPE_INFO, MSG_TYPE_SUCCESS, MSG_TYPE_WARNING} from "../const/common";
import {validateNotNull} from "../util/validate";
import {baseUrl} from "../config/env";
// progress bar style

axios.defaults.baseURL = baseUrl;
// 超时时间
axios.defaults.timeout = 30000
// 跨域请求，允许保存cookie
axios.defaults.withCredentials = true
NProgress.configure({ showSpinner: false })// NProgress Configuration
let msg
// HTTPrequest拦截
axios.interceptors.request.use(config => {
  NProgress.start() // start progress bar
  if (store.getters.access_token) {
    config.headers['Authorization'] = getToken() // 让每个请求携带token--['X-Token']为自定义key 请根据实际情况自行修改
  }
  return config
}, error => {
  return Promise.reject(error)
})
// HTTPresponse拦截
axios.interceptors.response.use(response => {
  NProgress.done()
  if(response && response.status!=200){
    console.error(response)
    Message({
      message: "请求异常",
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject('error');
  }
  var data=response.data;
  if (data) {
    if (validateNotNull(data.message)) {
      Message({
        message: data.message,
        type: data.status == MSG_TYPE_SUCCESS ? 'success' : data.status == MSG_TYPE_ERROR ? 'error' : data.status == MSG_TYPE_WARNING ? 'warning' : 'info',
        duration: 5 * 1000
      })
    }
  }
  return data
}, error => {
  NProgress.done()
  console.log('err' + error)// for debug
  Message({
    message: error.response && error.response.data && error.response.data.message ? error.response.data.message : error.message,
    type: 'error',
    duration: 5 * 1000
  })
  return Promise.reject(error)
})

export default axios
