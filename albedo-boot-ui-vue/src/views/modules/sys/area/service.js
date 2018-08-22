import request from '@/router/axios'

export function fetchAreaTree(query) {
  return request({
    url: '/sys/area/findTreeData',
    method: 'get',
    params: query
  })
}

export function pageArea(query) {
  return request({
    url: '/sys/area/',
    method: 'get',
    params: query
  })
}

export function saveArea(obj) {
  return request({
    url: '/sys/area/',
    method: 'post',
    data: obj
  })
}

export function findArea(id) {
  return request({
    url: '/sys/area/' + id,
    method: 'get'
  })
}

export function removeArea(id) {
  return request({
    url: '/sys/area/' + id,
    method: 'delete'
  })
}
export function lockArea(id) {
  return request({
    url: '/sys/area/' + id,
    method: 'put'
  })
}
