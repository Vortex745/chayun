<template>
  <div class="tea-page">

    <div class="action-bar">
      <div class="bar-left">
        <span class="header-icon">🍃</span>
        <span class="page-title">茶叶商品库</span>
        <div class="search-box">
          <el-input
              v-model="searchName"
              :prefix-icon="Search"
              class="search-input"
              clearable
              placeholder="搜索茶叶名称..."
              @clear="loadTeaList"
              @keyup.enter="handleSearch"
          />
          <el-button class="search-btn" type="primary" @click="handleSearch">搜索</el-button>
        </div>
      </div>

      <div class="bar-right">
        <el-button :icon="Refresh" circle class="icon-btn" title="刷新列表" @click="loadTeaList"/>
        <el-button :icon="Plus" class="add-btn" type="primary" @click="openDialog()">新增茶叶</el-button>
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
        <el-table-column align="center" label="ID" prop="id" width="80"/>
        <el-table-column align="center" label="商品图" width="110">
          <template #default="{ row }">
            <el-image :preview-src-list="[row.imgUrl]" :src="row.imgUrl" class="row-img" fit="cover" loading="lazy">
              <template #error>
                <div class="img-error">
                  <el-icon>
                    <Picture/>
                  </el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="茶叶名称" min-width="160" prop="name">
          <template #default="{ row }">
            <div class="name-col"><span class="tea-name">{{ row.name }}</span></div>
          </template>
        </el-table-column>
        <el-table-column align="center" label="分类" prop="type" width="120">
          <template #default="{ row }">
            <el-tag class="type-tag" effect="light" round>{{ row.type || '未分类' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="售价" prop="price" sortable width="140">
          <template #default="{ row }">
            <span class="price-text">¥ {{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" label="库存" prop="stock" width="140">
          <template #default="{ row }">
            <div class="stock-box">
              <span :class="row.stock < 20 ? 'text-danger' : ''" class="stock-num">{{ row.stock }}</span>
              <el-progress :percentage="Math.min(row.stock, 100)" :show-text="false"
                           :status="row.stock < 20 ? 'exception' : 'success'" :stroke-width="6" style="width: 60px;"/>
            </div>
          </template>
        </el-table-column>
        <el-table-column align="center" fixed="right" label="操作" width="180">
          <template #default="{ row }">
            <el-button :icon="Edit" link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button :icon="Delete" link type="danger" @click="handleDelete(row.id)">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
        v-model="dialogVisible"
        :show-close="false"
        align-center
        class="custom-dialog"
        destroy-on-close
        width="550px"
    >
      <template #header>
        <div class="dialog-header">
          <span class="dialog-title">{{ form.id ? '编辑茶叶信息' : '录入新茶叶' }}</span>
          <span class="dialog-subtitle">Product Info</span>
        </div>
      </template>

      <el-form :model="form" class="dialog-form" label-width="80px" size="large">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" class="custom-input" placeholder="请输入茶叶名称，如：西湖龙井"/>
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="form.type" class="custom-select" placeholder="请选择茶叶分类" style="width: 100%;">
            <el-option v-for="t in ['绿茶','红茶','乌龙茶','白茶','普洱茶','花茶','其他']" :key="t" :label="t"
                       :value="t"/>
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" required>
              <el-input-number v-model="form.price" :min="0" :precision="2" controls-position="right"
                               style="width: 100%;"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存" required>
              <el-input-number v-model="form.stock" :min="0" controls-position="right" style="width: 100%;"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品图片">
          <el-upload
              :action="uploadUrl"
              :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccess"
              :show-file-list="false"
              class="avatar-uploader"
              name="file"
          >
            <img v-if="form.imgUrl" :src="form.imgUrl" class="avatar"/>
            <div v-else class="upload-placeholder">
              <el-icon class="upload-icon">
                <Plus/>
              </el-icon>
              <div class="upload-text">点击上传商品图</div>
            </div>
          </el-upload>
        </el-form-item>

        <el-form-item label="简介描述">
          <el-input v-model="form.content" :rows="3" class="custom-textarea" placeholder="请输入商品的详细描述..."
                    resize="none" type="textarea"/>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button class="btn-cancel" @click="dialogVisible = false">取 消</el-button>
          <el-button :loading="submitting" class="btn-save" type="primary" @click="saveTea">确 定</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import {computed, getCurrentInstance, onMounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Delete, Edit, Picture, Plus, Refresh, Search} from '@element-plus/icons-vue'

const {proxy} = getCurrentInstance()
const axios = proxy.$http

const tableData = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const searchName = ref('')

const form = reactive({
  id: null, name: '', type: '', price: 0, stock: 0, content: '', imgUrl: ''
})

// 🔥🔥🔥 核心修改：动态获取后端地址
const baseURL = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const uploadUrl = computed(() => `${baseURL}/common/upload`)

const loadTeaList = async () => {
  loading.value = true
  let url = '/tea/list'
  try {
    const res = await axios.get(url)
    if (res.data.code === '200') {
      let data = res.data.data
      if (searchName.value) {
        data = data.filter(item => item.name.includes(searchName.value))
      }
      tableData.value = data
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (error) {
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => loadTeaList()

const openDialog = (row = null) => {
  if (row) Object.assign(form, row)
  else {
    form.id = null;
    form.name = '';
    form.type = '';
    form.price = 0;
    form.stock = 100;
    form.content = '';
    form.imgUrl = '';
  }
  dialogVisible.value = true
}

const saveTea = async () => {
  if (!form.name) return ElMessage.warning('请输入名称')
  submitting.value = true
  try {
    const isEdit = !!form.id
    const url = isEdit ? '/tea/update' : '/tea/add'
    const method = isEdit ? axios.put : axios.post
    const res = await method(url, form)
    if (res.data.code === '200') {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      loadTeaList()
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定下架该茶叶？', '提示', {type: 'warning'})
      .then(async () => {
        try {
          const res = await axios.delete(`/tea/delete/${id}`)
          if (res.data.code === '200') {
            ElMessage.success('已下架');
            loadTeaList()
          } else ElMessage.error(res.data.msg)
        } catch (e) {
        }
      }).catch(() => {
  })
}

const handleAvatarSuccess = (res) => {
  if (res.code === '200') form.imgUrl = res.data
  else ElMessage.error(res.msg)
}
const beforeAvatarUpload = (file) => {
  const isImg = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt6M = file.size / 1024 / 1024 < 6
  if (!isImg || !isLt6M) ElMessage.error('图片格式或大小不符合要求')
  return isImg && isLt6M
}

onMounted(() => loadTeaList())
</script>

<style scoped>
.tea-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

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
  gap: 30px;
}

.page-title {
  font-size: 18px;
  font-weight: 800;
  color: #2c3e50;
}

.search-box {
  display: flex;
  gap: 10px;
  align-items: center;
}

.search-input {
  width: 220px;
}

:deep(.search-input .el-input__wrapper) {
  border-radius: 20px;
  background-color: #f7f9fc;
  box-shadow: none;
  border: 1px solid transparent;
}

:deep(.search-input .el-input__wrapper.is-focus) {
  background-color: white;
  border-color: #55ab62;
  box-shadow: 0 0 0 2px rgba(85, 171, 98, 0.15) !important;
}

.search-btn {
  border-radius: 20px;
  background-color: #55ab62;
  border-color: #55ab62;
}

.icon-btn {
  color: #666;
  margin-right: 10px;
}

.icon-btn:hover {
  color: #55ab62;
  background: #eef7f2;
}

.add-btn {
  background: linear-gradient(135deg, #55ab62, #429e50);
  border: none;
  border-radius: 20px;
  padding: 10px 24px;
  font-weight: bold;
  box-shadow: 0 4px 12px rgba(85, 171, 98, 0.3);
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(85, 171, 98, 0.4);
}

.data-table-container {
  background: white;
  border-radius: 16px;
  padding: 5px;
  flex: 1;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  overflow: hidden;
}

.row-img {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.tea-name {
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.type-tag {
  border-color: #e1f3d8;
  color: #55ab62;
  background: #f0f9eb;
  font-weight: bold;
}

.price-text {
  color: #55ab62;
  font-weight: 800;
  font-family: Arial;
}

.stock-box {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.stock-num {
  font-weight: bold;
  color: #666;
  width: 30px;
  text-align: right;
}

.text-danger {
  color: #f56c6c;
}

.dialog-header {
  display: flex;
  align-items: baseline;
  gap: 10px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.dialog-title {
  font-size: 20px;
  font-weight: 800;
  color: #333;
}

.dialog-subtitle {
  font-size: 13px;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.dialog-form {
  padding: 15px 10px 0;
}

.custom-input :deep(.el-input__wrapper), .custom-select :deep(.el-input__wrapper), .custom-textarea :deep(.el-textarea__inner) {
  background-color: #f8fafc;
  border-radius: 8px;
  box-shadow: none;
  border: 1px solid #eef2f6;
  transition: all 0.3s;
}

.custom-input :deep(.el-input__wrapper.is-focus), .custom-select :deep(.el-input__wrapper.is-focus), .custom-textarea :deep(.el-textarea__inner:focus) {
  background-color: white;
  border-color: #55ab62;
  box-shadow: 0 0 0 3px rgba(85, 171, 98, 0.1);
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #dcdfe6;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: .3s;
  background: #fafafa;
  width: 120px;
  height: 120px;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #55ab62;
  background: #f0f9eb;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
  transition: all 0.3s;
}

.upload-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 12px;
  text-align: center;
  padding: 0 10px;
  line-height: 1.4;
}

.avatar-uploader :deep(.el-upload:hover) .upload-placeholder {
  color: #55ab62;
}

.avatar {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 10px;
}

.btn-cancel {
  border-radius: 20px;
  padding: 10px 24px;
}

.btn-cancel:hover {
  background: #f4f4f5;
  color: #333;
  border-color: #dcdfe6;
}

.btn-save {
  background: linear-gradient(135deg, #55ab62, #429e50);
  border: none;
  border-radius: 20px;
  padding: 10px 28px;
  font-weight: bold;
  box-shadow: 0 4px 12px rgba(85, 171, 98, 0.25);
}

.btn-save:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 15px rgba(85, 171, 98, 0.35);
}
</style>
<style>
.custom-dialog {
  border-radius: 16px !important;
  overflow: hidden;
}

.custom-dialog .el-dialog__header {
  margin-right: 0;
  padding: 20px 25px 0;
}

.custom-dialog .el-dialog__body {
  padding: 20px 25px 10px;
}

.custom-dialog .el-dialog__footer {
  padding: 15px 25px 25px;
  border-top: 1px solid #f9f9f9;
  background: #fff;
}
</style>