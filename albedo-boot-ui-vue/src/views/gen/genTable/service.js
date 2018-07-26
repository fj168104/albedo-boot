
import request from '@/router/axios'

export function pageGenTable(query) {
  return request({
    url: '/gen/genTable/',
    method: 'get',
    params: query
  })
}

export function saveGenTable(obj) {
  return request({
    url: '/gen/genTable/',
    method: 'post',
    data: obj
  })
}

export function findGenTable(id) {
  return request({
    url: '/gen/genTable/' + id,
    method: 'get'
  })
}

export function removeGenTable(id) {
  return request({
    url: '/gen/genTable/' + id,
    method: 'delete'
  })
}


