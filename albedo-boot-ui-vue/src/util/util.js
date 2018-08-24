

import {
  validateNull
} from './validate'
import {
  baseUrl
} from '@/config/env'
import {validateNotNull} from "../packages/utils/validate";

export const initMenu = (router, menu) => {
  if (menu.length === 0) {
    return
  }
  router.addRoutes(formatRoutes(menu, false))
}

export const formatRoutes = (aMenu, isChild) => {
  const aRouter = []
  aMenu.forEach(oMenu => {
    const {
      href,
      component,
      name,
      iconCls,
      children
    } = oMenu
    let componentPath = component, nullChild = validateNull(children)
    let notNullPath = validateNotNull(componentPath) && componentPath!='Layout'
    // if (!validateNull(component)) {
      const oRouter = {
        path: nullChild && notNullPath && !isChild ? "" : href,
        component(resolve) {
          if ((validateNull(href) || nullChild) && !isChild) {
            require(['../page/index'], resolve)
            return
          }
          if(notNullPath){
            require([`../${componentPath}.vue`], resolve)
          }
        },
        name: nullChild && notNullPath && !isChild ? undefined : name,
        icon: iconCls,
        children: nullChild ? notNullPath && !isChild ? [{
            path: href,
            name: name,
            component(resolve) {
              require([`../${componentPath}.vue`], resolve)
            }
          }] : [] : formatRoutes(children, true)
      }
      if(oRouter.children.length>0 || notNullPath){
        aRouter.push(oRouter)
      }
    // }

  })
  return aRouter
}

export const parseTreeData = (dataList) => {
  var treeData=[],getChild = function (id) {
    return dataList.filter((item) => {
      return item.pid == id
    })
  },parseData = function(item){
    var childs = getChild(item.id);
    if(!validateNull(childs)){
      item.children = childs;
      childs.forEach(temp => {
        parseData(temp)
      })
    }
  }
  dataList.forEach(item => {
    if (item.id==1 || validateNull(item.pid)|| item.pid==0) {
      parseData(item)
      treeData.push(item)
    }
  })
  return treeData;
}

/**
 * 加密处理
 */
export const encryption = (params) => {
  let {
    data,
    type,
    param,
    key
  } = params
  let result = JSON.parse(JSON.stringify(data))
  if (type === 'Base64') {
    param.forEach(ele => {
      result[ele] = btoa(result[ele])
    })
  } else if (type === 'Aes') {
    param.forEach(ele => {
      result[ele] = CryptoJS.AES.encrypt(result[ele], key).toString()
    })
  }
  return result
}

/**
 * 设置浏览器头部标题
 */
export const setTitle = function (title) {
  title = title ? `${title}——albedo-boot` : 'albedo-boot';
  window.document.title = title
}
/**
 * 浏览器判断是否全屏
 */
export const fullscreenToggel = () => {
  if (fullscreenEnable()) {
    exitFullScreen()
  } else {
    reqFullScreen()
  }
}
/**
 * esc监听全屏
 */
export const listenfullscreen = (callback) => {
  function listen() {
    callback()
  }
  document.addEventListener('fullscreenchange', function (e) {
    listen()
  })
  document.addEventListener('mozfullscreenchange', function (e) {
    listen()
  })
  document.addEventListener('webkitfullscreenchange', function (e) {
    listen()
  })
  document.addEventListener('msfullscreenchange', function (e) {
    listen()
  })
}

/**
 * 浏览器判断是否全屏
 */
export const fullscreenEnable = () => {
  var isFullscreen = document.fullscreenEnabled ||
    window.fullScreen ||
    document.mozFullscreenEnabled ||
    document.webkitIsFullScreen
  return isFullscreen
}

/**
 * 浏览器全屏
 */
export const reqFullScreen = () => {
  if (document.documentElement.requestFullScreen) {
    document.documentElement.requestFullScreen()
  } else if (document.documentElement.webkitRequestFullScreen) {
    document.documentElement.webkitRequestFullScreen()
  } else if (document.documentElement.mozRequestFullScreen) {
    document.documentElement.mozRequestFullScreen()
  }
}

/**
 * 浏览器退出全屏
 */
export const exitFullScreen = () => {
  if (document.documentElement.requestFullScreen) {
    document.exitFullScreen()
  } else if (document.documentElement.webkitRequestFullScreen) {
    document.webkitCancelFullScreen()
  } else if (document.documentElement.mozRequestFullScreen) {
    document.mozCancelFullScreen()
  }
}

/**
 * 递归寻找子类的父类
 */
export const findParent = (menu, id) => {
  if(!validateNull(menu)){
    for (let i = 0; i < menu.length; i++) {
      if (!validateNull(menu[i].children)) {
        for (let j = 0; j < menu[i].children.length; j++) {
          if (menu[i].children[j].id === id) {
            return menu[i]
          } else {
            if (!validateNull(menu[i].children[j].children)) {
              return findParent(menu[i].children[j].children, id)
            }
          }
        }
      }
    }
  }

}

/**
 * 总体路由处理器
 */
export const resolveUrlPath = (url, name) => {
  let reqUrl = url
  if (url.indexOf('#') !== -1 && url.indexOf('http') === -1) {
    const port = reqUrl.substr(reqUrl.indexOf(':'))
    reqUrl = `/myiframe/urlPath?src=${baseUrl}${port}${reqUrl.replace('#', '').replace(port, '')}}&name=${name}`
  } else if (url.indexOf('http') !== -1) {
    reqUrl = `/myiframe/urlPath?src=${reqUrl}&name=${name}`
  } else {
    reqUrl = `${reqUrl}`
  }
  console.log(reqUrl)
  return reqUrl
}

/**
 * 总体路由设置器
 */
export const setUrlPath = ($route) => {
  let value = ''
  if ($route.query.src) {
    value = $route.query.src
  } else {
    value = $route.path
  }
  return value
}

/**
 * 动态插入css
 */
export const loadStyle = url => {
  const link = document.createElement('link')
  link.type = 'text/css'
  link.rel = 'stylesheet'
  link.href = url
  const head = document.getElementsByTagName('head')[0]
  head.appendChild(link)
}

/**
 * 根据字典的value显示label
 */
export const findByvalue = (dic, value) => {
  let result = ''
  if (validateNull(dic)) return value
  if (typeof (value) === 'string' || typeof (value) === 'number') {
    let index = 0
    index = findArray(dic, value)
    if (index !== -1) {
      result = dic[index].label
    } else {
      result = value
    }
  } else if (value instanceof Array) {
    result = []
    let index = 0
    value.forEach(ele => {
      index = findArray(dic, ele)
      if (index !== -1) {
        result.push(dic[index].label)
      } else {
        result.push(value)
      }
    })
    result = result.toString()
  }
  return result
}

/**
 * 根据字典的value查找对应的index
 */
export const findArray = (dic, value) => {
  for (let i = 0; i < dic.length; i++) {
    if (dic[i].value === value) {
      return i
    }
  }
  return -1
}

/**
 * 生成随机len位数字
 */
export const randomLenNum = (len, date) => {
  let random = ''
  random = Math.ceil(Math.random() * 100000000000000).toString().substr(0, typeof len === 'number' ? len : 4)
  if (date) random = random + Date.now()
  return random
}


export const parseJsonItemForm = function (formItems) {
  var i = 0, json_list = [];
  if(validateNull(formItems)){
    return null;
  }
  let option =
  formItems.forEach(item => {
    var filterItem={}
    if(item instanceof String){
      filterItem = {
        format:'',
        fieldName:item,
        attrType:'String',
        fieldNode:'',
        operate:'like',
        weight:0,
        value:formItems[item],
        endValue:'',
      }
    }else{
      filterItem = jQuery.extend(true,{
        format:'',
        fieldName:'',
        attrType:'String',
        fieldNode:'',
        operate:'like',
        weight:0,
        value:'',
        endValue:'',
      }, item)
    }

    if(validateNotNull(filterItem.value)){
      json_list[i++] = filterItem;
    }
  })
  return JSON.stringify(json_list);
}
export function formatDateDay(date){
  return formatDate(date, 'yyyy-MM-dd')
}
export function formatDate(date,fmt){
  if(/(y+)/.test(fmt)){
    fmt = fmt.replace(RegExp.$1, (date.getFullYear()+'').substr(4-RegExp.$1.length));
  }
  let o = {
    'M+': date.getMonth()+1,
    'd+': date.getDate(),
    'h+': date.getHours(),
    'm+': date.getMinutes(),
    's+': date.getSeconds()
  }
  for(let k in o){
    let str = o[k]+'';
    if(new RegExp(`(${k})`).test(fmt)){
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length===1)?str:padLeftZero(str));
    }
  }
  return fmt;
};

function padLeftZero(str){
  return ('00'+str).substr(str.length);
}

export function getCtxFile(path){
  return validateNull(path) ? '' : (baseUrl+'/file/get/'+path)
}
