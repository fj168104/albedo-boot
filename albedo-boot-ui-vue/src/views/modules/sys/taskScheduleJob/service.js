import request from '@/router/axios'

export function pageTaskScheduleJob(query) {
  return request({
    url: '/sys/taskScheduleJob/',
    method: 'get',
    params: query
  })
}

export function saveTaskScheduleJob(obj) {
  return request({
    url: '/sys/taskScheduleJob/',
    method: 'post',
    data: obj
  })
}

export function findTaskScheduleJob(id) {
  return request({
    url: '/sys/taskScheduleJob/' + id,
    method: 'get'
  })
}

export function removeTaskScheduleJob(id) {
  return request({
    url: '/sys/taskScheduleJob/' + id,
    method: 'delete'
  })
}
export function lockTaskScheduleJob(id) {
  return request({
    url: '/sys/taskScheduleJob/' + id,
    method: 'put'
  })
}