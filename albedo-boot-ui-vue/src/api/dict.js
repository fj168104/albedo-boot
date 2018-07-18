

import request from '@/router/axios'

export function fetchList(query) {
  return request({
    url: '/sys/dict/dictPage',
    method: 'get',
    params: query
  })
}

export function addObj(obj) {
  return request({
    url: '/sys/dict/',
    method: 'post',
    data: obj
  })
}

export function getObj(id) {
  return request({
    url: '/sys/dict/' + id,
    method: 'get'
  })
}

export function delObj(row) {
  return request({
    url: '/sys/dict/' + row.id + '/' + row.type,
    method: 'delete'
  })
}

export function putObj(obj) {
  return request({
    url: '/sys/dict/',
    method: 'put',
    data: obj
  })
}

export function remote(type) {
  return request({
    url: '/sys/dict/type/' + type,
    method: 'get'
  })
}
