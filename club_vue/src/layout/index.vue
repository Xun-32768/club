<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="220px" class="aside-menu">
        <div class="logo">
          <el-icon>
            <School />
          </el-icon>
          <span>社团管理系统</span>
        </div>

        <el-menu active-text-color="#409EFF" background-color="#304156" text-color="#bfcbd9"
          :default-active="$route.path" router class="el-menu-vertical">
          <el-menu-item index="/dashboard">
            <el-icon>
              <Odometer />
            </el-icon>
            <span>首页概览</span>
          </el-menu-item>
          <el-menu-item index="/club/list">
            <el-icon>
              <Trophy />
            </el-icon>
            <span>社团大厅</span>
          </el-menu-item>
          <el-menu-item index="/my-club">
            <el-icon>
              <UserFilled />
            </el-icon>
            <span>我的社团</span>
          </el-menu-item>
          <el-menu-item index="/activity/list">
            <el-icon>
              <Calendar />
            </el-icon>
            <span>活动中心</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="breadcrumb">
            高校社团管理系统 - 欢迎您
          </div>

          <div class="user-info">
            <el-dropdown>
              <span class="el-dropdown-link">
                <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />

                <div class="name-box">
                  <span class="realname">{{ userInfo.realName || '同学' }}</span>
                  <span class="username">{{ userInfo.username }}</span>
                </div>

                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>

              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>个人中心</el-dropdown-item>
                  <el-dropdown-item>修改密码</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { School, Odometer, Trophy, UserFilled, Calendar, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request' // 引入请求工具

const router = useRouter()

// 定义用户信息变量
const userInfo = ref({
  realName: '',
  username: ''
})

// 获取用户信息的函数
const fetchUserInfo = async () => {
  try {
    const res = await request.get('/user/info')
    userInfo.value = res
  } catch (error) {
    console.error('Layout获取用户信息失败', error)
    // 如果获取失败（比如Token过期），可能会在 request.js 里自动跳回登录页
  }
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    router.push('/login')
    ElMessage.success('已退出登录')
  })
}

// 页面加载时调用
onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.common-layout,
.el-container {
  height: 100vh;
}

.aside-menu {
  background-color: #304156;
  color: white;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  background-color: #2b3a4d;
  color: white;
}

.logo .el-icon {
  margin-right: 8px;
  font-size: 22px;
}

.el-menu-vertical {
  border-right: none;
}

.header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, .08);
  /* 加点阴影更有层次感 */
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
}

/* 新增样式的部分 */
.name-box {
  display: flex;
  flex-direction: column;
  /* 上下排列 */
  margin-left: 10px;
  line-height: 1.2;
  text-align: left;
}

.realname {
  font-weight: bold;
  font-size: 14px;
  color: #333;
}

.username {
  font-size: 12px;
  color: #999;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>