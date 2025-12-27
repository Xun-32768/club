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

                <el-table-column label="操作" min-width="150">
                    <template #default="scope">
                        <el-button 
                            v-if="scope.row.joinStatus === 1 && scope.row.memberRole === 2" 
                            type="primary"
                            size="small" 
                            @click="handleManage(scope.row.clubId)"
                        >
                            管理社团
                        </el-button>

                        <el-button v-if="scope.row.joinStatus === 0" type="info" size="small" disabled>
                            等待审核
                        </el-button>

                        <el-button 
                            v-if="scope.row.joinStatus === 1 && scope.row.memberRole !== 2" 
                            type="danger" 
                            link
                            size="small"
                            @click="handleQuit(scope.row.clubId)"
                        >
                            退出社团
                        </el-button>

                        <el-tooltip 
                            v-if="scope.row.joinStatus === 1 && scope.row.memberRole === 2" 
                            content="社长需先转让职位后方可退出" 
                            placement="top"
                        >
                            <el-button type="info" link disabled size="small">退出社团</el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const myClubList = ref([])

const formatTime = (timeStr) => {
    if (!timeStr) return ''
    return timeStr.replace('T', ' ').substring(0, 19)
}

const fetchMyClubs = async () => {
    try {
        const res = await request.get('/club/my')
        myClubList.value = res
    } catch (e) {
        console.error('请求失败:', e)
    }
}

const handleManage = (clubId) => {
    router.push(`/club/manage/${clubId}`)
}

const handleQuit = (id) => {
    ElMessageBox.confirm(
        '确定要退出该社团吗？退出后需重新申请才能加入。',
        '退出确认',
        {
            confirmButtonText: '确定退出',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        try {
            // 注意：这里传的是 clubId
            await request.post('/club/member/quit', { clubId: id });
            ElMessage.success('退出成功');
            fetchMyClubs(); 
        } catch (error) {
            console.error('退出失败', error);
        }
    }).catch(() => { });
};

onMounted(() => {
    fetchMyClubs()
})
</script>

<style scoped>
.card-header {
    font-weight: bold;
}
.my-club-container {
    padding: 10px;
}
</style>