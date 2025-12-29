<template>
  <div class="page-container">

    <div class="action-bar">
      <div class="bar-left">
        <span class="header-icon">📦</span>
        <span class="page-title">订单管理中心</span>
      </div>
      <div class="bar-right">
        <el-button :icon="Refresh" circle class="icon-btn" @click="loadData" title="刷新列表" />
      </div>
    </div>

    <div class="table-container">
      <el-table
          :data="tableData"
          style="width: 100%"
          v-loading="loading"
          :header-cell-style="{ background: '#f8fdfa', color: '#666', fontWeight: '600', height: '50px' }"
          :row-style="{ height: '75px' }"
          highlight-current-row
      >
        <el-table-column align="center" label="订单编号" width="180">
          <template #default="{ row }">
            <span class="order-no">{{ row.orderNo || '暂无编号' }}</span>
          </template>
        </el-table-column>

        <el-table-column align="center" label="下单用户" width="160">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="32" class="user-avatar">{{ (row.username || '客').charAt(0) }}</el-avatar>
              <span class="user-name">{{ row.username || '匿名用户' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column align="center" label="实付金额" width="120">
          <template #default="{ row }">
            <span class="price-text">¥ {{ row.totalPrice }}</span>
          </template>
        </el-table-column>

        <el-table-column label="收货地址" min-width="200" prop="address" show-overflow-tooltip />

        <el-table-column label="配送状态" width="220">
          <template #default="{ row }">
            <div v-if="['已发货', '已完成'].includes(row.state)" class="delivery-wrapper">

              <div v-if="row.deliveryMethod === '自提'" class="pickup-badge">
                <el-icon class="icon"><Shop /></el-icon>
                <span>客户到店自提</span>
              </div>

              <div v-else class="express-info">
                <div class="express-company">{{ row.courierCompany || '快递' }}</div>
                <div class="express-number">
                  <el-icon style="margin-right: 2px; vertical-align: -1px;"><Van /></el-icon>
                  {{ row.trackingNumber }}
                </div>
              </div>

            </div>
            <div v-else class="pending-badge">
              <span class="dot"></span>等待发货
            </div>
          </template>
        </el-table-column>

        <el-table-column align="center" label="订单状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.state === '已发货'" type="success" effect="light" round class="status-tag">
              已发货
            </el-tag>
            <el-tag v-else-if="!row.state || row.state === '待发货'" type="warning" effect="light" round class="status-tag">
              待发货
            </el-tag>
            <el-tag v-else type="info" effect="light" round class="status-tag">
              {{ row.state }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column align="center" fixed="right" label="操作" width="160">
          <template #default="{ row }">
            <el-button
                v-if="!row.state || row.state === '待发货'"
                link
                type="primary"
                class="btn-action"
                @click="openSendDialog(row)"
            >
              发货
            </el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
        v-model="sendDialogVisible"
        destroy-on-close
        width="520px"
        align-center
        class="custom-dialog"
        :show-close="false"
    >
      <template #header>
        <div class="dialog-header">
          <span class="dialog-title">📦 订单发货</span>
          <span class="dialog-subtitle">Order Delivery</span>
        </div>
      </template>

      <el-form :model="sendForm" label-position="top" class="dialog-form" size="large">
        <el-form-item label="选择配送方式">
          <el-radio-group v-model="sendForm.deliveryMethod" class="custom-radio-group">
            <el-radio-button label="快递" value="快递">
              <el-icon><Van /></el-icon> 快递配送
            </el-radio-button>
            <el-radio-button label="自提" value="自提">
              <el-icon><Shop /></el-icon> 到店自提
            </el-radio-button>
          </el-radio-group>
        </el-form-item>

        <div v-if="sendForm.deliveryMethod === '快递'">
          <el-row :gutter="15">
            <el-col :span="10">
              <el-form-item label="快递公司">
                <el-select v-model="sendForm.courierCompany" placeholder="请选择" class="custom-select">
                  <el-option v-for="item in courierOptions" :key="item" :label="item" :value="item"/>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="14">
              <el-form-item label="快递单号">
                <el-input v-model="sendForm.trackingNumber" placeholder="输入快递单号" class="custom-input" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div v-else class="pickup-tips">
          <div class="icon-box"><el-icon><SuccessFilled /></el-icon></div>
          <div class="text-box">
            <h4>确认用户已自提？</h4>
            <p>点击“确认发货”后，订单状态将变更为“已发货”</p>
          </div>
        </div>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="sendDialogVisible = false" class="btn-cancel">取 消</el-button>
          <el-button :loading="sending" type="primary" @click="confirmSend" class="btn-save">确认发货</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { getCurrentInstance, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Delete, Van, Shop, SuccessFilled } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http

const loading = ref(false)
const tableData = ref([])
const sendDialogVisible = ref(false)
const sending = ref(false)
const courierOptions = ['中通', '顺丰', '圆通', '韵达', '极兔', '邮政', '京东', '其他']

const sendForm = reactive({
  id: null, deliveryMethod: '快递', courierCompany: '', trackingNumber: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await axios.get(`/order/list?t=${new Date().getTime()}`)
    if (res.data.code === '200') tableData.value = res.data.data
  } catch (e) { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const openSendDialog = (row) => {
  sendForm.id = row.id
  sendForm.deliveryMethod = '快递'
  sendForm.courierCompany = ''
  sendForm.trackingNumber = ''
  sendDialogVisible.value = true
}

const confirmSend = async () => {
  if (sendForm.deliveryMethod === '快递') {
    if (!sendForm.courierCompany) return ElMessage.warning('请选择快递公司')
    if (!sendForm.trackingNumber) return ElMessage.warning('请输入单号')
  } else {
    sendForm.courierCompany = ''
    sendForm.trackingNumber = ''
  }

  sending.value = true
  try {
    const res = await axios.post('/order/send', sendForm)
    if (res.data.code === '200') {
      ElMessage.success('发货成功')
      sendDialogVisible.value = false
      loadData()
    } else { ElMessage.error(res.data.msg) }
  } catch (e) { ElMessage.error('发货失败') }
  finally { sending.value = false }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除此订单吗？', '提示', { type: 'warning', confirmButtonText: '确定删除' })
      .then(async () => {
        await axios.delete(`/order/delete/${id}`)
        ElMessage.success('已删除')
        loadData()
      }).catch(() => {})
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container { height: 100%; display: flex; flex-direction: column; gap: 20px; }

/* 操作栏 */
.action-bar {
  background: white; padding: 15px 25px; border-radius: 16px;
  display: flex; justify-content: space-between; align-items: center;
  box-shadow: 0 4px 20px rgba(0,0,0,0.03);
}
.bar-left { display: flex; align-items: center; gap: 10px; }
.header-icon { font-size: 20px; }
.page-title { font-size: 18px; font-weight: 800; color: #2c3e50; }
.icon-btn { color: #666; }
.icon-btn:hover { color: #55ab62; background: #eef7f2; }

/* 表格区域 */
.table-container { background: white; border-radius: 16px; padding: 5px; flex: 1; box-shadow: 0 4px 20px rgba(0,0,0,0.03); overflow: hidden; }

/* 表格细节 */
.order-no { font-family: 'Monaco', monospace; color: #666; font-size: 13px; background: #f5f7fa; padding: 4px 8px; border-radius: 4px; }
.user-info { display: flex; align-items: center; justify-content: center; gap: 8px; }
.user-avatar { background: #55ab62; color: white; font-weight: bold; font-size: 12px; }
.user-name { font-weight: 500; color: #333; }
.price-text { color: #f56c6c; font-weight: 800; font-family: Arial; }

/* 🔥🔥🔥 核心修复：配送状态样式 🔥🔥🔥 */
.delivery-wrapper { display: flex; align-items: center; }

/* 自提标签：绿色胶囊风格 */
.pickup-badge {
  display: inline-flex; align-items: center; gap: 6px;
  background-color: #f0f9eb; color: #55ab62;
  padding: 6px 12px; border-radius: 20px;
  font-size: 13px; font-weight: 600;
  border: 1px solid #e1f3d8;
}
.pickup-badge .icon { font-size: 16px; }

/* 快递信息 */
.express-info { display: flex; flex-direction: column; align-items: flex-start; gap: 4px; }
.express-company {
  font-size: 12px; color: #666; background: #f2f3f5;
  padding: 2px 8px; border-radius: 4px; font-weight: bold;
}
.express-number {
  font-size: 13px; color: #333; font-family: 'Arial', sans-serif; letter-spacing: 0.5px;
}

/* 待发货文字 */
.pending-badge { display: flex; align-items: center; gap: 6px; color: #999; font-size: 13px; font-style: italic; }
.pending-badge .dot { width: 6px; height: 6px; background: #ddd; border-radius: 50%; }

.btn-action { color: #55ab62; font-weight: bold; font-size: 14px; }
.btn-action:hover { color: #429e50; }
.status-tag { border: none; font-weight: bold; }

/* 弹窗样式 (保持一致) */
.dialog-header { display: flex; align-items: baseline; gap: 10px; padding-bottom: 20px; border-bottom: 1px solid #f0f0f0; }
.dialog-title { font-size: 20px; font-weight: 800; color: #333; }
.dialog-subtitle { font-size: 13px; color: #999; text-transform: uppercase; letter-spacing: 1px; }
.dialog-form { padding: 15px 10px 0; }

.custom-radio-group :deep(.el-radio-button__inner) {
  border: none; background: #f5f7fa; color: #666; padding: 10px 20px;
  border-radius: 8px; margin-right: 10px; box-shadow: none !important;
}
.custom-radio-group :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: #55ab62; color: white; box-shadow: 0 4px 10px rgba(85, 171, 98, 0.3) !important;
}
.custom-input :deep(.el-input__wrapper), .custom-select :deep(.el-input__wrapper) {
  background-color: #f8fafc; border-radius: 8px; box-shadow: none; border: 1px solid #eef2f6; transition: all 0.3s;
}
.custom-input :deep(.el-input__wrapper.is-focus), .custom-select :deep(.el-input__wrapper.is-focus) {
  background-color: white; border-color: #55ab62; box-shadow: 0 0 0 3px rgba(85, 171, 98, 0.1);
}

.pickup-tips { background: #f0f9eb; border: 1px dashed #bce3c9; border-radius: 12px; padding: 20px; display: flex; align-items: center; gap: 15px; margin-top: 10px; }
.pickup-tips .icon-box { font-size: 32px; color: #55ab62; }
.pickup-tips h4 { margin: 0 0 5px; color: #2c3e50; font-size: 15px; }
.pickup-tips p { margin: 0; color: #7f9985; font-size: 13px; }

.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; padding-top: 15px; }
.btn-cancel { border-radius: 20px; padding: 10px 24px; }
.btn-save { background: linear-gradient(135deg, #55ab62, #429e50); border: none; border-radius: 20px; padding: 10px 28px; font-weight: bold; box-shadow: 0 4px 12px rgba(85, 171, 98, 0.25); }
.btn-save:hover { transform: translateY(-1px); box-shadow: 0 6px 15px rgba(85, 171, 98, 0.35); }
</style>

<style>
.custom-dialog { border-radius: 16px !important; overflow: hidden; }
.custom-dialog .el-dialog__header { margin-right: 0; padding: 20px 25px 0; }
.custom-dialog .el-dialog__body { padding: 20px 25px 10px; }
.custom-dialog .el-dialog__footer { padding: 15px 25px 25px; border-top: 1px solid #f9f9f9; background: #fff; }
</style>