<template>
    <div class="admin-club-manage">
        <el-card>
            <template #header>
                <div class="header-content">
                    <span>全校社团总览</span>
                    <el-input v-model="queryForm.name" placeholder="搜索社团名称" style="width: 200px" @clear="fetchClubs"
                        clearable>
                        <template #append>
                            <el-button :icon="Search" @click="fetchClubs" />
                        </template>
                    </el-input>
                </div>
            </template>

            <el-table :data="clubList" v-loading="loading" stripe style="width: 100%">
                <el-table-column prop="name" label="社团名称" min-width="150" />
                
                <el-table-column prop="category" label="分类" width="100">
                    <template #default="scope">
                        <el-tag>{{ scope.row.category }}</el-tag>
                    </template>
                </el-table-column>

                <el-table-column prop="memberCount" label="成员数量" width="100" align="center">
                    <template #default="scope">
                        <el-tag type="info">{{ scope.row.memberCount || 0 }} 人</el-tag>
                    </template>
                </el-table-column>

                <el-table-column prop="presidentName" label="社长" width="120" />
                
                <el-table-column prop="createTime" label="创建时间" width="180">
                    <template #default="scope">{{ formatTime(scope.row.createTime) }}</template>
                </el-table-column>

                <el-table-column label="状态" width="100">
                    <template #default="scope">
                        <el-tag v-if="scope.row.status === 1" type="success">正常</el-tag>
                        <el-tag v-else-if="scope.row.status === 0" type="warning">审核中</el-tag>
                        <el-tag v-else-if="scope.row.status === 2" type="info">已驳回</el-tag>
                        <el-tag v-else type="danger">已解散</el-tag>
                    </template>
                </el-table-column>

                <el-table-column label="操作" width="220" fixed="right">
                    <template #default="scope">
                        <el-button type="primary" size="small" @click="handleGoManage(scope.row.id)">管理</el-button>
                        <el-button v-if="scope.row.status !== 3" type="danger" size="small"
                            @click="handleDismiss(scope.row)">解散</el-button>
                        <el-button v-else size="small" disabled>已解散</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination">
                <el-pagination 
                    layout="total, prev, pager, next" 
                    :total="Number(total)" 
                    v-model:current-page="queryForm.page"
                    @current-change="fetchClubs" 
                />
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const clubList = ref([])
const total = ref(0)

const queryForm = reactive({
    name: '',
    page: 1,
    size: 10
})

const formatTime = (val) => val ? val.replace('T', ' ').substring(0, 16) : ''

const fetchClubs = async () => {
    loading.value = true
    try {
        // 后端 listClubs 已根据 systemRole 适配：管理员可看全部
        const res = await request.post('/club/list', queryForm)
        clubList.value = res.records
        total.value = res.total
    } finally {
        loading.value = false
    }
}

// 跳转到该社团的具体管理页（复用现有的 ClubManage.vue）
const handleGoManage = (clubId) => {
    router.push(`/club/manage/${clubId}`)
}

// 解散社团
const handleDismiss = (club) => {
    ElMessageBox.confirm(
        `确定要解散社团【${club.name}】吗？解散后不可恢复。`,
        '高危操作提示',
        { confirmButtonText: '确定解散', cancelButtonText: '取消', type: 'error' }
    ).then(async () => {
        // 状态 3 代表已注销/解散
        await request.post('/club/admin/audit', { clubId: club.id, status: 3 })
        ElMessage.success('社团已解散')
        fetchClubs()
    })
}

onMounted(fetchClubs)
</script>

<style scoped>
.header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style>