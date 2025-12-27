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

          <el-menu-item v-if="userInfo.systemRole === 0" index="/admin/club-audit">
            <el-icon>
              <Checked />
            </el-icon>
            <span>社团创建审核</span>
          </el-menu-item>
          <el-menu-item v-if="userInfo.systemRole === 0" index="/admin/club-manage">
            <el-icon>
              <Checked />
            </el-icon>
            <span>社团管理</span>
          </el-menu-item>
          <el-menu-item index="/club/list">
            <el-icon>
              <Trophy />
            </el-icon>
            <span>{{ userInfo.systemRole === 0 ? '社团总览' : '社团大厅' }}</span>
          </el-menu-item>

          <el-menu-item v-if="userInfo.systemRole != 0" index="/my-club">
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
          <el-menu-item v-if="userInfo.systemRole != 0" index="/activity/my">
            <el-icon>
              <Calendar />
            </el-icon>
            <span>我的活动</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="breadcrumb">高校社团管理系统 - 欢迎您</div>

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
                  <el-dropdown-item @click="pwdDialogVisible = true">修改密码</el-dropdown-item>
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

    <el-dialog v-model="pwdDialogVisible" title="修改密码" width="400px" @closed="handleDialogClosed">
      <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入旧密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpdatePwd" :loading="pwdLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { School, Odometer, Trophy, UserFilled, Calendar, ArrowDown, Checked } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()

// --- 用户信息逻辑 ---
const userInfo = ref({
  realName: '',
  username: '',
  systemRole: null
})

const fetchUserInfo = async () => {
  try {
    const res = await request.get('/user/info')
    userInfo.value = res
  } catch (error) {
    console.error('Layout获取用户信息失败', error)
  }
}

// --- 修改密码相关逻辑 (之前缺失的部分) ---
const pwdDialogVisible = ref(false)
const pwdLoading = ref(false)
const pwdFormRef = ref(null)

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 自定义校验：验证两次输入的密码是否一致
const validateConfirmPwd = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ]
}

// 提交修改
const submitUpdatePwd = () => {
  pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      pwdLoading.value = true
      try {
        await request.post('/user/updatePassword', {
          oldPassword: pwdForm.oldPassword,
          newPassword: pwdForm.newPassword
        })
        ElMessage.success('密码修改成功，请重新登录')
        // 成功后清除token并跳转登录
        localStorage.removeItem('token')
        router.push('/login')
      } catch (error) {
        // 这里的 error 提示 request.js 通常已经处理过了
      } finally {
        pwdLoading.value = false
      }
    }
  })
}

// 弹窗关闭时重置表单
const handleDialogClosed = () => {
  if (pwdFormRef.value) {
    pwdFormRef.value.resetFields()
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

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
/* 保持你的样式不变 */
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
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
}

.name-box {
  display: flex;
  flex-direction: column;
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