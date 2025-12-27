<template>
    <div class="activity-list-container">
        <el-card class="search-card">
            <el-form :inline="true" :model="queryForm">
                <el-form-item label="活动名称">
                    <el-input v-model="queryForm.title" placeholder="请输入活动名称" clearable />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="fetchActivities">搜索</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-row :gutter="20" style="margin-top: 20px">
            <el-col :span="8" v-for="item in activityList" :key="item.id" style="margin-bottom: 20px">
                <el-card :body-style="{ padding: '0px' }" shadow="hover" class="activity-card">
                    <div class="card-tag">
                        <el-tag effect="dark" type="success">报名中</el-tag>
                    </div>
                    <div style="padding: 20px">
                        <h3 class="title">{{ item.title }}</h3>
                        <div class="info-item">
                            <el-icon>
                                <Calendar />
                            </el-icon>
                            <span>{{ formatTime(item.startTime) }}</span>
                        </div>
                        <div class="info-item">
                            <el-icon>
                                <Location />
                            </el-icon>
                            <span>{{ item.location }}</span>
                        </div>
                        <div class="info-item">
                            <el-icon>
                                <User />
                            </el-icon>
                            <span>人数上限: {{ item.maxPeople === 0 ? '不限' : item.maxPeople }}</span>
                        </div>
                        <p class="content-preview">{{ item.content }}</p>
                        <div class="bottom">
                            <el-button type="primary" plain @click="handleDetail(item.id)">查看详情</el-button>
                            <el-button type="success" @click="handleSignup(item.id)">立即报名</el-button>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <el-dialog v-model="detailVisible" title="活动详情" width="600px" custom-class="detail-dialog">
            <div v-if="currentActivity" class="activity-detail-content">
                <h2 class="detail-title">{{ currentActivity.title }}</h2>

                <div class="detail-info">
                    <p><el-icon>
                            <Calendar />
                        </el-icon> <b>时间：</b>{{ formatTime(currentActivity.startTime) }} 至 {{
                            formatTime(currentActivity.endTime) }}</p>
                    <p><el-icon>
                            <Location />
                        </el-icon> <b>地点：</b>{{ currentActivity.location }}</p>
                    <p><el-icon>
                            <User />
                        </el-icon> <b>人数限制：</b>{{ currentActivity.maxPeople === 0 ? '不限' : currentActivity.maxPeople + '人' }}</p>
                </div>

                <el-divider content-position="left">活动介绍</el-divider>

                <div class="detail-desc">
                    {{ currentActivity.content }}
                </div>
            </div>
            <template #footer>
                <el-button @click="detailVisible = false">关闭</el-button>
                <el-button type="success" @click="handleSignup(currentActivity.id)">立即报名</el-button>
            </template>
        </el-dialog>
        <div class="pagination">
            <el-pagination layout="total, prev, pager, next" :total="Number(total)"
                v-model:current-page="queryForm.page" @current-change="fetchActivities" />
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Calendar, Location, User } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const activityList = ref([])
const total = ref(0)
const queryForm = reactive({
    page: 1,
    size: 9,
    title: ''
})

const formatTime = (val) => val ? val.replace('T', ' ') : ''

const fetchActivities = async () => {
    try {
        const res = await request.post('/activity/list', queryForm)
        activityList.value = res.records
        total.value = res.total
    } catch (error) {
        console.error(error)
    }
}

const handleSignup = async (id) => {
    try {
        // 1. 二次确认
        await ElMessageBox.confirm('确定要报名参加这个活动吗？', '报名确认', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        });

        // 2. 发送请求
        await request.post('/activity/member/signup', { activityId: id });

        // 3. 提示成功
        ElMessage.success('报名成功！');

        // 可选：刷新列表显示最新人数（如果前端展示了当前人数的话）
        fetchActivities();

    } catch (error) {
        if (error !== 'cancel') {
            console.error('报名失败', error);
            // 错误信息已由 request.js 拦截器统一弹出提示
        }
    }
};
const detailVisible = ref(false)
const currentActivity = ref(null)

// 查看详情
const handleDetail = async (id) => {
  try {
    const res = await request.get(`/activity/${id}`)
    currentActivity.value = res
    detailVisible.value = true
  } catch (error) {
    console.error('获取详情失败', error)
  }
}


onMounted(fetchActivities)
</script>

<style scoped>
.activity-card {
    position: relative;
}

.card-tag {
    position: absolute;
    right: 10px;
    top: 10px;
    z-index: 10;
}

.title {
    margin: 0 0 15px 0;
    font-size: 18px;
    color: #303133;
}

.info-item {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #606266;
    font-size: 14px;
    margin-bottom: 8px;
}

.content-preview {
    font-size: 13px;
    color: #909399;
    height: 40px;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    margin: 15px 0;
}

.bottom {
    display: flex;
    justify-content: space-between;
    border-top: 1px solid #EBEEF5;
    padding-top: 15px;
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}

.detail-title {
  color: #303133;
  margin-bottom: 20px;
  text-align: center;
}
.detail-info p {
  margin: 12px 0;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  color: #606266;
}
.detail-desc {
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap; /* 保留后台输入的换行 */
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
}
</style>