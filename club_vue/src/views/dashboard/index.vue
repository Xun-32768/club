<template>
  <div class="home-container">

  <el-card class="welcome-card" shadow="hover">
      <div class="user-profile">
        <el-avatar :size="64" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
        <div class="info-box">
          <div class="name-row">
            <span class="greeting">{{ greeting }}，</span>
            <span class="real-name">{{ userInfo.realName || '同学' }}</span>
          </div>
          <div class="role-info">
            <el-tag size="small" effect="dark">
              {{ userInfo.username }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-card>
    
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="icon club-icon"><School /></el-icon>
            <div class="right">
              <div class="label">活跃社团</div>
              <div class="value">{{ stats.activeClubCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="icon ongoing-icon"><VideoPlay /></el-icon>
            <div class="right">
              <div class="label">正在进行的活动</div>
              <div class="value">{{ stats.ongoingActivityCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="icon signup-icon"><EditPen /></el-icon>
            <div class="right">
              <div class="label">火热报名中</div>
              <div class="value">{{ stats.signingActivityCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
        <el-card style="margin-top: 20px">
      <h3>系统公告</h3>
      <p>欢迎使用高校社团管理系统！请遵守社团管理规定...</p>
    </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted,computed } from 'vue'
import request from '@/utils/request'

const stats = ref({
  activeClubCount: 0,
  ongoingActivityCount: 0,
  signingActivityCount: 0
})

const fetchStats = async () => {
  try {
    const res = await request.get('/index/statistics')
    stats.value = res
  } catch (e) {
    console.error('获取统计数据失败', e)
  }
}
const userInfo = ref({})

// 计算当前是早上、下午还是晚上
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

// 获取当前用户信息
const fetchUserInfo = async () => {
  try {
    const res = await request.get('/user/info')
    // 后端返回的是 User 对象
    userInfo.value = res
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

onMounted(() => {
  fetchUserInfo()
})
onMounted(fetchStats)
</script>

<style scoped>
  .welcome-card {
  margin-bottom: 20px;
}
.user-profile {
  display: flex;
  align-items: center;
}
.info-box {
  margin-left: 20px;
}
.name-row {
  font-size: 20px;
  font-weight: bold;
}
.home-container { padding: 20px; }
.stat-card { border-radius: 8px; }
.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.icon {
  font-size: 48px;
  margin-right: 20px;
}

.club-icon { color: #409EFF; }
.ongoing-icon { color: #67C23A; }
.signup-icon { color: #E6A23C; }

.label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}
.value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.real-name {
  color: #409EFF;
  /* 名字用蓝色高亮 */
}
</style>