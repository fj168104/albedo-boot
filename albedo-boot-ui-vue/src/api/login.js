import request from '@/utils/request'

export function login(username, password) {
  return request({
    url: '/authenticate',
    method: 'post',
    data: {
      username,
      password
    }
  })
}

export function getAccout() {
  return request({
    url: '/account',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/logout',
    method: 'get'
  })
}
