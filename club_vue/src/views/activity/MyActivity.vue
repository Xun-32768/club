<template>
  <div class="my-activity-container">
    <el-card>
      <template #header><span>我的活动报名记录</span></template>

      <el-table :data="myActivities" stripe v-loading="loading">
        <el-table-column prop="title" label="活动名称" min-width="150" />
        <el-table-column prop="location" label="地点" width="150" />
        <el-table-column label="活动时间" width="200">
          <template #default="scope">
            <div class="time-range-box">
              <div class="time-item start">
                <el-tag size="small" type="success" effect="plain" class="time-tag">起</el-tag>
                <span>{{ formatTime(scope.row.startTime) }}</span>
              </div>
              <div class="time-item end" style="margin-top: 5px;">
                <el-tag size="small" type="danger" effect="plain" class="time-tag">止</el-tag>
                <span>{{ formatTime(scope.row.endTime) }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="报名时间" width="180">
          <template #default="scope">{{ formatTime(scope.row.signupTime) }}</template>
        </el-table-column>
        <el-table-column label="活动状态" width="120">
          <template #default="scope">
            <el-tag :type="getActivityStatus(scope.row.startTime, scope.row.endTime).type">
              {{ getActivityStatus(scope.row.startTime, scope.row.endTime).text }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="签到状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.checkinStatus === 1 ? 'success' : 'warning'" effect="plain">
              {{ scope.row.checkinStatus === 1 ? '已签到' : '未签到' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button v-if="getActivityStatus(scope.row.startTime, scope.row.endTime).text === '未开始'" type="danger"
              link size="small" @click="handleCancel(scope.row.activityId)">
              取消报名
            </el-button>

            <span v-else style="color: #999; font-size: 12px">不可取消</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const myActivities = ref([])
const loading = ref(false)

const formatTime = (val) => val ? val.replace('T', ' ').substring(0, 16) : ''

const fetchMyActivities = async () => {
  loading.value = true
  try {
    myActivities.value = await request.get('/activity/member/my')
  } finally {
    loading.value = false
  }
}

// 判断时间是否在现在之后
const isBefore = (startTimeStr) => {
  if (!startTimeStr) return false;
  const startTime = new Date(startTimeStr.replace('T', ' '));
  return startTime > new Date(); // 如果开始时间大于当前时间，则返回 true（可以取消）
}

// 执行取消报名
const handleCancel = (activityId) => {
  ElMessageBox.confirm('确定要取消该活动的报名吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '点错了',
    type: 'warning'
  }).then(async () => {
    try {
      await request.post('/activity/member/cancel', { activityId });
      ElMessage.success('已成功取消报名');
      fetchMyActivities(); // 刷新列表
    } catch (error) {
      console.error('取消失败', error);
    }
  }).catch(() => { });
}
const getActivityStatus = (startTimeStr, endTimeStr) => {
  if (!startTimeStr || !endTimeStr) return { text: '未知', type: 'info' };

  const now = new Date();
  const start = new Date(startTimeStr.replace('T', ' '));
  const end = new Date(endTimeStr.replace('T', ' '));

  if (now < start) {
    return { text: '未开始', type: 'info' }; // 灰色
  } else if (now >= start && now <= end) {
    return { text: '进行中', type: 'success' }; // 绿色
  } else {
    return { text: '已结束', type: 'danger' }; // 红色/褐色
  }
};
onMounted(fetchMyActivities)
</script>

<style>
  .time-range-box {
  display: flex;
  flex-direction: column;
  justify-content: center;
  font-size: 13px;
  line-height: 1.4;
}

.time-item {
  display: flex;
  align-items: center;
  white-space: nowrap;
}

.time-tag {
  margin-right: 8px;
  padding: 0 4px;
  height: 18px;
  line-height: 16px;
  font-weight: bold;
}
</style>