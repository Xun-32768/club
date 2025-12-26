<template>
    <div class="my-club-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>我的社团 & 申请记录</span>
                </div>
            </template>

            <el-table :data="myClubList" style="width: 100%" stripe>
                <el-table-column prop="clubName" label="社团名称" width="200" />

                <el-table-column prop="clubCategory" label="分类" width="120">
                    <template #default="scope">
                        <el-tag>{{ scope.row.clubCategory }}</el-tag>
                    </template>
                </el-table-column>

                <el-table-column prop="joinTime" label="申请/加入时间" width="180">
                    <template #default="scope">
                        {{ formatTime(scope.row.joinTime) }}
                    </template>
                </el-table-column>

                <el-table-column label="当前状态" width="150">
                    <template #default="scope">
                        <el-tag v-if="scope.row.joinStatus === 1" type="success" effect="dark">正式成员</el-tag>
                        <el-tag v-else-if="scope.row.joinStatus === 0" type="warning">审核中</el-tag>
                        <el-tag v-else type="danger">已拒绝</el-tag>
                    </template>
                </el-table-column>

                <el-table-column label="职位" width="120">
                    <template #default="scope">
                        <span v-if="scope.row.joinStatus !== 1">-</span>
                        <el-tag v-else-if="scope.row.memberRole === 2" type="warning" effect="plain">社长</el-tag>
                        <el-tag v-else type="info" effect="plain">普通成员</el-tag>
                    </template>
                </el-table-column>

                <el-table-column label="操作">
                    <template #default="scope">
                        <el-button v-if="scope.row.memberRole === 2 && scope.row.joinStatus === 1" type="primary"
                            size="small" @click="handleManage(scope.row.clubId)">
                            管理社团
                        </el-button>

                        <el-button v-if="scope.row.joinStatus === 1" type="danger" size="small" link
                            @click="handleQuit(scope.row.clubId)">
                            退出
                        </el-button>
                        <el-button v-if="scope.row.joinStatus === 0" type="info" size="small" disabled>
                            等待审核
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router' // 1. 引入 useRouter
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

// 2. 初始化 router
const router = useRouter()

const myClubList = ref([])

// 简单的时间格式化
const formatTime = (timeStr) => {
    if (!timeStr) return ''
    return timeStr.replace('T', ' ').substring(0, 19)
}

const fetchMyClubs = async () => {
    // 调试技巧：如果请求不通，在这里打印一下 res
    // console.log('开始请求 /club/my')
    try {
        const res = await request.get('/club/my')
        // console.log('我的社团数据:', res)
        myClubList.value = res
    } catch (e) {
        console.error('请求失败:', e)
    }
}

const handleManage = (clubId) => {
    router.push(`/club/manage/${clubId}`) 
}

const handleQuit = (clubId) => {
    ElMessageBox.confirm('确定要退出该社团吗？', '提示', { type: 'warning' })
        .then(() => {
            ElMessage.info('退出功能暂未开发')
        })
}

onMounted(() => {
    fetchMyClubs()
})
</script>
<style scoped>
.card-header {
    font-weight: bold;
}
</style>