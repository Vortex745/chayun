<template>
  <div class="tea-page">

    <div class="action-bar">
      <div class="bar-left">
        <span class="header-icon">💬</span>
        <span class="page-title">会员留言</span>
        <div class="search-box">
          <span class="page-desc">查看并回复会员的咨询与建议</span>
        </div>
      </div>
      <div class="bar-right">
        <el-button :icon="Refresh" circle class="icon-btn" title="刷新列表" @click="loadData"/>
      </div>
    </div>

    <div class="data-table-container">
      <el-table
          v-loading="loading"
          :data="tableData"
          :header-cell-style="{ background: '#f8fdfa', color: '#666', fontWeight: '600', height: '50px' }"
          :row-style="{ height: '65px' }"
          highlight-current-row
          style="width: 100%"
      >
        <el-table-column align="center" label="提交时间" width="160">
          <template #default="{ row }">
            <div class="time-box">
              <el-icon>
                <Clock/>
              </el-icon>
              <span>{{ formatTime(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column align="center" label="用户" prop="username" show-overflow-tooltip width="120"/>

        <el-table-column align="center" label="联系电话" width="140">
          <template #default="{ row }">
            <span v-if="row.phone" class="phone-text">{{ row.phone }}</span>
            <span v-else class="no-data">-</span>
          </template>
        </el-table-column>

        <el-table-column label="留言内容" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="content-text">{{ row.content }}</span>
          </template>
        </el-table-column>

        <el-table-column align="center" label="回复情况" min-width="150">
          <template #default="{ row }">
            <div v-if="row.replyList && row.replyList.length > 0" class="reply-status replied">
              <el-icon>
                <ChatLineRound/>
              </el-icon>
              <span>已回复 ({{ row.replyList.length }})</span>
            </div>
            <div v-else class="reply-status waiting">暂未回复</div>
          </template>
        </el-table-column>

        <el-table-column align="center" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getTagType(row.state)" effect="plain" round size="small">
              {{ row.state }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column align="center" fixed="right" label="操作" width="160">
          <template #default="{ row }">
            <el-button
                :icon="Edit"
                link
                type="primary"
                @click="openReplyDialog(row)"
            >
              回复
            </el-button>
            <el-button :icon="Delete" link type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
        v-model="replyVisible"
        align-center
        class="custom-dialog chat-dialog"
        destroy-on-close
        title="回复详情"
        width="600px"
    >
      <div class="chat-container">
        <div class="chat-item user">
          <div class="avatar u">U</div>
          <div class="bubble">
            <div class="bubble-info">
              <span class="name">{{ currentRow.username }}</span>
              <span class="time">{{ formatTime(currentRow.createTime) }}</span>
            </div>
            <div class="text">{{ currentRow.content }}</div>
          </div>
        </div>

        <template v-if="currentRow.replyList && currentRow.replyList.length > 0">
          <div class="divider"><span>历史回复</span></div>
          <div v-for="reply in currentRow.replyList" :key="reply.id" class="chat-item admin">
            <div class="bubble">
              <div class="bubble-info">
                <span class="name">客服</span>
                <span class="time">{{ formatTime(reply.createTime) }}</span>
              </div>
              <div class="text">{{ reply.content }}</div>
            </div>
            <div class="avatar a">A</div>
          </div>
        </template>
      </div>

      <div class="reply-input-area">
        <el-input
            v-model="replyContent"
            :rows="3"
            class="custom-textarea"
            placeholder="请输入您的回复内容..."
            resize="none"
            type="textarea"
        />
        <div class="dialog-footer">
          <el-button class="btn-cancel" @click="replyVisible = false">关闭</el-button>
          <el-button class="btn-save" type="primary" @click="submitReply">发送回复</el-button>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script setup>
import {getCurrentInstance, onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {ChatLineRound, Clock, Delete, Edit, Refresh} from '@element-plus/icons-vue'

const {proxy} = getCurrentInstance()
const axios = proxy.$http
const tableData = ref([])
const loading = ref(false)

const replyVisible = ref(false)
const replyContent = ref('')
const currentRow = ref({})

const formatTime = (isoStr) => {
  if (!isoStr) return ''
  return isoStr.replace('T', ' ').substring(0, 16)
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await axios.get('/message/list')
    if (res.data.code === '200') {
      tableData.value = res.data.data
    }
  } catch (e) {
  } finally {
    loading.value = false
  }
}

const getTagType = (state) => {
  if (state === '未读') return 'danger'
  if (state === '已回复' || state === '已读') return 'success'
  return 'info'
}

const openReplyDialog = (row) => {
  currentRow.value = row
  replyContent.value = ''
  replyVisible.value = true
}

const submitReply = async () => {
  if (!replyContent.value.trim()) return ElMessage.warning('请输入回复内容')

  try {
    await axios.put('/message/reply', {
      id: currentRow.value.id,
      reply: replyContent.value
    })
    ElMessage.success('回复成功')
    replyVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('回复失败')
  }
}

const del = (id) => {
  ElMessageBox.confirm('确定删除这条留言吗？', '提示', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  }).then(async () => {
    await axios.delete(`/message/delete/${id}`)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {
  })
}

onMounted(() => loadData())
</script>

<style scoped>
.tea-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 顶部操作栏 */
.action-bar {
  background: white;
  padding: 15px 25px;
  border-radius: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
}

.bar-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.page-title {
  font-size: 18px;
  font-weight: 800;
  color: #2c3e50;
}

.page-desc {
  font-size: 13px;
  color: #999;
  border-left: 1px solid #eee;
  padding-left: 15px;
  margin-left: 5px;
}

.icon-btn {
  color: #666;
  transition: all 0.3s;
}

.icon-btn:hover {
  color: #55ab62;
  background: #eef7f2;
  transform: rotate(180deg);
}

/* 表格容器 */
.data-table-container {
  background: white;
  border-radius: 16px;
  padding: 5px;
  flex: 1;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  overflow: hidden;
}

/* 单元格样式 */
.time-box {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  color: #666;
  font-family: Arial, sans-serif;
  font-size: 13px;
}

.no-data {
  color: #ccc;
}

.phone-text {
  font-family: Arial, sans-serif;
  color: #555;
}

.content-text {
  color: #333;
}

.reply-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  font-size: 13px;
  padding: 4px 10px;
  border-radius: 20px;
  width: fit-content;
  margin: 0 auto;
}

.reply-status.replied {
  color: #55ab62;
  background: #eef9f2;
}

.reply-status.waiting {
  color: #999;
  background: #f5f5f5;
}

/* 弹窗样式 */
:deep(.custom-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.custom-dialog .el-dialog__header) {
  padding: 20px 25px 0;
  margin-right: 0;
}

:deep(.custom-dialog .el-dialog__body) {
  padding: 0;
}

/* 聊天容器 */
.chat-container {
  background: #f8fafc;
  padding: 20px 25px;
  height: 300px;
  overflow-y: auto;
  border-bottom: 1px solid #eee;
}

.chat-item {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-start;
  gap: 12px;
}

.chat-item.user {
  flex-direction: row;
}

.chat-item.admin {
  flex-direction: row;
  justify-content: flex-end;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  font-weight: bold;
  flex-shrink: 0;
}

.avatar.u {
  background: #ccc;
}

.avatar.a {
  background: #55ab62;
}

.bubble {
  max-width: 80%;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  position: relative;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.chat-item.user .bubble {
  background: white;
  border-top-left-radius: 2px;
  color: #333;
}

.chat-item.admin .bubble {
  background: #55ab62;
  border-top-right-radius: 2px;
  color: white;
}

.bubble-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  margin-bottom: 4px;
  opacity: 0.8;
  gap: 10px;
}

.bubble-info .name {
  font-weight: bold;
}

.divider {
  text-align: center;
  margin: 20px 0;
  position: relative;
}

.divider span {
  background: #f8fafc;
  padding: 0 10px;
  color: #ccc;
  font-size: 12px;
  position: relative;
  z-index: 1;
}

.divider::after {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  width: 100%;
  height: 1px;
  background: #eee;
  z-index: 0;
}

/* 输入区域 */
.reply-input-area {
  padding: 20px 25px;
  background: white;
}

.custom-textarea :deep(.el-textarea__inner) {
  border-radius: 8px;
  padding: 12px;
  background: #f9f9f9;
  border: 1px solid #eee;
  transition: all 0.3s;
  margin-bottom: 15px;
}

.custom-textarea :deep(.el-textarea__inner:focus) {
  border-color: #55ab62;
  background: white;
  box-shadow: 0 0 0 2px rgba(85, 171, 98, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-cancel {
  border-radius: 20px;
  padding: 10px 24px;
}

.btn-save {
  background: linear-gradient(135deg, #55ab62, #429e50);
  border: none;
  border-radius: 20px;
  padding: 10px 28px;
  font-weight: bold;
  color: white;
  box-shadow: 0 4px 12px rgba(85, 171, 98, 0.25);
}

.btn-save:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 15px rgba(85, 171, 98, 0.35);
}
</style>