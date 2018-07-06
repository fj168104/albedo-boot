
<template>
  <div class="app-container calendar-list-container">
    <el-row :gutter="20">
      <el-col :span="4">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>模块</span>
            <el-button type="text" style="float: right; padding: 3px 0" @click="searchTree=(searchTree ? false:true)">搜索</el-button>
          </div>
          <el-input v-show="searchTree"
            placeholder="输入关键字进行过滤"
            v-model="filterText">
          </el-input>
          <el-tree
            class="filter-tree"
            :data="treeData"
            ref="leftTree"
            node-key="id"
            highlight-current
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
            @node-click="getNodeData"
            default-expand-all >
          </el-tree>
        </el-card>
      </el-col>
      <el-col :span="20">
        <div class="filter-container">
            <el-form :inline="true">
              <el-form-item label="名称">
                <el-input style="width: 200px;" class="filter-item" v-model="listQuery.name"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">搜索</el-button>
                <el-button v-if="sys_module_edit" class="filter-item" style="margin-left: 10px;" @click="handleEdit" type="primary" icon="edit">添加</el-button>
              </el-form-item>
            </el-form>
        </div>
        <el-table :data="list" v-loading="listLoading" element-loading-text="加载中..." border fit highlight-current-row style="width: 99%">
          <el-table-column align="center" label="名称">
            <template slot-scope="scope">
              <span><i :class="scope.row.iconCls"></i>{{scope.row.name}}</span>
            </template>
          </el-table-column>

          <el-table-column align="center" label="类型">
            <template slot-scope="scope">
              <span>{{scope.row.type}}</span>
            </template>
          </el-table-column>

          <el-table-column align="center" label="权限">
            <template slot-scope="scope">
              <span>{{scope.row.permission}}</span>
            </template>
          </el-table-column>

          <el-table-column align="center" label="请求方法">
            <template slot-scope="scope">
              <span>{{scope.row.requestMethod}}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" label="链接">
            <template slot-scope="scope">
              <span>{{scope.row.url}}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" label="序号">
            <template slot-scope="scope">
              <span>{{scope.row.sort}}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" class-name="status-col" label="状态">
            <template slot-scope="scope">
              <el-tag>{{scope.row.status}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" label="更新时间">
            <template slot-scope="scope">
              <span>{{scope.row.lastModifiedDate}}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" width="200">
            <template slot-scope="scope">
              <el-button v-if="sys_module_edit" size="small" type="success" @click="handleEdit(scope.row)">编辑
              </el-button>
              <el-button v-if="sys_module_delete" size="small" type="danger" @click="handleDelete(scope.row)">删除
              </el-button>
            </template>
          </el-table-column>

        </el-table>
        <div v-show="!listLoading" class="pagination-container">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.page" :page-sizes="[10,20,30, 50]" :page-size="listQuery.size" layout="total, sizes, prev, pager, next, jumper" :total="total">
            </el-pagination>
          </div>
      </el-col>
    </el-row>
    <el-dialog title="选择模块" :visible.sync="dialogModuleVisible">
      <el-input placeholder="输入关键字进行过滤"
                v-model="filterFormText">
      </el-input>
      <el-tree class="filter-tree" ref="formTree" :data="treeModuleData"
               check-strictly node-key="id" highlight-current @node-click="getNodeFormData"
               :filter-node-method="filterNode" default-expand-all>
      </el-tree>
    </el-dialog>

    <el-dialog title="查看图标" :visible.sync="dialogIconVisible">
      <ul class="icon_lists clear">

        <li>
          <i class="icon iconfont icon-check-circle"></i>
          <div class="name">check-circle</div>
          <div class="fontclass">icon-check-circle</div>
        </li>

        <li>
          <i class="icon iconfont icon-CI"></i>
          <div class="name">CI</div>
          <div class="fontclass">icon-CI</div>
        </li>

        <li>
          <i class="icon iconfont icon-Dollar"></i>
          <div class="name">Dollar</div>
          <div class="fontclass">icon-Dollar</div>
        </li>

        <li>
          <i class="icon iconfont icon-compass"></i>
          <div class="name">compass</div>
          <div class="fontclass">icon-compass</div>
        </li>

        <li>
          <i class="icon iconfont icon-close-circle"></i>
          <div class="name">close-circle</div>
          <div class="fontclass">icon-close-circle</div>
        </li>

        <li>
          <i class="icon iconfont icon-frown"></i>
          <div class="name">frown</div>
          <div class="fontclass">icon-frown</div>
        </li>

        <li>
          <i class="icon iconfont icon-info-circle"></i>
          <div class="name">info-circle</div>
          <div class="fontclass">icon-info-circle</div>
        </li>

        <li>
          <i class="icon iconfont icon-left-circle"></i>
          <div class="name">left-circle</div>
          <div class="fontclass">icon-left-circle</div>
        </li>

        <li>
          <i class="icon iconfont icon-down-circle"></i>
          <div class="name">down-circle</div>
          <div class="fontclass">icon-down-circle</div>
        </li>

        <li>
          <i class="icon iconfont icon-EURO"></i>
          <div class="name">EURO</div>
          <div class="fontclass">icon-EURO</div>
        </li>

        <li>
          <i class="icon iconfont icon-copyright"></i>
          <div class="name">copyright</div>
          <div class="fontclass">icon-copyright</div>
        </li>

        <li>
          <i class="icon iconfont icon-minus-circle"></i>
          <div class="name">minus-circle</div>
          <div class="fontclass">icon-minus-circle</div>
        </li>

        <li>
          <i class="icon iconfont icon-meh"></i>
          <div class="name">meh</div>
          <div class="fontclass">icon-meh</div>
        </li>

        <li>
          <i class="icon iconfont icon-plus-circle"></i>
          <div class="name">plus-circle</div>
          <div class="fontclass">icon-plus-circle</div>
        </li>

        <li>
          <i class="icon iconfont icon-play-circle"></i>
          <div class="name">play-circle</div>
          <div class="fontclass">icon-play-circle</div>
        </li>

        <li>
          <i class="icon iconfont icon-question-circle"></i>
          <div class="name">question-circle</div>
          <div class="fontclass">icon-question-circle</div>
        </li>

        <li>
          <i class="icon iconfont icon-Pound"></i>
          <div class="name">Pound</div>
          <div class="fontclass">icon-Pound</div>
        </li>

        <li>
          <i class="icon iconfont icon-right-circle"></i>
          <div class="name">right-circle</div>
          <div class="fontclass">icon-right-circle</div>
        </li>

        <li>
          <i class="icon iconfont icon-smile"></i>
          <div class="name">smile</div>
          <div class="fontclass">icon-smile</div>
        </li>

        <li>
          <i class="icon iconfont icon-trademark"></i>
          <div class="name">trademark</div>
          <div class="fontclass">icon-trademark</div>
        </li>

        <li>
          <i class="icon iconfont icon-time-circle"></i>
          <div class="name">time-circle</div>
          <div class="fontclass">icon-time-circle</div>
        </li>

        <li>
          <i class="icon iconfont icon-timeout"></i>
          <div class="name">time out</div>
          <div class="fontclass">icon-timeout</div>
        </li>

        <li>
          <i class="icon iconfont icon-earth"></i>
          <div class="name">earth</div>
          <div class="fontclass">icon-earth</div>
        </li>

        <li>
          <i class="icon iconfont icon-YUAN"></i>
          <div class="name">YUAN</div>
          <div class="fontclass">icon-YUAN</div>
        </li>

        <li>
          <i class="icon iconfont icon-up-circle"></i>
          <div class="name">up-circle</div>
          <div class="fontclass">icon-up-circle</div>
        </li>

        <li>
          <i class="icon iconfont icon-warning-circle"></i>
          <div class="name">warning-circle</div>
          <div class="fontclass">icon-warning-circle</div>
        </li>

        <li>
          <i class="icon iconfont icon-sync"></i>
          <div class="name">sync</div>
          <div class="fontclass">icon-sync</div>
        </li>

        <li>
          <i class="icon iconfont icon-transaction"></i>
          <div class="name">transaction</div>
          <div class="fontclass">icon-transaction</div>
        </li>

        <li>
          <i class="icon iconfont icon-undo"></i>
          <div class="name">undo</div>
          <div class="fontclass">icon-undo</div>
        </li>

        <li>
          <i class="icon iconfont icon-redo"></i>
          <div class="name">redo</div>
          <div class="fontclass">icon-redo</div>
        </li>

        <li>
          <i class="icon iconfont icon-reload"></i>
          <div class="name">reload</div>
          <div class="fontclass">icon-reload</div>
        </li>

        <li>
          <i class="icon iconfont icon-reloadtime"></i>
          <div class="name">reload time</div>
          <div class="fontclass">icon-reloadtime</div>
        </li>

        <li>
          <i class="icon iconfont icon-message"></i>
          <div class="name">message</div>
          <div class="fontclass">icon-message</div>
        </li>

        <li>
          <i class="icon iconfont icon-dashboard"></i>
          <div class="name">dashboard</div>
          <div class="fontclass">icon-dashboard</div>
        </li>

        <li>
          <i class="icon iconfont icon-issuesclose"></i>
          <div class="name">issues close</div>
          <div class="fontclass">icon-issuesclose</div>
        </li>

        <li>
          <i class="icon iconfont icon-poweroff"></i>
          <div class="name">poweroff</div>
          <div class="fontclass">icon-poweroff</div>
        </li>

        <li>
          <i class="icon iconfont icon-logout"></i>
          <div class="name">logout</div>
          <div class="fontclass">icon-logout</div>
        </li>

        <li>
          <i class="icon iconfont icon-login"></i>
          <div class="name">login</div>
          <div class="fontclass">icon-login</div>
        </li>

        <li>
          <i class="icon iconfont icon-piechart"></i>
          <div class="name">pie chart</div>
          <div class="fontclass">icon-piechart</div>
        </li>

        <li>
          <i class="icon iconfont icon-setting"></i>
          <div class="name">setting</div>
          <div class="fontclass">icon-setting</div>
        </li>

        <li>
          <i class="icon iconfont icon-eye"></i>
          <div class="name">eye</div>
          <div class="fontclass">icon-eye</div>
        </li>

        <li>
          <i class="icon iconfont icon-location"></i>
          <div class="name">location</div>
          <div class="fontclass">icon-location</div>
        </li>

        <li>
          <i class="icon iconfont icon-edit-square"></i>
          <div class="name">edit-square</div>
          <div class="fontclass">icon-edit-square</div>
        </li>

        <li>
          <i class="icon iconfont icon-export"></i>
          <div class="name">export</div>
          <div class="fontclass">icon-export</div>
        </li>

        <li>
          <i class="icon iconfont icon-save"></i>
          <div class="name">save</div>
          <div class="fontclass">icon-save</div>
        </li>

        <li>
          <i class="icon iconfont icon-Import"></i>
          <div class="name">Import</div>
          <div class="fontclass">icon-Import</div>
        </li>

        <li>
          <i class="icon iconfont icon-appstore"></i>
          <div class="name">app store</div>
          <div class="fontclass">icon-appstore</div>
        </li>

        <li>
          <i class="icon iconfont icon-close-square"></i>
          <div class="name">close-square</div>
          <div class="fontclass">icon-close-square</div>
        </li>

        <li>
          <i class="icon iconfont icon-down-square"></i>
          <div class="name">down-square</div>
          <div class="fontclass">icon-down-square</div>
        </li>

        <li>
          <i class="icon iconfont icon-layout"></i>
          <div class="name">layout</div>
          <div class="fontclass">icon-layout</div>
        </li>

        <li>
          <i class="icon iconfont icon-left-square"></i>
          <div class="name">left-square</div>
          <div class="fontclass">icon-left-square</div>
        </li>

        <li>
          <i class="icon iconfont icon-play-square"></i>
          <div class="name">play-square</div>
          <div class="fontclass">icon-play-square</div>
        </li>

        <li>
          <i class="icon iconfont icon-control"></i>
          <div class="name">control</div>
          <div class="fontclass">icon-control</div>
        </li>

        <li>
          <i class="icon iconfont icon-codelibrary"></i>
          <div class="name">code library</div>
          <div class="fontclass">icon-codelibrary</div>
        </li>

        <li>
          <i class="icon iconfont icon-detail"></i>
          <div class="name">detail</div>
          <div class="fontclass">icon-detail</div>
        </li>

        <li>
          <i class="icon iconfont icon-minus-square"></i>
          <div class="name">minus-square</div>
          <div class="fontclass">icon-minus-square</div>
        </li>

        <li>
          <i class="icon iconfont icon-plus-square"></i>
          <div class="name">plus-square</div>
          <div class="fontclass">icon-plus-square</div>
        </li>

        <li>
          <i class="icon iconfont icon-right-square"></i>
          <div class="name">right-square</div>
          <div class="fontclass">icon-right-square</div>
        </li>

        <li>
          <i class="icon iconfont icon-project"></i>
          <div class="name">project</div>
          <div class="fontclass">icon-project</div>
        </li>

        <li>
          <i class="icon iconfont icon-wallet"></i>
          <div class="name">wallet</div>
          <div class="fontclass">icon-wallet</div>
        </li>

        <li>
          <i class="icon iconfont icon-up-square"></i>
          <div class="name">up-square</div>
          <div class="fontclass">icon-up-square</div>
        </li>

        <li>
          <i class="icon iconfont icon-calculator"></i>
          <div class="name">calculator</div>
          <div class="fontclass">icon-calculator</div>
        </li>

        <li>
          <i class="icon iconfont icon-interation"></i>
          <div class="name">interation</div>
          <div class="fontclass">icon-interation</div>
        </li>

        <li>
          <i class="icon iconfont icon-check-square"></i>
          <div class="name">check-square</div>
          <div class="fontclass">icon-check-square</div>
        </li>

        <li>
          <i class="icon iconfont icon-border"></i>
          <div class="name">border</div>
          <div class="fontclass">icon-border</div>
        </li>

        <li>
          <i class="icon iconfont icon-border-outer"></i>
          <div class="name">border-outer</div>
          <div class="fontclass">icon-border-outer</div>
        </li>

        <li>
          <i class="icon iconfont icon-border-top"></i>
          <div class="name">border-top</div>
          <div class="fontclass">icon-border-top</div>
        </li>

        <li>
          <i class="icon iconfont icon-border-bottom"></i>
          <div class="name">border-bottom</div>
          <div class="fontclass">icon-border-bottom</div>
        </li>

        <li>
          <i class="icon iconfont icon-border-left"></i>
          <div class="name">border-left</div>
          <div class="fontclass">icon-border-left</div>
        </li>

        <li>
          <i class="icon iconfont icon-border-right"></i>
          <div class="name">border-right</div>
          <div class="fontclass">icon-border-right</div>
        </li>

        <li>
          <i class="icon iconfont icon-border-inner"></i>
          <div class="name">border-inner</div>
          <div class="fontclass">icon-border-inner</div>
        </li>

        <li>
          <i class="icon iconfont icon-border-verticle"></i>
          <div class="name">border-verticle</div>
          <div class="fontclass">icon-border-verticle</div>
        </li>

        <li>
          <i class="icon iconfont icon-border-horizontal"></i>
          <div class="name">border-horizontal</div>
          <div class="fontclass">icon-border-horizontal</div>
        </li>

        <li>
          <i class="icon iconfont icon-radius-bottomleft"></i>
          <div class="name">radius-bottomleft</div>
          <div class="fontclass">icon-radius-bottomleft</div>
        </li>

        <li>
          <i class="icon iconfont icon-radius-bottomright"></i>
          <div class="name">radius-bottomright</div>
          <div class="fontclass">icon-radius-bottomright</div>
        </li>

        <li>
          <i class="icon iconfont icon-radius-upleft"></i>
          <div class="name">radius-upleft</div>
          <div class="fontclass">icon-radius-upleft</div>
        </li>

        <li>
          <i class="icon iconfont icon-radius-upright"></i>
          <div class="name">radius-upright</div>
          <div class="fontclass">icon-radius-upright</div>
        </li>

        <li>
          <i class="icon iconfont icon-radius-setting"></i>
          <div class="name">radius-setting</div>
          <div class="fontclass">icon-radius-setting</div>
        </li>

        <li>
          <i class="icon iconfont icon-adduser"></i>
          <div class="name">add user</div>
          <div class="fontclass">icon-adduser</div>
        </li>

        <li>
          <i class="icon iconfont icon-deleteteam"></i>
          <div class="name">delete team</div>
          <div class="fontclass">icon-deleteteam</div>
        </li>

        <li>
          <i class="icon iconfont icon-deleteuser"></i>
          <div class="name">delete user</div>
          <div class="fontclass">icon-deleteuser</div>
        </li>

        <li>
          <i class="icon iconfont icon-star-fill"></i>
          <div class="name">star-fill</div>
          <div class="fontclass">icon-star-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-unlock-fill"></i>
          <div class="name">unlock-fill</div>
          <div class="fontclass">icon-unlock-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-alert-fill"></i>
          <div class="name">alert-fill</div>
          <div class="fontclass">icon-alert-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-api-fill"></i>
          <div class="name">api-fill</div>
          <div class="fontclass">icon-api-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-highlight-fill"></i>
          <div class="name">highlight-fill</div>
          <div class="fontclass">icon-highlight-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-phone-fill"></i>
          <div class="name">phone-fill</div>
          <div class="fontclass">icon-phone-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-edit-fill"></i>
          <div class="name">edit-fill</div>
          <div class="fontclass">icon-edit-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-pushpin-fill"></i>
          <div class="name">pushpin-fill</div>
          <div class="fontclass">icon-pushpin-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-rocket-fill"></i>
          <div class="name">rocket-fill</div>
          <div class="fontclass">icon-rocket-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-thunderbolt-fill"></i>
          <div class="name">thunderbolt-fill</div>
          <div class="fontclass">icon-thunderbolt-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-tag-fill"></i>
          <div class="name">tag-fill</div>
          <div class="fontclass">icon-tag-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-wrench-fill"></i>
          <div class="name">wrench-fill</div>
          <div class="fontclass">icon-wrench-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-tags-fill"></i>
          <div class="name">tags-fill</div>
          <div class="fontclass">icon-tags-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-bank-fill"></i>
          <div class="name">bank-fill</div>
          <div class="fontclass">icon-bank-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-camera-fill"></i>
          <div class="name">camera-fill</div>
          <div class="fontclass">icon-camera-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-error-fill"></i>
          <div class="name">error-fill</div>
          <div class="fontclass">icon-error-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-crown-fill"></i>
          <div class="name">crown-fill</div>
          <div class="fontclass">icon-crown-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-mail-fill"></i>
          <div class="name">mail-fill</div>
          <div class="fontclass">icon-mail-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-car-fill"></i>
          <div class="name">car-fill</div>
          <div class="fontclass">icon-car-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-printer-fill"></i>
          <div class="name">printer-fill</div>
          <div class="fontclass">icon-printer-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-shop-fill"></i>
          <div class="name">shop-fill</div>
          <div class="fontclass">icon-shop-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-setting-fill"></i>
          <div class="name">setting-fill</div>
          <div class="fontclass">icon-setting-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-USB-fill"></i>
          <div class="name">USB-fill</div>
          <div class="fontclass">icon-USB-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-golden-fill"></i>
          <div class="name">golden-fill</div>
          <div class="fontclass">icon-golden-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-build-fill"></i>
          <div class="name">build-fill</div>
          <div class="fontclass">icon-build-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-boxplot-fill"></i>
          <div class="name">box plot-fill</div>
          <div class="fontclass">icon-boxplot-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-sliders-fill"></i>
          <div class="name">sliders-fill</div>
          <div class="fontclass">icon-sliders-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-alibaba"></i>
          <div class="name">alibaba</div>
          <div class="fontclass">icon-alibaba</div>
        </li>

        <li>
          <i class="icon iconfont icon-alibabacloud"></i>
          <div class="name">alibabacloud</div>
          <div class="fontclass">icon-alibabacloud</div>
        </li>

        <li>
          <i class="icon iconfont icon-antdesign"></i>
          <div class="name">ant design</div>
          <div class="fontclass">icon-antdesign</div>
        </li>

        <li>
          <i class="icon iconfont icon-ant-cloud"></i>
          <div class="name">ant-cloud</div>
          <div class="fontclass">icon-ant-cloud</div>
        </li>

        <li>
          <i class="icon iconfont icon-behance"></i>
          <div class="name">behance</div>
          <div class="fontclass">icon-behance</div>
        </li>

        <li>
          <i class="icon iconfont icon-googleplus"></i>
          <div class="name">google plus</div>
          <div class="fontclass">icon-googleplus</div>
        </li>

        <li>
          <i class="icon iconfont icon-medium"></i>
          <div class="name">medium</div>
          <div class="fontclass">icon-medium</div>
        </li>

        <li>
          <i class="icon iconfont icon-google"></i>
          <div class="name">google</div>
          <div class="fontclass">icon-google</div>
        </li>

        <li>
          <i class="icon iconfont icon-IE"></i>
          <div class="name">IE</div>
          <div class="fontclass">icon-IE</div>
        </li>

        <li>
          <i class="icon iconfont icon-amazon"></i>
          <div class="name">amazon</div>
          <div class="fontclass">icon-amazon</div>
        </li>

        <li>
          <i class="icon iconfont icon-slack"></i>
          <div class="name">slack</div>
          <div class="fontclass">icon-slack</div>
        </li>

        <li>
          <i class="icon iconfont icon-alipay"></i>
          <div class="name">alipay</div>
          <div class="fontclass">icon-alipay</div>
        </li>

        <li>
          <i class="icon iconfont icon-taobao"></i>
          <div class="name">taobao</div>
          <div class="fontclass">icon-taobao</div>
        </li>

        <li>
          <i class="icon iconfont icon-zhihu"></i>
          <div class="name">zhihu</div>
          <div class="fontclass">icon-zhihu</div>
        </li>

        <li>
          <i class="icon iconfont icon-HTML"></i>
          <div class="name">HTML5</div>
          <div class="fontclass">icon-HTML</div>
        </li>

        <li>
          <i class="icon iconfont icon-linkedin"></i>
          <div class="name">linkedin</div>
          <div class="fontclass">icon-linkedin</div>
        </li>

        <li>
          <i class="icon iconfont icon-yahoo"></i>
          <div class="name">yahoo</div>
          <div class="fontclass">icon-yahoo</div>
        </li>

        <li>
          <i class="icon iconfont icon-facebook"></i>
          <div class="name">facebook</div>
          <div class="fontclass">icon-facebook</div>
        </li>

        <li>
          <i class="icon iconfont icon-skype"></i>
          <div class="name">skype</div>
          <div class="fontclass">icon-skype</div>
        </li>

        <li>
          <i class="icon iconfont icon-CodeSandbox"></i>
          <div class="name">CodeSandbox</div>
          <div class="fontclass">icon-CodeSandbox</div>
        </li>

        <li>
          <i class="icon iconfont icon-chrome"></i>
          <div class="name">chrome</div>
          <div class="fontclass">icon-chrome</div>
        </li>

        <li>
          <i class="icon iconfont icon-codepen"></i>
          <div class="name">codepen</div>
          <div class="fontclass">icon-codepen</div>
        </li>

        <li>
          <i class="icon iconfont icon-aliwangwang"></i>
          <div class="name">aliwangwang</div>
          <div class="fontclass">icon-aliwangwang</div>
        </li>

        <li>
          <i class="icon iconfont icon-apple"></i>
          <div class="name">apple</div>
          <div class="fontclass">icon-apple</div>
        </li>

        <li>
          <i class="icon iconfont icon-android"></i>
          <div class="name">android</div>
          <div class="fontclass">icon-android</div>
        </li>

        <li>
          <i class="icon iconfont icon-sketch"></i>
          <div class="name">sketch</div>
          <div class="fontclass">icon-sketch</div>
        </li>

        <li>
          <i class="icon iconfont icon-Gitlab"></i>
          <div class="name">Gitlab</div>
          <div class="fontclass">icon-Gitlab</div>
        </li>

        <li>
          <i class="icon iconfont icon-dribbble"></i>
          <div class="name">dribbble</div>
          <div class="fontclass">icon-dribbble</div>
        </li>

        <li>
          <i class="icon iconfont icon-instagram"></i>
          <div class="name">instagram</div>
          <div class="fontclass">icon-instagram</div>
        </li>

        <li>
          <i class="icon iconfont icon-reddit"></i>
          <div class="name">reddit</div>
          <div class="fontclass">icon-reddit</div>
        </li>

        <li>
          <i class="icon iconfont icon-windows"></i>
          <div class="name">windows</div>
          <div class="fontclass">icon-windows</div>
        </li>

        <li>
          <i class="icon iconfont icon-yuque"></i>
          <div class="name">yuque</div>
          <div class="fontclass">icon-yuque</div>
        </li>

        <li>
          <i class="icon iconfont icon-Youtube"></i>
          <div class="name">Youtube</div>
          <div class="fontclass">icon-Youtube</div>
        </li>

        <li>
          <i class="icon iconfont icon-Gitlab-fill"></i>
          <div class="name">Gitlab-fill</div>
          <div class="fontclass">icon-Gitlab-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-dropbox"></i>
          <div class="name">dropbox</div>
          <div class="fontclass">icon-dropbox</div>
        </li>

        <li>
          <i class="icon iconfont icon-dingtalk"></i>
          <div class="name">dingtalk</div>
          <div class="fontclass">icon-dingtalk</div>
        </li>

        <li>
          <i class="icon iconfont icon-android-fill"></i>
          <div class="name">android-fill</div>
          <div class="fontclass">icon-android-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-apple-fill"></i>
          <div class="name">apple-fill</div>
          <div class="fontclass">icon-apple-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-HTML-fill"></i>
          <div class="name">HTML5-fill</div>
          <div class="fontclass">icon-HTML-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-windows-fill"></i>
          <div class="name">windows-fill</div>
          <div class="fontclass">icon-windows-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-QQ"></i>
          <div class="name">QQ</div>
          <div class="fontclass">icon-QQ</div>
        </li>

        <li>
          <i class="icon iconfont icon-twitter"></i>
          <div class="name">twitter</div>
          <div class="fontclass">icon-twitter</div>
        </li>

        <li>
          <i class="icon iconfont icon-skype-fill"></i>
          <div class="name">skype-fill</div>
          <div class="fontclass">icon-skype-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-weibo"></i>
          <div class="name">weibo</div>
          <div class="fontclass">icon-weibo</div>
        </li>

        <li>
          <i class="icon iconfont icon-yuque-fill"></i>
          <div class="name">yuque-fill</div>
          <div class="fontclass">icon-yuque-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-Youtube-fill"></i>
          <div class="name">Youtube-fill</div>
          <div class="fontclass">icon-Youtube-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-yahoo-fill"></i>
          <div class="name">yahoo-fill</div>
          <div class="fontclass">icon-yahoo-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-wechat-fill"></i>
          <div class="name">wechat-fill</div>
          <div class="fontclass">icon-wechat-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-chrome-fill"></i>
          <div class="name">chrome-fill</div>
          <div class="fontclass">icon-chrome-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-alipay-circle-fill"></i>
          <div class="name">alipay-circle-fill</div>
          <div class="fontclass">icon-alipay-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-aliwangwang-fill"></i>
          <div class="name">aliwangwang-fill</div>
          <div class="fontclass">icon-aliwangwang-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-behance-circle-fill"></i>
          <div class="name">behance-circle-fill</div>
          <div class="fontclass">icon-behance-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-amazon-circle-fill"></i>
          <div class="name">amazon-circle-fill</div>
          <div class="fontclass">icon-amazon-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-codepen-circle-fill"></i>
          <div class="name">codepen-circle-fill</div>
          <div class="fontclass">icon-codepen-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-CodeSandbox-circle-f"></i>
          <div class="name">CodeSandbox-circle-f</div>
          <div class="fontclass">icon-CodeSandbox-circle-f</div>
        </li>

        <li>
          <i class="icon iconfont icon-dropbox-circle-fill"></i>
          <div class="name">dropbox-circle-fill</div>
          <div class="fontclass">icon-dropbox-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-github-fill"></i>
          <div class="name">github-fill</div>
          <div class="fontclass">icon-github-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-dribbble-circle-fill"></i>
          <div class="name">dribbble-circle-fill</div>
          <div class="fontclass">icon-dribbble-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-googleplus-circle-f"></i>
          <div class="name">google plus-circle-f</div>
          <div class="fontclass">icon-googleplus-circle-f</div>
        </li>

        <li>
          <i class="icon iconfont icon-medium-circle-fill"></i>
          <div class="name">medium-circle-fill</div>
          <div class="fontclass">icon-medium-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-QQ-circle-fill"></i>
          <div class="name">QQ-circle-fill</div>
          <div class="fontclass">icon-QQ-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-IE-circle-fill"></i>
          <div class="name">IE-circle-fill</div>
          <div class="fontclass">icon-IE-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-google-circle-fill"></i>
          <div class="name">google-circle-fill</div>
          <div class="fontclass">icon-google-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-dingtalk-circle-fill"></i>
          <div class="name">dingtalk-circle-fill</div>
          <div class="fontclass">icon-dingtalk-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-sketch-circle-fill"></i>
          <div class="name">sketch-circle-fill</div>
          <div class="fontclass">icon-sketch-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-slack-circle-fill"></i>
          <div class="name">slack-circle-fill</div>
          <div class="fontclass">icon-slack-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-twitter-circle-fill"></i>
          <div class="name">twitter-circle-fill</div>
          <div class="fontclass">icon-twitter-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-taobao-circle-fill"></i>
          <div class="name">taobao-circle-fill</div>
          <div class="fontclass">icon-taobao-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-weibo-circle-fill"></i>
          <div class="name">weibo-circle-fill</div>
          <div class="fontclass">icon-weibo-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-zhihu-circle-fill"></i>
          <div class="name">zhihu-circle-fill</div>
          <div class="fontclass">icon-zhihu-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-reddit-circle-fill"></i>
          <div class="name">reddit-circle-fill</div>
          <div class="fontclass">icon-reddit-circle-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-alipay-square-fill"></i>
          <div class="name">alipay-square-fill</div>
          <div class="fontclass">icon-alipay-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-dingtalk-square-fill"></i>
          <div class="name">dingtalk-square-fill</div>
          <div class="fontclass">icon-dingtalk-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-CodeSandbox-square-f"></i>
          <div class="name">CodeSandbox-square-f</div>
          <div class="fontclass">icon-CodeSandbox-square-f</div>
        </li>

        <li>
          <i class="icon iconfont icon-behance-square-fill"></i>
          <div class="name">behance-square-fill</div>
          <div class="fontclass">icon-behance-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-amazon-square-fill"></i>
          <div class="name">amazon-square-fill</div>
          <div class="fontclass">icon-amazon-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-codepen-square-fill"></i>
          <div class="name">codepen-square-fill</div>
          <div class="fontclass">icon-codepen-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-dribbble-square-fill"></i>
          <div class="name">dribbble-square-fill</div>
          <div class="fontclass">icon-dribbble-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-dropbox-square-fill"></i>
          <div class="name">dropbox-square-fill</div>
          <div class="fontclass">icon-dropbox-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-facebook-fill"></i>
          <div class="name">facebook-fill</div>
          <div class="fontclass">icon-facebook-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-googleplus-square-f"></i>
          <div class="name">google plus-square-f</div>
          <div class="fontclass">icon-googleplus-square-f</div>
        </li>

        <li>
          <i class="icon iconfont icon-google-square-fill"></i>
          <div class="name">google-square-fill</div>
          <div class="fontclass">icon-google-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-instagram-fill"></i>
          <div class="name">instagram-fill</div>
          <div class="fontclass">icon-instagram-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-IE-square-fill"></i>
          <div class="name">IE-square-fill</div>
          <div class="fontclass">icon-IE-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-medium-square-fill"></i>
          <div class="name">medium-square-fill</div>
          <div class="fontclass">icon-medium-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-linkedin-fill"></i>
          <div class="name">linkedin-fill</div>
          <div class="fontclass">icon-linkedin-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-QQ-square-fill"></i>
          <div class="name">QQ-square-fill</div>
          <div class="fontclass">icon-QQ-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-reddit-square-fill"></i>
          <div class="name">reddit-square-fill</div>
          <div class="fontclass">icon-reddit-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-twitter-square-fill"></i>
          <div class="name">twitter-square-fill</div>
          <div class="fontclass">icon-twitter-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-sketch-square-fill"></i>
          <div class="name">sketch-square-fill</div>
          <div class="fontclass">icon-sketch-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-slack-square-fill"></i>
          <div class="name">slack-square-fill</div>
          <div class="fontclass">icon-slack-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-taobao-square-fill"></i>
          <div class="name">taobao-square-fill</div>
          <div class="fontclass">icon-taobao-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-weibo-square-fill"></i>
          <div class="name">weibo-square-fill</div>
          <div class="fontclass">icon-weibo-square-fill</div>
        </li>

        <li>
          <i class="icon iconfont icon-zhihu-square-fill"></i>
          <div class="name">zhihu-square-fill</div>
          <div class="fontclass">icon-zhihu-square-fill</div>
        </li>

      </ul>
    </el-dialog>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :label-position="labelPosition" label-width="80px" :model="form" ref="form">
        <el-form-item label="上级模块" prop="parentName">
          <el-input v-model="form.parentName" placeholder="选择模块" @focus="handleModule()" readonly></el-input>
          <input type="hidden" v-model="form.parentId" />
        </el-form-item>
        <el-form-item label="名称" prop="name" :rules="[{required: true,message: '请输入名称'}]">
          <el-input v-model="form.name" ></el-input>
        </el-form-item>
        <el-form-item label="服务名称" prop="microservice">
          <el-input v-model="form.microservice" ></el-input>
        </el-form-item>
        <el-form-item label="图标" prop="iconCls">
          <el-input v-model="form.iconCls"></el-input>
          <el-button type="text" @click="handleIcon()">查看图标</el-button>
        </el-form-item>
        <el-form-item label="类型" prop="type" :rules="[{required: true,message: '请选择类型'}]">
          <AvueCrudRadio v-model="form.type" :dic="typeOptions" :rules="[{required: true,message: '请选择类型'}]"></AvueCrudRadio>
        </el-form-item>
        <el-form-item label="权限" prop="permission" :rules="[{validator: validateUnique}]">
          <el-input v-model="form.permission" ></el-input>
        </el-form-item>
        <el-form-item label="请求方法" prop="requestMethod">
          <AvueCrudCheckbox v-model="form.requestMethod" :dic="methodOptions"></AvueCrudCheckbox>
        </el-form-item>
        <el-form-item label="操作目标" prop="target">
          <el-input v-model="form.target" ></el-input>
        </el-form-item>
        <el-form-item label="前端组件" prop="component">
          <el-input v-model="form.component" ></el-input>
        </el-form-item>
        <el-form-item label="链接" prop="url">
          <el-input v-model="form.url" ></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sort" :rules="[{type: 'number',message: '序号必须为数字'}]">
          <el-input-number v-model="form.sort" :step="5"></el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status" :rules="[{required: true,message: '请选择状态'}]">
          <AvueCrudRadio v-model="form.status" :dic="statusOptions"></AvueCrudRadio>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="form.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel()">取 消</el-button>
        <el-button type="primary" @click="save()">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {fetchModuleTree, findModule, saveModule, removeModule, pageModule} from "./moduleService";
  import { mapGetters } from 'vuex'
  import {parseJsonItemForm, parseTreeData} from "../../../util/util";
  import {dictCodes} from "../../../api/dataSystem";
  import {isValidateUnique, objectToString, toStr, validateNotNull} from "../../../util/validate";
  import {MSG_TYPE_SUCCESS} from "../../../const/common";
  export default {
    name: 'module',
    data() {
      return {
        treeModuleData: [],
        dialogModuleVisible: false,
        dialogIconVisible: false,
        dialogFormVisible: false,
        list: null,
        total: null,
        listLoading: true,
        listQuery: {
          page: 1,
          size: 20
        },
        formEdit: true,
        filterText: '',
        filterFormText: '',
        formStatus: '',
        statusOptions: [],
        typeOptions: [],
        methodOptions: [],
        searchTree: false,
        treeData: [],
        labelPosition: 'right',
        form: {
          name: undefined,
          parentId: undefined,
          parentName: undefined,
          microservice: undefined,
          type: undefined,
          iconCls: undefined,
          permission: undefined,
          requestMethod: undefined,
          target: undefined,
          url: undefined,
          sort: undefined,
          status: undefined,
          description: undefined
        },
        validateUnique: (rule, value, callback) => {
          isValidateUnique(rule, value, callback, '/sys/module/checkByProperty?id='+toStr(this.form.id))
        },
        dialogStatus: 'create',
        textMap: {
          update: '编辑',
          create: '创建'
        },
        sys_module_edit: false,
        sys_module_delete: false,
        currentNode: {}
      }
    },
    watch: {
      filterText(val) {
        this.$refs['leftTree'].filter(val);
      },
      filterFormText(val) {
        this.$refs['formTree'].filter(val);
      }
    },
    created() {
      this.getTree()
      this.getList()
      this.sys_module_edit = this.authorities.indexOf("sys_module_edit") !== -1;
      this.sys_module_delete = this.authorities.indexOf("sys_module_delete") !== -1;

      dictCodes({codes:'sys_status,sys_module_type,sys_request_method'}).then(rs => {
        this.statusOptions = rs.data[0];
        this.typeOptions = rs.data[1];
        this.methodOptions = rs.data[2];
      });
    },
    computed: {
      ...mapGetters([
        'authorities'
      ])
    },
    methods: {
      getList() {
        this.listLoading = true;
        this.listQuery.isAsc = false;
        this.listQuery.queryConditionJson = parseJsonItemForm([{
          fieldName: 'name',value:this.listQuery.name
        },{
          fieldName: 'parentId',value:this.listQuery.parentId
        }])
        pageModule(this.listQuery).then(response => {
          this.list = response.data;
          this.total = response.total;
          this.listLoading = false;
        });
      },
      getTree() {
        fetchModuleTree({all:true}).then(response => {
          this.treeData = parseTreeData(response.data);
        })
      },
      filterNode(value, data) {
        if (!value) return true
        return data.label.indexOf(value) !== -1
      },
      getNodeData(data) {
        this.listQuery.parentId = data.id
        this.currentNode = data;
        this.getList()
      },
      getNodeFormData(data){
        this.dialogModuleVisible = false;
        this.form.parentId = data.id;
        this.form.parentName = data.label;
      },

      handleFilter() {
        this.listQuery.page = 1;
        this.getList();
      },
      handleSizeChange(val) {
        this.listQuery.size = val;
        this.getList();
      },
      handleCurrentChange(val) {
        this.listQuery.page = val;
        this.getList();
      },
      handleEdit(row) {
        this.resetForm();
        this.dialogStatus = row && validateNotNull(row.id)? "update" : "create";
        if(this.dialogStatus == "create"){
          this.dialogFormVisible = true;
          this.form.parentId = this.currentNode.id
          this.form.parentName = this.currentNode.label;
        }else{
          findModule(row.id).then(response => {
            this.form = response.data;
            this.form.status=objectToString(this.form.status)
            this.form.requestMethod = this.form.requestMethod.split(",")
            this.dialogFormVisible = true;
          });
        }
      },
      handleModule() {
        fetchModuleTree({extId: this.form.id}).then(response => {
          this.treeModuleData = parseTreeData(response.data);
          this.dialogModuleVisible = true;
        })
      },
      handleIcon(){
        this.dialogIconVisible = true;
      },
      handleDelete(row) {
        this.$confirm('此操作将永久删除, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          removeModule(row.id).then(() => {
            if (data.status == MSG_TYPE_SUCCESS) {
              this.getList();
            }
          })
        })
      },
      save() {
        const set = this.$refs;
        set['form'].validate(valid => {
          if (valid) {
            saveModule(this.form).then(() => {
              this.getList()
              this.dialogFormVisible = false;
            })
          } else {
            return false;
          }
        });
      },
      cancel() {
        this.dialogFormVisible = false;
        this.$refs['form'].resetFields();
      },
      resetForm() {
        this.form = {
          permission: undefined,
          name: undefined,
          menuId: undefined,
          parentId: undefined,
          url: undefined,
          icon: undefined,
          sort: undefined,
          component: undefined,
          type: undefined,
          method: undefined
        }
        if(this.$refs['form']){
          this.$refs['form'].resetFields();
        }
      }
    }
  }
</script>

