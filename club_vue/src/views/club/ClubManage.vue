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

                        <el-table-column label="操作" width="250">
                            <template #default="scope">
                                <div v-if="scope.row.status === 0">
                                    <el-button type="success" size="small"
                                        @click="handleAudit(scope.row.id, true)">通过</el-button>
                                    <el-button type="danger" size="small"
                                        @click="handleAudit(scope.row.id, false)">拒绝</el-button>
                                </div>

                                <div v-else-if="scope.row.status === 1 && scope.row.memberRole !== 2">
                                    <el-button type="warning" size="small" plain @click="handleTransfer(scope.row)">
                                        转让社长
                                    </el-button>
                                    <el-button type="danger" size="small" link
                                        @click="handleRemoveMember(scope.row.id)">移除</el-button>
                                </div>

                                <div v-else>
                                    <el-tag size="small" type="warning" effect="plain">现任社长</el-tag>
                                </div>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>

                <el-tab-pane label="活动发布" name="activity">
                    <div class="tab-action">
                        <el-radio-group v-model="activityStatusFilter" size="small" @change="fetchClubActivities">
                            <el-radio-button :label="-1">全部活动</el-radio-button>
                            <el-radio-button :label="1">已发布</el-radio-button>
                            <el-radio-button :label="2">已结束</el-radio-button>
                        </el-radio-group>
                        <el-button type="primary" size="small" @click="openActivityDialog">发布新活动</el-button>
                    </div>

                    <el-table :data="clubActivityList" v-loading="activityLoading" style="width: 100%; margin-top: 15px"
                        stripe>
                        <el-table-column prop="title" label="活动名称" min-width="150" />
                        <el-table-column prop="location" label="地点" width="120" />
                        <el-table-column label="活动时间" width="180">
                            <template #default="scope">
                                {{ formatTime(scope.row.startTime) }}
                            </template>
                        </el-table-column>
                        <el-table-column label="状态" width="100">
                            <template #default="scope">
                                <el-tag v-if="scope.row.status === 1" type="success">已发布</el-tag>
                                <el-tag v-else-if="scope.row.status === 2" type="info">已结束</el-tag>
                                <el-tag v-else type="warning">草稿</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="120">
                            <template #default="scope">
                                <el-button type="danger" link size="small"
                                    @click="handleDeleteActivity(scope.row.id)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>

                    <el-dialog v-model="activityVisible" title="发布新活动" width="600px">
                        <el-form :model="activityForm" :rules="activityRules" ref="activityFormRef" label-width="100px">
                            <el-form-item label="活动标题" prop="title">
                                <el-input v-model="activityForm.title" placeholder="请输入活动标题" />
                            </el-form-item>
                            <el-form-item label="活动地点" prop="location">
                                <el-input v-model="activityForm.location" placeholder="请输入活动地点" />
                            </el-form-item>
                            <el-form-item label="活动时间" prop="timeRange">
                                <el-date-picker v-model="activityForm.timeRange" type="datetimerange"
                                    range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间"
                                    value-format="YYYY-MM-DD HH:mm:ss" />
                            </el-form-item>
                            <el-form-item label="人数限制" prop="maxPeople">
                                <el-input-number v-model="activityForm.maxPeople" :min="0" placeholder="0表示不限" />
                            </el-form-item>
                            <el-form-item label="活动内容" prop="content">
                                <el-input v-model="activityForm.content" type="textarea" :rows="4"
                                    placeholder="请输入活动详情..." />
                            </el-form-item>
                        </el-form>
                        <template #footer>
                            <el-button @click="activityVisible = false">取消</el-button>
                            <el-button type="primary" @click="submitActivity"
                                :loading="activityLoading">立即发布</el-button>
                        </template>
                    </el-dialog>
                </el-tab-pane>

            </el-tabs>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
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

const activityVisible = ref(false)
const activityLoading = ref(false)
const activityFormRef = ref(null)
const activityForm = reactive({
    clubId: route.params.id, // 当前管理的社团ID
    title: '',
    location: '',
    timeRange: [],
    maxPeople: 0,
    content: ''
})

const clubActivityList = ref([]);
const activityStatusFilter = ref(-1);

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

const activityRules = {
    title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
    location: [{ required: true, message: '请输入地点', trigger: 'blur' }],
    timeRange: [{ required: true, message: '请选择时间', trigger: 'change' }],
    content: [{ required: true, message: '请输入活动内容', trigger: 'blur' }]
}

const openActivityDialog = () => {
    activityVisible.value = true
    if (activityFormRef.value) activityFormRef.value.resetFields()
}

// 获取该社团的活动列表
const fetchClubActivities = async () => {
    activityLoading.value = true;
    try {
        const res = await request.get(`/activity/club/${clubId}`);
        // 如果有状态过滤需求，可以在前端过滤，或者后端接口加参数
        if (activityStatusFilter.value !== -1) {
            clubActivityList.value = res.filter(a => a.status === activityStatusFilter.value);
        } else {
            clubActivityList.value = res;
        }
    } catch (e) {
        console.error('获取活动列表失败', e);
    } finally {
        activityLoading.value = false;
    }
};

const submitActivity = () => {
    activityFormRef.value.validate(async (valid) => {
        if (valid) {
            activityLoading.value = true
            try {
                // 1. 正确定义 postData，合并表单数据并处理时间字段
                const postData = {
                    clubId: route.params.id, // 确保携带当前社团ID
                    title: activityForm.title,
                    location: activityForm.location,
                    maxPeople: activityForm.maxPeople,
                    content: activityForm.content,
                    startTime: activityForm.timeRange[0], // 从时间范围数组中提取
                    endTime: activityForm.timeRange[1]
                }

                // 2. 发送请求
                await request.post('/activity/add', postData)

                ElMessage.success('活动发布成功！')
                activityVisible.value = false

                // 3. 刷新活动列表（如果你之前实现了 fetchClubActivities）
                if (typeof fetchClubActivities === 'function') {
                    fetchClubActivities()
                }
            } catch (error) {
                console.error('发布活动失败:', error)
            } finally {
                activityLoading.value = false
            }
        }
    })
}

// 删除活动逻辑
const handleDeleteActivity = (id) => {
    ElMessageBox.confirm('确定要删除该活动吗？', '提示', { type: 'warning' }).then(async () => {
        // 建议后端实现逻辑删除或 status 改为已取消
        // await request.delete(`/activity/${id}`); 
        ElMessage.success('删除功能待后端实现');
    });
};

const handleTransfer = (memberRow) => {
    ElMessageBox.confirm(
        `确定要将社长职位转让给【${memberRow.realName}】吗？转让后你将失去管理权限！`,
        '职位转让警告',
        {
            confirmButtonText: '确定转让',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        try {
            await request.post('/club/transfer', {
                clubId: clubId,
                newUserId: memberRow.userId // 注意这里使用的是成员对应的用户ID
            });
            ElMessage.success('转让成功，正在退出管理页面...');
            
            // 转让后由于失去权限，跳转回“我的社团”
            setTimeout(() => {
                router.push('/my-club');
            }, 1500);
        } catch (error) {
            console.error('转让失败', error);
        }
    }).catch(() => {});
};

// 在原有的 onMounted 中增加调用
onMounted(() => {
    if (clubId) {
        fetchMembers();
        fetchClubActivities(); // 新增调用
    }
    // ...
});
</script>

<style scoped>
.tab-action {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
}
</style>