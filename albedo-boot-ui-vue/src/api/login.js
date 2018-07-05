

import { baseUrl, khglUrl, dicUrl } from '@/config/env'
import request from '@/router/axios'
import { userInfo, tableData } from '@/mock/user'
import { menu, menuAll } from '@/mock/menu'
export const loginByUsername = (username, password, code, randomStr) => {
  var grant_type = 'password'
  var scope = 'server'
  return request({
    url: '/authenticate',
    method: 'post',
    data: {
      username,
      password
    }
  })
}

export function mobileLogin(mobile, code) {
  var grant_type = 'mobile'
  var scope = 'server'
  return request({
    url: '/auth/mobile/token',
    headers: {
      'Authorization': 'Basic cGlnOnBpZw=='
    },
    method: 'post',
    params: { mobile, code, grant_type, scope }
  })
}

export const getUserInfo = () => {
  return request({
    url: '/account',
    method: 'get'
  })
}

export const logout = (accesstoken, refreshToken) => {
  return request({
    url: '/logout',
    method: 'get',
    params: { accesstoken, refreshToken }
  })
}
