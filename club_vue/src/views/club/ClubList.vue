<template>
  <div class="club-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="社团名称">
          <el-input v-model="queryForm.name" placeholder="输入社团名称搜索" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button type="success" @click="openCreateDialog">申请创建新社团</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6" v-for="club in clubList" :key="club.id" style="margin-bottom: 20px">
        <el-card :body-style="{ padding: '0px' }" shadow="hover">
          <img
            src="https://images.unsplash.com/photo-1519389950473-47ba0277781c?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
            class="image" />
          <div style="padding: 14px">
            <div class="club-title">
              <span>{{ club.name }}</span>
              <el-tag size="small" type="success">{{ club.category }}</el-tag>
            </div>
            <div class="club-desc">{{ club.description }}</div>
            <div class="bottom">
              <span class="president">社长: {{ club.presidentName }}</span>
              <el-button type="primary" link class="button" @click="handleApply(club.id)">申请加入</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request' // 引入我们封装的 axios
import { ElMessage, ElMessageBox } from 'element-plus'

const queryForm = reactive({
  name: '',
  page: 1,
  size: 10
})

const clubList = ref([]) // 列表数据
const total = ref(0)     // 总条数 (如果要加分页条的话)

// 获取数据的方法
const fetchClubs = async () => {
  try {
    // 调用后端接口
    const res = await request.post('/club/list', queryForm)
    // 后端返回的是 Page 对象，数据在 records 里
    clubList.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  }
}

// 页面加载时自动获取
onMounted(() => {
  fetchClubs()
})

const handleSearch = () => {
  queryForm.page = 1 // 搜索时重置回第一页
  fetchClubs()
}


const handleApply = async (clubId) => {
  try {
    // 二次确认弹窗 (优化体验)
    await ElMessageBox.confirm('确定要申请加入该社团吗?', '提示', {
      confirmButtonText: '确定申请',
      cancelButtonText: '再看看',
      type: 'info'
    })

    // 发送请求
    await request.post('/club/join', { clubId: clubId })

    ElMessage.success('申请提交成功！')


  } catch (error) {
    // 用户点击取消，或者后端报错，都会进这里
    if (error !== 'cancel') {
      console.error(error)
    }
  }
  const openCreateDialog = () => {
    ElMessage.info('申请创建社团功能开发中...')
  }
}
</script>

<style scoped>
.image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  display: block;
}

.club-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 10px;
}

.club-desc {
  font-size: 13px;
  color: #999;
  margin-bottom: 15px;
  height: 40px;
  /* 限制高度，防止参差不齐 */
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.president {
  font-size: 12px;
  color: #666;
}
</style>