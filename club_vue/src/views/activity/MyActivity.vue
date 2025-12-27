<template>
  <div class="my-activity-container">
    <el-card>
      <template #header><span>我的活动报名记录</span></template>
      
      <el-table :data="myActivities" stripe v-loading="loading">
        <el-table-column prop="title" label="活动名称" min-width="150" />
        <el-table-column prop="location" label="地点" width="150" />
        <el-table-column label="活动时间" width="180">
          <template #default="scope">{{ formatTime(scope.row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="报名时间" width="180">
          <template #default="scope">{{ formatTime(scope.row.signupTime) }}</template>
        </el-table-column>
        <el-table-column label="活动状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">进行中</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="info">已结束</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="签到状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.checkinStatus === 1 ? 'success' : 'warning'" effect="plain">
              {{ scope.row.checkinStatus === 1 ? '已签到' : '未签到' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
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

onMounted(fetchMyActivities)
</script>