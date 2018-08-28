<!--
  -    Copyright (c) 2018-2025, lengleng All rights reserved.
  -
  - Redistribution and use in source and binary forms, with or without
  - modification, are permitted provided that the following conditions are met:
  -
  - Redistributions of source code must retain the above copyright notice,
  - this list of conditions and the following disclaimer.
  - Redistributions in binary form must reproduce the above copyright
  - notice, this list of conditions and the following disclaimer in the
  - documentation and/or other materials provided with the distribution.
  - Neither the name of the pig4cloud.com developer nor the names of its
  - contributors may be used to endorse or promote products derived from
  - this software without specific prior written permission.
  - Author: lengleng (wangiegie@gmail.com)
  -->

<template>
  <div class="menu-wrapper">
    <template v-for="(item,index) in menu">
      <el-menu-item v-if="validateNull(item.children)"
                    :index="filterPath(item.href,index)" :class="(item.menuTop && item.href=='')? 'is-active section-text':''"
                    @click="open(item)" :key="item.name">
        <i :class="item.iconCls" v-show="!item.menuTop"></i>
        <span slot="title">{{item.name}}</span>
      </el-menu-item>
      <el-submenu v-else :index="filterPath(item.name,index)" :key="item.name">
        <template slot="title">
          <i :class="item.iconCls"></i>
          <span slot="title" :class="{'el-menu--display':isCollapse}">{{item.name}}</span>
        </template>
        <template v-for="(child,cindex) in item.children">
          <el-menu-item :index="filterPath(child.href,cindex)" @click="open(child)" v-if="validateNull(item.children)" :key="cindex">
            <i :class="child.icoCls"></i>
            <span slot="title">{{child.name}}</span>
          </el-menu-item>
          <sidebar-item v-else :menu="[child]" :key="cindex" :isCollapse="isCollapse"></sidebar-item>
        </template>
      </el-submenu>
    </template>
  </div>
</template>
<script>
import { resolveUrlPath } from "@/util/util";
import {validateNull} from "@/util/validate";
export default {
  name: "SidebarItem",
  data() {
    return {};
  },
  props: {
    menu: {
      type: Array
    },
    isCollapse: {
      type: Boolean
    }
  },
  created() {
  },
  mounted() {},
  computed: {},
  methods: {
    filterPath(path, index) {
      return path == null ? index + "" : path;
    },
    validateNull(val){
      return validateNull(val);
    },
    open(item) {
      if(item.href){
        this.$router.push({
          path: resolveUrlPath(item.href, item.name),
          query: item.query
        });
      }
    }
  }
};
</script>
<style lang="scss" scoped>
  .section-text{line-height: 32px;height: 32px;color:#909399 !important;}
</style>

