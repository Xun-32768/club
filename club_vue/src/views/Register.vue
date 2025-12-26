<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-right" style="width: 100%">
        <h3 style="text-align: center">注册新账号</h3>
        <el-form :model="form" :rules="rules" ref="formRef" size="large" label-width="80px">
          
          <el-form-item label="学号" prop="username">
            <el-input v-model="form.username" placeholder="请输入学号" />
          </el-form-item>
          
          <el-form-item label="姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="你的真实姓名" />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password />
          </el-form-item>

          <el-button type="primary" class="login-btn" @click="handleRegister" :loading="loading">注册并登录</el-button>
          
          <div class="links">
             <el-link @click="$router.push('/login')">已有账号？返回登录</el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
  username: '',
  password: '',
  realName: ''
})

const rules = {
  username: [{ required: true, message: '必填', trigger: 'blur' }],
  realName: [{ required: true, message: '必填', trigger: 'blur' }],
  password: [{ required: true, message: '必填', trigger: 'blur' }]
}

const handleRegister = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await request.post('/user/register', form)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* 复用 Login.vue 的样式，或者把 .login-container 放到全局样式里 */
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}
.login-box {
  width: 400px;
  padding: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}
.login-btn { width: 100%; margin-top: 20px; }
.links { margin-top: 15px; text-align: center; }
</style>