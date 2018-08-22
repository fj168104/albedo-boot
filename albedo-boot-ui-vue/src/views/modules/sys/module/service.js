import request from '@/router/axios'

export function fetchModuleTree(query) {
  return request({
    url: '/dataSystem/module/findTreeData',
    method: 'get',
    params: query
  })
}
export function fetchModuleMenu() {
  return request({
    url: '/dataSystem/module/findTreeData?all&type=menu',
    method: 'get'
  })
}

export function lockModule(id) {
  return request({
    url: '/sys/module/' + id,
    method: 'put'
  })
}

export function pageModule(query) {
  return request({
    url: '/sys/module/',
    method: 'get',
    params: query
  })
}

export function saveModule(obj) {
  return request({
    url: '/sys/module/',
    method: 'post',
    data: obj
  })
}

export function findModule(id) {
  return request({
    url: '/sys/module/' + id,
    method: 'get'
  })
}

export function removeModule(id) {
  return request({
    url: '/sys/module/' + id,
    method: 'delete'
  })
}
