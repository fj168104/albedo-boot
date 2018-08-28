import request from '@/router/axios'

export function fetchDictTree(query) {
  return request({
    url: '/dataSystem/dict/findTreeData',
    method: 'get',
    params: query
  })
}

export function pageDict(query) {
  return request({
    url: '/sys/dict/',
    method: 'get',
    params: query
  })
}

export function saveDict(obj) {
  return request({
    url: '/sys/dict/',
    method: 'post',
    data: obj
  })
}

export function findDict(id) {
  return request({
    url: '/sys/dict/' + id,
    method: 'get'
  })
}

export function removeDict(id) {
  return request({
    url: '/sys/dict/' + id,
    method: 'delete'
  })
}
export function lockDict(id) {
  return request({
    url: '/sys/dict/' + id,
    method: 'put'
  })
}
