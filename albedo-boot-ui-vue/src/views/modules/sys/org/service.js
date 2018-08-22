import request from '@/router/axios'

export function fetchOrgTree(query) {
  return request({
    url: '/dataSystem/org/findTreeData',
    method: 'get',
    params: query
  })
}

export function saveOrg(obj) {
  return request({
    url: '/sys/org/',
    method: 'post',
    data: obj
  })
}

export function findOrg(id) {
  return request({
    url: '/sys/org/' + id,
    method: 'get'
  })
}

export function removeOrg(id) {
  return request({
    url: '/sys/org/' + id,
    method: 'delete'
  })
}
export function lockOrg(id) {
  return request({
    url: '/sys/org/' + id,
    method: 'put'
  })
}
