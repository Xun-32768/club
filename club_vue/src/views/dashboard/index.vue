<template>
  <div class="dashboard-container">
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
            <el-tag size="small" type="success" style="margin-left: 10px">
              {{ userInfo.college || '未知学院' }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>当前活跃社团</template>
          <h2 style="color: #409EFF">12</h2>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>本周活动</template>
          <h2 style="color: #67C23A">5</h2>
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
import { ref, reactive, onMounted, computed } from 'vue'
import request from '@/utils/request'

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
  margin-bottom: 8px;
  color: #303133;
}

.real-name {
  color: #409EFF;
  /* 名字用蓝色高亮 */
}
</style>