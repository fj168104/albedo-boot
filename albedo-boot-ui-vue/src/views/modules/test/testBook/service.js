import request from '@/router/axios'

export function pageTestBook(query) {
  return request({
    url: '/test/testBook/',
    method: 'get',
    params: query
  })
}

export function saveTestBook(obj) {
  return request({
    url: '/test/testBook/',
    method: 'post',
    data: obj
  })
}

export function findTestBook(id) {
  return request({
    url: '/test/testBook/' + id,
    method: 'get'
  })
}

export function removeTestBook(id) {
  return request({
    url: '/test/testBook/' + id,
    method: 'delete'
  })
}
export function lockTestBook(id) {
  return request({
    url: '/test/testBook/' + id,
    method: 'put'
  })
}