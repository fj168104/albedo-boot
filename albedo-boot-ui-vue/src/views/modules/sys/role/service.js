
import request from '@/router/axios'

export function pageRole(query) {
  return request({
    url: '/sys/role/',
    method: 'get',
    params: query
  })
}

export function saveRole(obj) {
  return request({
    url: '/sys/role/',
    method: 'post',
    data: obj
  })
}

export function findRole(id) {
  return request({
    url: '/sys/role/' + id,
    method: 'get'
  })
}

export function removeRole(id) {
  return request({
    url: '/sys/role/' + id,
    method: 'delete'
  })
}

export function lockRole(id) {
  return request({
    url: '/sys/role/' + id,
    method: 'put'
  })
}

export function comboRoleList() {
  return request({
    url: '/sys/role/comboData',
    method: 'get'
  })
}
