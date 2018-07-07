
<template>
  <div class="app-container calendar-list-container">
    <div class="avue-data-display">
      <h3>JVM 资源监控</h3>
      <el-row :span="24" :gutter="30">
        <el-col :span="8">
          <b >内存{{getVal(metrics.gauges['jvm.memory.total.used']) * 100 /
            getVal(metrics.gauges['jvm.memory.total.max']) | numFilter}}</b>
          <p><span>总内存</span> ({{getVal(metrics.gauges['jvm.memory.total.used']) / 1000000 }}M /
            {{getVal(metrics.gauges['jvm.memory.total.max']) / 1000000 }}M)</p>
          <el-progress status="success" :text-inside="true" :stroke-width="18"
                       :percentage="getVal(metrics.gauges['jvm.memory.total.used']) * 100 /
                        getVal(metrics.gauges['jvm.memory.total.max']) | numFilter">
          </el-progress>
          <p><span>堆内存</span> ({{getVal(metrics.gauges['jvm.memory.heap.used'])  / 1000000}}M / {{getVal(metrics.gauges['jvm.memory.heap.max'])  / 1000000}}M)</p>
          <el-progress status="success" :text-inside="true" :stroke-width="18"
                       :percentage="getVal(metrics.gauges['jvm.memory.heap.used'])  * 100 /
                       getVal(metrics.gauges['jvm.memory.heap.max']) | numFilter" >
            <span>%</span>
          </el-progress>
          <p><span>非堆内存</span> ({{getVal(metrics.gauges['jvm.memory.non-heap.used'])  / 1000000}}M / {{getVal(metrics.gauges['jvm.memory.non-heap.committed'])  / 1000000}}M)</p>
          <el-progress status="success" :text-inside="true" :stroke-width="18"
                        :percentage="getVal(metrics.gauges['jvm.memory.non-heap.used'])  * 100 /
                        getVal(metrics.gauges['jvm.memory.non-heap.committed']) | numFilter">
          </el-progress>
        </el-col>
        <el-col :span="8">
          <b >线程</b> (Total: {{getVal(metrics.gauges['jvm.threads.count']) }})
          <a class="hand">
            <i class="fa fa-eye"></i></a>
          <p><span>可运行</span> {{getVal(metrics.gauges['jvm.threads.runnable.count']) }}</p>
          <el-progress status="success" :text-inside="true" :stroke-width="18"
                       :percentage="getVal(metrics.gauges['jvm.threads.runnable.count'])  * 100 /
                       getVal(metrics.gauges['jvm.threads.count']) | numFilter">
          </el-progress>
          <p><span >定时等待</span> ({{getVal(metrics.gauges['jvm.threads.timed_waiting.count']) }})</p>
          <el-progress status="warning" :text-inside="true" :stroke-width="18"
                       :percentage="getVal(metrics.gauges['jvm.threads.timed_waiting.count'])  * 100 /
                       getVal(metrics.gauges['jvm.threads.count']) | numFilter">
          </el-progress>
          <p><span>等待中</span> ({{getVal(metrics.gauges['jvm.threads.waiting.count']) }})</p>
          <el-progress status="warning" :text-inside="true" :stroke-width="18"
                                  :percentage="getVal(metrics.gauges['jvm.threads.waiting.count'])  * 100 /
                                  getVal(metrics.gauges['jvm.threads.count']) | numFilter">
          </el-progress>
          <p><span>阻塞中</span> ({{getVal(metrics.gauges['jvm.threads.blocked.count']) }})</p>
          <el-progress status="success" :text-inside="true" :stroke-width="18"
                       :percentage="getVal(metrics.gauges['jvm.threads.blocked.count'])  * 100 /
                       getVal(metrics.gauges['jvm.threads.count']) | numFilter">
          </el-progress>
        </el-col>
        <el-col :span="8">
          <b>垃圾回收</b>
          <div class="row">
            <div class="col-md-9">标记清除次数</div>
            <div class="col-md-3 text-right">{{getVal(metrics.gauges['jvm.garbage.PS-MarkSweep.count']) }}</div>
          </div>
          <div class="row">
            <div class="col-md-9">标记清除耗时</div>
            <div class="col-md-3 text-right">{{getVal(metrics.gauges['jvm.garbage.PS-MarkSweep.time']) }}ms</div>
          </div>
          <div class="row">
            <div class="col-md-9">回收次数</div>
            <div class="col-md-3 text-right">{{getVal(metrics.gauges['jvm.garbage.PS-Scavenge.count']) }}</div>
          </div>
          <div class="row">
            <div class="col-md-9">回收耗时</div>
            <div class="col-md-3 text-right">{{getVal(metrics.gauges['jvm.garbage.PS-Scavenge.time']) }}ms</div>
          </div>
        </el-col>
      </el-row>
      <h3>HTTP 请求 (事件 / 秒)</h3>
      <p v-show="metrics.counters">
        <span>使用中请求:</span> <b>{{getValByKey(metrics.counters['com.codahale.metrics.servlet.InstrumentedFilter.activeRequests'], 'count')}}</b> -
        <span>请求总数</span> <b>{{getValByKey(metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'], 'count')}}</b>
      </p>
      <div class="table-responsive" v-show="!updatingMetrics">
        <table class="table m-table table-bordered table-hover">
          <thead>
          <tr>
            <th>状态码</th>
            <th>次数</th>
            <th class="text-right">平均数</th>
            <th class="text-right"><span>平均值</span> (1 min)</th>
            <th class="text-right"><span>平均值</span> (5 min)</th>
            <th class="text-right"><span>平均值</span> (15 min)</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>2xx (成功)</td>
            <td>
              <el-progress status="success" :text-inside="true" :stroke-width="18"
                           :percentage="getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'],'count') * 100 /
                        getValByKey(metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'],'count') | numFilter">
              </el-progress>
            </td>
            <td class="text-right">
              {{getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'],'mean_rate')}}
            </td>
            <td class="text-right">
              {{getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'],'m1_rate')}}
            </td>
            <td class="text-right">
              {{getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'],'m5_rate')}}
            </td>
            <td class="text-right">
              {{getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'],'m15_rate')}}
            </td>
          </tr>
          <tr>
            <td>4xx (请求错误)</td>
            <td>
              <el-progress status="success" :text-inside="true" :stroke-width="18"
                           :percentage="getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'],'count') * 100 /
                        getValByKey(metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'],'count') | numFilter">
              </el-progress>
            </td>
            <td class="text-right">
              {{getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'],'mean_rate')}}
            </td>
            <td class="text-right">
              {{getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'],'m1_rate')}}
            </td>
            <td class="text-right">
              {{getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'],'m5_rate')}}
            </td>
            <td class="text-right">
              {{getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'],'m15_rate')}}
            </td>
          </tr>
          <tr>
            <td>5xx (服务器错误)</td>
            <td>
              <el-progress status="success" :text-inside="true" :stroke-width="18"
                           :percentage="getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'],'count') * 100 /
                        getValByKey(metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'],'count') | numFilter">
              </el-progress>
            </td>
            <td class="text-right">
              {{getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'],'mean_rate')}}
            </td>
            <td class="text-right">
              {{getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'],'m1_rate')}}
            </td>
            <td class="text-right">
              {{getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'],'m5_rate')}}
            </td>
            <td class="text-right">
              {{getValByKey(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'],'m15_rate')}}
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <h3>服务统计 (时间单位为毫秒)</h3>
      <div class="table-responsive" v-show="!updatingMetrics">
        <table class="table m-table table-bordered table-hover">
          <thead>
          <tr>
            <th>服务名称</th>
            <th class="text-right">计数</th>
            <th class="text-right">平均值</th>
            <th class="text-right">最小值</th>
            <th class="text-right">p50</th>
            <th class="text-right">p75</th>
            <th class="text-right">p95</th>
            <th class="text-right">p99</th>
            <th class="text-right">最大值</th>
          </tr>
          </thead>
          <tbody>
          <tr  v-for="(entry,index) in servicesStats">
            <td>{{entry.key}}</td>
            <td class="text-right">{{getValByKey(entry.value,'count')}}</td>
            <td class="text-right">{{getValByKey(entry.value,'mean')}}</td>
            <td class="text-right">{{getValByKey(entry.value,'min')}}</td>
            <td class="text-right">{{getValByKey(entry.value,'p50')}}</td>
            <td class="text-right">{{getValByKey(entry.value,'p75')}}</td>
            <td class="text-right">{{getValByKey(entry.value,'p95')}}</td>
            <td class="text-right">{{getValByKey(entry.value,'p99')}}</td>
            <td class="text-right">{{getValByKey(entry.value,'max')}}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <h3>Cache 统计</h3>
      <div class="table-responsive" v-show="!updatingMetrics">
        <table class="table m-table table-bordered table-hover">
          <thead>
          <tr>
            <th>缓存名称</th>
            <th class="text-right">缓存命中</th>
            <th class="text-right">缓存未命中</th>
            <th class="text-right">缓存获取</th>
            <th class="text-right">缓存装入</th>
            <th class="text-right">缓存清除</th>
            <th class="text-right">缓存Evictions(驱逐)</th>
            <th class="text-right">缓存命中 %</th>
            <th class="text-right">缓存未命中 %</th>
            <th class="text-right">平均获取时间 (µs)</th>
            <th class="text-right">平均装入时间 (µs)</th>
            <th class="text-right">平均清除时间 (µs)</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(entry,index) in cachesStats">
            <td>{{entry.key}}</td>
            <td class="text-right">{{getVal(metrics.gauges[entry.key + '.cache-hits'])}}</td>
            <td class="text-right">{{getVal(metrics.gauges[entry.key + '.cache-misses'])}}</td>
            <td class="text-right">{{getVal(metrics.gauges[entry.key + '.cache-gets'])}}</td>
            <td class="text-right">{{getVal(metrics.gauges[entry.key + '.cache-puts'])}}</td>
            <td class="text-right">{{getVal(metrics.gauges[entry.key + '.cache-removals'])}}</td>
            <td class="text-right">{{getVal(metrics.gauges[entry.key + '.cache-evictions'])}}</td>
            <td class="text-right">{{getVal(metrics.gauges[entry.key + '.cache-hit-percentage'])}}</td>
            <td class="text-right">{{getVal(metrics.gauges[entry.key + '.cache-miss-percentage']) }}</td>
            <td class="text-right">{{getVal(metrics.gauges[entry.key + '.average-get-time'])}}</td>
            <td class="text-right">{{getVal(metrics.gauges[entry.key + '.average-put-time'])}}</td>
            <td class="text-right">{{getVal(metrics.gauges[entry.key + '.average-remove-time']) }}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <h3 v-show="metrics.gauges && metrics.gauges['HikariPool-1.pool.TotalConnections'] && getVal(metrics.gauges['HikariPool-1.pool.TotalConnections']) > 0">数据源统计 (时间单位为毫秒)</h3>
      <div class="table-responsive" v-show="!updatingMetrics && metrics.gauges && metrics.gauges['HikariPool-1.pool.TotalConnections'] && metrics.gauges['HikariPool-1.pool.TotalConnections'].value > 0">
        <table class="table m-table table-bordered table-hover">
          <thead>
          <tr>
            <th><span>用法</span> ({{getVal(metrics.gauges['HikariPool-1.pool.ActiveConnections'])}} /
              {{getVal(metrics.gauges['HikariPool-1.pool.TotalConnections'])}})</th>
            <th class="text-right">计数</th>
            <th class="text-right">平均值</th>
            <th class="text-right">最小值</th>
            <th class="text-right">p50</th>
            <th class="text-right">p75</th>
            <th class="text-right">p95</th>
            <th class="text-right">p99</th>
            <th class="text-right">最大值</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>
              <div class="progress progress-striped">
                <el-progress status="success" :text-inside="true" :stroke-width="18"
                             :percentage="getVal(metrics.gauges['HikariPool-1.pool.ActiveConnections']) * 100 / getVal(metrics.gauges['HikariPool-1.pool.TotalConnections']) | numFilter">
                </el-progress>
              </div>
            </td>
            <td class="text-right">{{getValByKey(metrics.histograms['HikariPool-1.pool.Usage'],'count')}}</td>
            <td class="text-right">{{getValByKey(metrics.histograms['HikariPool-1.pool.Usage'],'mean')}}</td>
            <td class="text-right">{{getValByKey(metrics.histograms['HikariPool-1.pool.Usage'],'min')}}</td>
            <td class="text-right">{{getValByKey(metrics.histograms['HikariPool-1.pool.Usage'],'p50')}}</td>
            <td class="text-right">{{getValByKey(metrics.histograms['HikariPool-1.pool.Usage'],'p75')}}</td>
            <td class="text-right">{{getValByKey(metrics.histograms['HikariPool-1.pool.Usage'],'p95')}}</td>
            <td class="text-right">{{getValByKey(metrics.histograms['HikariPool-1.pool.Usage'],'p99')}}</td>
            <td class="text-right">{{getValByKey(metrics.histograms['HikariPool-1.pool.Usage'],'max')}}</td>
          </tr>
          </tbody>
        </table>
      </div>

    </div>
  </div>
</template>

<script>
  import {getMetrics} from "./metricsService";

  export default {
    components: {
    },
    name: "admin_metrics",
    directives: {
    },
    data() {
      return{
        metrics:{gauges:{},meters:{}},
        cachesStats:{},
        updatingMetrics: true,
        servicesStats: {},
        JCACHE_KEY: 'net.sf.ehcache.Cache'
      };
    },
    computed: {
      // ...mapGetters(['authorities'])
    },
    filters: {
      numFilter(value) {
        if(isNaN(value))return 0
// 截取当前数据到小数点后两位
        let realVal = Number(value).toFixed(2)
// num.toFixed(2)获取的是字符串
        return Number(realVal)
      },
      filterNaN(input) {
        if (isNaN(input)) {
          return 0;
        }
        return input;
      }
    },
    created() {
      this.initPageData();

    },
    methods: {
      getVal(val){
        return val && val.value;
      },
      getValByKey(val, key){
        return val && val[key];
      },
      initPageData() {

        this.updatingMetrics = true;
        getMetrics().then(metrics => {
          this.metrics = metrics;
          this.servicesStats = {};
          this.cachesStats = {};
          Object.keys(metrics.timers).forEach((key) => {
            const value = metrics.timers[key];
            if (key.indexOf('web') !== -1 || key.indexOf('service') !== -1) {
              this.servicesStats[key] = value;
            }
          });
          Object.keys(metrics.gauges).forEach((key) => {
            if (key.indexOf('jcache.statistics') !== -1) {
              const value = metrics.gauges[key].value;
              // remove gets or puts
              const index = key.lastIndexOf('.');
              const newKey = key.substr(0, index);

              // Keep the name of the domain
              this.cachesStats[newKey] = {
                'name': this.JCACHE_KEY.length,
                'value': value
              };
            }
          });

          this.updatingMetrics = false;
        });
      }
    }
  };
</script>
