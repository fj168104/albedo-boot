
import request from '@/router/axios'

export function pageUser(query) {
  return request({
    url: '/sys/user/',
    method: 'get',
    params: query
  })
}

export function saveUser(obj) {
  return request({
    url: '/sys/user/',
    method: 'post',
    data: obj
  })
}

export function findUser(id) {
  return request({
    url: '/sys/user/' + id,
    method: 'get'
  })
}

export function removeUser(id) {
  return request({
    url: '/sys/user/' + id,
    method: 'delete'
  })
}


