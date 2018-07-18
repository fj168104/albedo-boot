

import request from '@/router/axios'

export function fetchList(query) {
  return request({
    url: '/sys/log/logPage',
    method: 'get',
    params: query
  })
}

export function delObj(id) {
  return request({
    url: '/sys/log/' + id,
    method: 'delete'
  })
}

export function addObj(obj) {
  return request({
    url: '/sys/user/',
    method: 'post',
    data: obj
  })
}

export function getObj(id) {
  return request({
    url: '/sys/user/' + id,
    method: 'get'
  })
}

export function putObj(obj) {
  return request({
    url: '/sys/user/',
    method: 'put',
    data: obj
  })
}
