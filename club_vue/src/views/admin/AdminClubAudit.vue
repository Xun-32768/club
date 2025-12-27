<template>
    <div class="admin-audit">
        <el-card>
            <template #header><span>待审核社团申请</span></template>
            <el-table :data="pendingClubs" v-loading="loading">
                <el-table-column prop="name" label="社团名称" />
                <el-table-column prop="category" label="分类" />
                <el-table-column prop="presidentName" label="申请人" />
                <el-table-column prop="createTime" label="申请时间" />
                <el-table-column label="操作">
                    <template #default="scope">
                        <el-button type="success" size="small" @click="handleAudit(scope.row.id, 1)">通过</el-button>
                        <el-button type="danger" size="small" @click="handleAudit(scope.row.id, 2)">驳回</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const pendingClubs = ref([])
const loading = ref(false)

const fetchPending = async () => {
    loading.value = true
    // 复用 list 接口，查询待审核(status=0)的社团
    const res = await request.post('/club/list', { status: 0, page: 1, size: 100 })
    pendingClubs.value = res.records.filter(c => c.status === 0)
    loading.value = false
}

const handleAudit = async (clubId, status) => {
    await request.post('/club/audit', { clubId, status })
    ElMessage.success('操作成功')
    fetchPending()
}

onMounted(fetchPending)
</script>