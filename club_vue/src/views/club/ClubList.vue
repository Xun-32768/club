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

              <el-button type="primary" link class="button" :disabled="club.isJoined" @click="handleApply(club.id)">
                {{ club.isJoined ? '已提交申请或已加入' : '申请加入' }}
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <div class="club-list">
    <el-dialog v-model="createDialogVisible" title="申请创建新社团" width="500px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="社团名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入社团名称" />
        </el-form-item>
        <el-form-item label="社团分类" prop="category">
          <el-select v-model="createForm.category" placeholder="请选择分类">
            <el-option label="科技" value="科技" />
            <el-option label="艺术" value="艺术" />
            <el-option label="体育" value="体育" />
          </el-select>
        </el-form-item>
        <el-form-item label="社团简介" prop="description">
          <el-input v-model="createForm.description" type="textarea" rows="4" placeholder="请简要介绍社团..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreateClub" :loading="submitting">提交申请</el-button>
      </template>
    </el-dialog>
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
    await ElMessageBox.confirm('确定要申请加入该社团吗?', '提示', {
      confirmButtonText: '确定申请',
      cancelButtonText: '再看看',
      type: 'info'
    });

    await request.post('/club/join', { clubId: clubId });
    ElMessage.success('申请提交成功！');

    // 成功后重新拉取列表，按钮会自动变为“已提交申请”并禁用
    fetchClubs();

  } catch (error) {
    if (error !== 'cancel') console.error(error);
  }
}

// ClubList.vue <script setup> 部分
const createDialogVisible = ref(false)
const submitting = ref(false)
const createFormRef = ref(null)

const createForm = reactive({
  name: '',
  category: '',
  description: ''
})

const createRules = {
  name: [{ required: true, message: '请输入社团名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  description: [{ required: true, message: '请输入社团简介', trigger: 'blur' }]
}

// 打开弹窗
const openCreateDialog = () => {
  createDialogVisible.value = true
  // 重置表单
  if (createFormRef.value) createFormRef.value.resetFields()
}

// 提交申请
const submitCreateClub = () => {
  createFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await request.post('/club/add', createForm)
        ElMessage.success('申请提交成功，请等待系统管理员审核！')
        createDialogVisible.value = false
        fetchClubs() // 刷新列表
      } catch (error) {
        console.error(error)
      } finally {
        submitting.value = false
      }
    }
  })
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