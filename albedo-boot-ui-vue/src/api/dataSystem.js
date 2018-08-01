import request from '@/router/axios'

export function menus() {
  return request({
    url: '/dataSystem/module/data',
    method: 'get',
    params: { type: 'menu' }
  })
}

export function moduleData(){
  return request({
    url: '/dataSystem/module/data',
    method: 'get'
  });
}

export function dictCodes(params){
  return request({
    url: '/dataSystem/dict/codes',
    method: 'get',
    params: params
  });
}
