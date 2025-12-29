<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-left">
        <img src="https://jsgzb.sdau.edu.cn/_upload/tpl/0d/0b/3339/template3339/images/2.jpg" alt="School" />
        <div class="overlay">
          <h2>高校社团管理系统</h2>
        </div>
      </div>
      
      <div class="login-right">
        <h3>欢迎登录</h3>
        <el-form :model="form" :rules="rules" ref="formRef" size="large">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入学号" :prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
          </el-form-item>
          
          <el-button type="primary" class="login-btn" @click="handleLogin" :loading="loading">立即登录</el-button>
          
          <div class="links">
            <el-link type="primary" @click="$router.push('/register')">没有账号？去注册</el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import request from '@/utils/request' // 引入刚才封装的 axios
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const formRef = ref(null)
// 定义表单对象
const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 调用后端接口
        const token = await request.post('/user/login', form)
        
        // 登录成功
        localStorage.setItem('token', token)
        ElMessage.success('登录成功')
        
        // 跳转到首页 (后面我们会写 Dashboard)
        router.push('/dashboard')
      } catch (error) {
        // 错误已经在 request.js 里弹窗了，这里不用处理
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}
.login-box {
  width: 1000px;
  height: 450px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.6);
  display: flex;
  overflow: hidden;
}
.login-left {
  flex: 1;
  position: relative;
}
.login-left img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding:20px;
  background: linear-gradient(to top, rgba(20, 20, 20, 0.5), transparent);
  color: white;
  box-sizing: border-box;
}
.login-right {
  flex: 1;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.login-right h3 {
  margin-bottom: 30px;
  font-size: 24px;
  color: #333;
  text-align: center;
}
.login-btn {
  width: 100%;
  margin-top: 10px;
}
.links {
  margin-top: 15px;
  text-align: right;
}
</style>