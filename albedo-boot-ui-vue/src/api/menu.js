

import request from '@/router/axios'

export function fetchTree(query) {
  return request({
    url: '/sys/menu/allTree',
    method: 'get',
    params: query
  })
}

export function addObj(obj) {
  return request({
    url: '/sys/menu/',
    method: 'post',
    data: obj
  })
}

export function getObj(id) {
  return request({
    url: '/sys/menu/' + id,
    method: 'get'
  })
}

export function delObj(id) {
  return request({
    url: '/sys/menu/' + id,
    method: 'delete'
  })
}

export function putObj(obj) {
  return request({
    url: '/sys/menu/',
    method: 'put',
    data: obj
  })
}
