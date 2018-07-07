
<template>
  <div class="app-container calendar-list-container">
    <div class="avue-data-display">
      <el-row :span="24" :gutter="30">
        <el-col :span="8">
          <b >内存{{getValue(metrics.gauges['jvm.memory.total.used']) * 100 /
            getValue(metrics.gauges['jvm.memory.total.max']) | numFilter}}</b>
          <p><span>总内存</span> ({{getValue(metrics.gauges['jvm.memory.total.used']) / 1000000 }}M /
            {{getValue(metrics.gauges['jvm.memory.total.max']) / 1000000 }}M)</p>
          <el-progress status="success" :text-inside="true" :stroke-width="18"
                       :percentage="getValue(metrics.gauges['jvm.memory.total.used']) * 100 /
                        getValue(metrics.gauges['jvm.memory.total.max']) | numFilter">
          </el-progress>
          <p><span>堆内存</span> ({{getValue(metrics.gauges['jvm.memory.heap.used'])  / 1000000}}M / {{getValue(metrics.gauges['jvm.memory.heap.max'])  / 1000000}}M)</p>
          <el-progress status="success" :text-inside="true" :stroke-width="18"
                       :percentage="getValue(metrics.gauges['jvm.memory.heap.used'])  * 100 /
                       getValue(metrics.gauges['jvm.memory.heap.max']) | numFilter" >
            <span>%</span>
          </el-progress>
          <p><span>非堆内存</span> ({{getValue(metrics.gauges['jvm.memory.non-heap.used'])  / 1000000}}M / {{getValue(metrics.gauges['jvm.memory.non-heap.committed'])  / 1000000}}M)</p>
          <el-progress status="success" :text-inside="true" :stroke-width="18"
                        :percentage="getValue(metrics.gauges['jvm.memory.non-heap.used'])  * 100 /
                        getValue(metrics.gauges['jvm.memory.non-heap.committed']) | numFilter">
          </el-progress>
        </el-col>
        <el-col :span="8">
          <b >线程</b> (Total: {{getValue(metrics.gauges['jvm.threads.count']) }})
          <a class="hand">
            <i class="fa fa-eye"></i></a>
          <p><span>可运行</span> {{getValue(metrics.gauges['jvm.threads.runnable.count']) }}</p>
          <el-progress status="success" :text-inside="true" :stroke-width="18"
                       :percentage="getValue(metrics.gauges['jvm.threads.runnable.count'])  * 100 /
                       getValue(metrics.gauges['jvm.threads.count']) | numFilter">
          </el-progress>
          <p><span >定时等待</span> ({{getValue(metrics.gauges['jvm.threads.timed_waiting.count']) }})</p>
          <el-progress status="warning" :text-inside="true" :stroke-width="18"
                       :percentage="getValue(metrics.gauges['jvm.threads.timed_waiting.count'])  * 100 /
                       getValue(metrics.gauges['jvm.threads.count']) | numFilter">
          </el-progress>
          <p><span>等待中</span> ({{getValue(metrics.gauges['jvm.threads.waiting.count']) }})</p>
          <el-progress status="warning" :text-inside="true" :stroke-width="18"
                                  :percentage="getValue(metrics.gauges['jvm.threads.waiting.count'])  * 100 /
                                  getValue(metrics.gauges['jvm.threads.count']) | numFilter">
          </el-progress>
          <p><span>阻塞中</span> ({{getValue(metrics.gauges['jvm.threads.blocked.count']) }})</p>
          <el-progress status="success" :text-inside="true" :stroke-width="18"
                       :percentage="getValue(metrics.gauges['jvm.threads.blocked.count'])  * 100 /
                       getValue(metrics.gauges['jvm.threads.count']) | numFilter">
          </el-progress>
        </el-col>
        <el-col :span="8">
          <b>垃圾回收</b>
          <div class="row">
            <div class="col-md-9">标记清除次数</div>
            <div class="col-md-3 text-right">{{getValue(metrics.gauges['jvm.garbage.PS-MarkSweep.count']) }}</div>
          </div>
          <div class="row">
            <div class="col-md-9">标记清除耗时</div>
            <div class="col-md-3 text-right">{{getValue(metrics.gauges['jvm.garbage.PS-MarkSweep.time']) }}ms</div>
          </div>
          <div class="row">
            <div class="col-md-9">回收次数</div>
            <div class="col-md-3 text-right">{{getValue(metrics.gauges['jvm.garbage.PS-Scavenge.count']) }}</div>
          </div>
          <div class="row">
            <div class="col-md-9">回收耗时</div>
            <div class="col-md-3 text-right">{{getValue(metrics.gauges['jvm.garbage.PS-Scavenge.time']) }}ms</div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
  import waves from "@/directive/waves/index.js";
  import {getMetrics} from "./metricsService";

  export default {
    components: {
    },
    name: "admin_metrics",
    directives: {
      waves
    },
    data() {
      return{
        metrics:{gauges:{}},
        cachesStats:{},
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
      }
    },
    created() {
      this.initPageData();

    },
    methods: {
      getValue(val){
        return val && val.value;
      },
      initPageData() {
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
        });
      }
    }
  };
</script>
