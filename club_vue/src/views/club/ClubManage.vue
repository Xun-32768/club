<template>
    <div class="club-manage">
        <div class="page-header">
            <el-page-header @back="goBack" content="社团管理后台" />
        </div>

        <el-card style="margin-top: 20px">
            <el-tabs v-model="activeTab">

                <el-tab-pane label="成员管理" name="member">
                    <div class="tab-action">
                        <el-radio-group v-model="filterStatus" @change="fetchMembers" size="small">
                            <el-radio-button :label="-1">全部</el-radio-button>
                            <el-radio-button :label="0">待审核</el-radio-button>
                            <el-radio-button :label="1">正式成员</el-radio-button>
                        </el-radio-group>
                        <el-button @click="fetchMembers" :icon="Refresh" circle />
                    </div>

                    <el-table :data="memberList" v-loading="loading" style="width: 100%; margin-top: 15px">
                        <el-table-column prop="realName" label="姓名" />
                        <el-table-column prop="username" label="学号" />
                        <el-table-column prop="joinTime" label="申请时间" width="180">
                            <template #default="scope">{{ formatTime(scope.row.joinTime) }}</template>
                        </el-table-column>

                        <el-table-column label="状态" width="120">
                            <template #default="scope">
                                <el-tag v-if="scope.row.status === 1" type="success">正式成员</el-tag>
                                <el-tag v-else-if="scope.row.status === 0" type="warning" effect="dark">待审核</el-tag>
                                <el-tag v-else type="danger">已拒绝</el-tag>
                            </template>
                        </el-table-column>

                        <el-table-column label="操作" width="200">
                            <template #default="scope">
                                <div v-if="scope.row.status === 0">
                                    <el-button type="success" size="small"
                                        @click="handleAudit(scope.row.id, true)">通过</el-button>
                                    <el-button type="danger" size="small"
                                        @click="handleAudit(scope.row.id, false)">拒绝</el-button>
                                </div>
                                <div v-else>
                                    <span style="color: #999; font-size: 12px">无需操作</span>
                                </div>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>

                <el-tab-pane label="活动发布" name="activity">
                    <el-empty description="活动发布功能即将上线..." />
                </el-tab-pane>

            </el-tabs>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const clubId = route.params.id // 从 URL 获取社团 ID
const activeTab = ref('member')
const memberList = ref([])
const loading = ref(false)
const filterStatus = ref(-1) // -1表示查全部

// 返回上一页
const goBack = () => router.back()

// 时间格式化
const formatTime = (val) => val ? val.replace('T', ' ').substring(0, 16) : ''

// 获取成员列表
const fetchMembers = async () => {
    loading.value = true
    try {
        // 构造参数：如果选了全部(-1)，就不传 status 参数
        const params = {}
        if (filterStatus.value !== -1) {
            params.status = filterStatus.value
        }

        const res = await request.get(`/club/members/${clubId}`, { params })
        memberList.value = res
    } finally {
        loading.value = false
    }
}

// 审核操作
const handleAudit = (memberId, pass) => {
    const actionText = pass ? '通过' : '拒绝'
    const type = pass ? 'success' : 'warning'

    ElMessageBox.confirm(`确定要【${actionText}】该同学的申请吗?`, '审核确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: type
    }).then(async () => {
        await request.post('/club/member/audit', { memberId, pass })
        ElMessage.success(`已${actionText}`)
        fetchMembers() // 刷新列表
    })
}

onMounted(() => {
    if (clubId) {
        fetchMembers()
    } else {
        ElMessage.error('参数缺失')
        router.push('/my-club')
    }
})
</script>

<style scoped>
.tab-action {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
}
</style>