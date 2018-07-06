
import request from '@/router/axios'

export function getMetrics(query) {
  return request({
    url: '/management/metrics',
    method: 'get',
    params: query
  })
}

export function threadDump(obj) {
  return request({
    url: '/management/dump',
    method: 'post',
    data: obj
  })
}
