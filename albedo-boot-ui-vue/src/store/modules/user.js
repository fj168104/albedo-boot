

import {
  getToken,
  setToken,
  removeToken
} from '@/util/auth'
import {
  setStore,
  getStore
} from '@/util/store'
import {
  validateNull
} from '@/util/validate'
import {
  loginByUsername,
  mobileLogin,
  getUserInfo,
  logout
} from '@/api/login'
import {
  menus
} from '@/api/dataSystem'
const user = {
  state: {
    userInfo: getStore({
      name: 'userInfo'
    }) || {},
    authorities: getStore({
      name: 'authorities'
    }) || [],
    roles: getStore({
      name: 'roles'
    }) || [],
    menu: getStore({
      name: 'menu'
    }) || [],
    isInitMenu: getStore({
      name: 'isInitMenu'
    }) || false,
    access_token: getStore({
      name: 'access_token'
    }) || '',
    refresh_token: getStore({
      name: 'refresh_token'
    }) || ''
  },
  actions: {
    // 根据用户名登录
    LoginByUsername({
      commit,
      state,
      dispatch
    }, userInfo) {
      return new Promise((resolve, reject) => {
        loginByUsername(userInfo.username, userInfo.password, userInfo.code, userInfo.randomStr).then(response => {
          console.log(response);
          const data = response.data
          setToken(data)
          commit('SET_ACCESS_TOKEN', data)
          // commit('SET_REFRESH_TOKEN', data.refresh_token)
          commit('CLEAR_LOCK')
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },
    // 根据手机号登录
    LoginByPhone({
      commit,
      state,
      dispatch
    }, userInfo) {
      const mobile = userInfo.mobile.trim()
      return new Promise((resolve, reject) => {
        mobileLogin(mobile, userInfo.code).then(response => {
          const data = response.data
          setToken(data.access_token)
          commit('SET_ACCESS_TOKEN', data.access_token)
          commit('SET_REFRESH_TOKEN', data.refresh_token)
          commit('CLEAR_LOCK')
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },
    GetTableData({
      commit,
      state,
      dispatch
    }, page) {
      return new Promise((resolve, reject) => {
        // 未定义
        // getTableData(page).then(res => {
        //   const data = res.data
        //   resolve(data)
        // })
      })
    },
    GetUserInfo({
      commit,
      state,
      dispatch
    }) {
      return new Promise((resolve, reject) => {
        getUserInfo().then(response => {
          console.log("getUserInfo"+response)
          const data = response.data;
          commit('SET_USER_INFO', data);
          data.roles && commit('SET_ROLES', data.roles)
          commit('SET_AUTHORITIES', data.authorities)
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },
    // 登出
    LogOut({
      commit,
      state
    }) {
      return new Promise((resolve, reject) => {
        logout(state.access_token, state.refresh_token).then(() => {
          // 清除菜单
          commit('SET_MENU', [])
          // 清除权限
          commit('SET_AUTHORITIES', [])
          // 清除用户信息
          commit('SET_USER_INFO', {})
          commit('SET_ACCESS_TOKEN', '')
          commit('SET_REFRESH_TOKEN', '')
          commit('SET_ROLES', [])
          commit('DEL_ALL_TAG')
          removeToken()
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },
    // 注销session
    FedLogOut({
      commit
    }) {
      return new Promise(resolve => {
        // 清除菜单
        commit('SET_MENU', [])
        // 清除权限
        commit('SET_AUTHORITIES', [])
        // 清除用户信息
        commit('SET_USER_INFO', {})
        commit('SET_ACCESS_TOKEN', '')
        commit('SET_REFRESH_TOKEN', '')
        commit('SET_ROLES', [])
        commit('DEL_ALL_TAG')
        removeToken()
        resolve()
      })
    },
    // 获取系统菜单
    GetMenu({
      commit
    }) {
      return new Promise(resolve => {
        menus().then((res) => {
          console.log(res)
          // const data = res.data
          const menus = res.data.moduleList,menusData=[];
          var getChildMenus = function (id) {
            return menus.filter((item) => {
              return item.parentId == id
            })
          }
          menus.forEach(item => {
            if (item.menuTop) {
              menusData.push(item)
              getChildMenus(item.id).forEach(itemChild => {
                if (!itemChild.menuLeaf) {
                  itemChild.children = getChildMenus(itemChild.id)
                }
                menusData.push(itemChild)
              })
            }
          })
          // data.forEach(ele => {
          //   ele.children.forEach(child => {
          //     if (!validateNull(child.component)) child.path = `${ele.path}/${child.path}`
          //   });
          // });
          commit('SET_MENU', menusData)
          resolve(menusData)
        })
      })
    }
  },
  mutations: {
    SET_ACCESS_TOKEN: (state, access_token) => {
      state.access_token = access_token
      setStore({
        name: 'access_token',
        content: state.access_token,
        type: 'session'
      })
    },
    SET_MENU: (state, menu) => {
      state.menu = menu
      setStore({
        name: 'menu',
        content: state.menu,
        type: 'session'
      })
    },
    SET_USER_INFO: (state, userInfo) => {
      state.userInfo = userInfo
      setStore({
        name: 'userInfo',
        content: state.userInfo,
        type: 'session'
      })
    },
    SET_AVATAR: (state, avatar) => {
      state.userInfo.avatar = avatar
      setStore({
        name: 'userInfo',
        content: state.userInfo,
        type: 'session'
      })
    },
    SET_REFRESH_TOKEN: (state, rfToken) => {
      state.refresh_token = rfToken
      setStore({
        name: 'refresh_token',
        content: state.refresh_token,
        type: 'session'
      })
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
      setStore({
        name: 'roles',
        content: state.roles,
        type: 'session'
      })
    },
    SET_AUTHORITIES: (state, authorities) => {
      console.log("SET_AUTHORITIES:"+authorities)
      state.authorities = authorities
      setStore({
        name: 'authorities',
        content: state.authorities,
        type: 'session'
      })
    }
  }
}
export default user
