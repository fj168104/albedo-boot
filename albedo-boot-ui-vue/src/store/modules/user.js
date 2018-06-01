import { login, logout, getAccout } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'

const user = {
  state: {
    token: getToken(),
    name: '',
    avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
    authorities: []
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_NAME: (state, name) => {
      state.name = name
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
    },
    SET_AUTHORITIES: (state, authorities) => {
      state.authorities = authorities
    }
  },

  actions: {
    // 登录
    Login({ commit }, userInfo) {
      const username = userInfo.username.trim()
      return new Promise((resolve, reject) => {
        login(username, userInfo.password).then(response => {
          console.log(response)
          const bearerToken = response.data
          if (bearerToken && bearerToken.slice(0, 7) === 'Bearer ') {
            const token = bearerToken.slice(7, bearerToken.length)
            setToken(token)
            commit('SET_TOKEN', token)
          }
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 获取用户信息
    GetAccout({ commit, state }) {
      return new Promise((resolve, reject) => {
        getAccout().then(response => {
          const data = response.data
          console.log(data)
          if (data.authorities && data.authorities.length > 0) { // 验证返回的roles是否是一个非空数组
            commit('SET_AUTHORITIES', data.authorities)
          } else {
            reject('getAS: authorities must be a non-null array !')
          }
          commit('SET_NAME', data.name)
          data.avatar && commit('SET_AVATAR', data.avatar)
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 登出
    LogOut({ commit, state }) {
      return new Promise((resolve, reject) => {
        logout(state.token).then(() => {
          commit('SET_TOKEN', '')
          commit('SET_ROLES', [])
          removeToken()
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 前端 登出
    FedLogOut({ commit }) {
      return new Promise(resolve => {
        commit('SET_TOKEN', '')
        removeToken()
        resolve()
      })
    },

    //获取系统菜单
    GetMenu({ commit }, parentId) {
      parentId
      return new Promise(resolve => {
        getMenu(parentId).then((res) => {
          const data = res.data;
          commit('SET_MENU', data);
          resolve(data);
        })
      })
    },

  }
}

export default user
