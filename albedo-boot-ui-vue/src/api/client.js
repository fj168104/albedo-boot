

import request from '@/router/axios'

export function fetchList(query) {
  return request({
    url: '/sys/client/page',
    method: 'get',
    params: query
  })
}

export function addObj(obj) {
  return request({
    url: '/sys/client/',
    method: 'post',
    data: obj
  })
}

export function getObj(id) {
  return request({
    url: '/sys/client/' + id,
    method: 'get'
  })
}

export function delObj(id) {
  return request({
    url: '/sys/client/' + id,
    method: 'delete'
  })
}

export function putObj(obj) {
  return request({
    url: '/sys/client',
    method: 'put',
    data: obj
  })
}
