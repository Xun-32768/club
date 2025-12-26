import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
    baseURL: 'http://localhost:32768', // 后端接口地址
    timeout: 5000
})

// 1. 请求拦截器：发送请求前自动加 Token
request.interceptors.request.use(config => {
    // 假设登录成功后，我们把 token 存在 localStorage 里
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = token;
    }
    return config;
}, error => {
    return Promise.reject(error);
})

// 2. 响应拦截器：统一处理错误
request.interceptors.response.use(response => {
    let res = response.data;
    // 如果后端返回的是标准 Result 结构 (code, msg, data)
    if (res.code === 200) {
        return res.data; // 直接返回数据部分
    } else {
        ElMessage.error(res.msg || '系统异常');
        return Promise.reject(res.msg);
    }
}, error => {
    // 处理 401 未登录等 HTTP 错误
    if (error.response && error.response.status === 401) {
        ElMessage.error('登录过期，请重新登录');
        localStorage.removeItem('token');
        // 这里可以跳转回登录页
    } else {
        ElMessage.error(error.message || '网络错误');
    }
    return Promise.reject(error);
})

export default request