<template>
  <div class="page-container">
    <el-card class="box-card" shadow="never">
      <el-row justify="space-between" align="middle">
        <div class="title">🍃 茶叶商品列表</div>
        <div>
          <el-button :icon="Refresh" circle @click="loadTeaList" :loading="loading" />
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增茶叶</el-button>
        </div>
      </el-row>
    </el-card>

    <el-card class="box-card" shadow="never" style="margin-top: 15px;">
      <el-table :data="tableData" style="width: 100%" v-loading="loading" stripe>

        <el-table-column prop="id" label="ID" width="80" align="center" />

        <el-table-column label="图片" width="100" align="center">
          <template #default="{ row }">
            <el-image
                style="width: 60px; height: 60px; border-radius: 4px;"
                :src="row.imgUrl"
                :preview-src-list="[row.imgUrl]"
                fit="cover">
              <template #error><div class="img-error"><el-icon><Picture /></el-icon></div></template>
            </el-image>
          </template>
        </el-table-column>

        <el-table-column prop="name" label="茶叶名称" min-width="120" />

        <el-table-column prop="price" label="价格" width="120" sortable>
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">￥{{ row.price }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="stock" label="库存" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.stock < 10 ? 'danger' : 'success'" size="small">{{ row.stock }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="上架时间" width="180" align="center" show-overflow-tooltip />

        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row.id)">下架</el-button>
          </template>
        </el-table-column>

      </el-table>
    </el-card>

    <el-dialog
        v-model="dialogVisible"
        :title="form.id ? '编辑茶叶' : '新增茶叶'"
        width="500px"
        destroy-on-close>

      <el-form :model="form" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="例如：西湖龙井" />
        </el-form-item>

        <el-form-item label="价格" required>
          <el-input-number v-model="form.price" :precision="2" :step="10" :min="0" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="库存" required>
          <el-input-number v-model="form.stock" :min="0" :step="10" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="商品图片">
          <el-upload
              class="avatar-uploader"
              action="http://localhost:8080/common/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
              name="file"
          >
            <img v-if="form.imgUrl" :src="form.imgUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item v-if="form.imgUrl" label="预览">
          <img :src="form.imgUrl" style="height: 80px; width: 80px; object-fit: cover; border-radius: 4px; border: 1px solid #eee;">
        </el-form-item>

        <el-form-item label="简介">
          <el-input type="textarea" v-model="form.content" :rows="3" placeholder="请输入商品描述..." />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTea" :loading="submitting">确定保存</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>

import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Plus, Edit, Delete, Picture, Link } from '@element-plus/icons-vue'

// 获取全局 Axios
const { proxy } = getCurrentInstance()
const axios = proxy.$http

// 数据定义
const tableData = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)

// 表单对象
const form = reactive({
  id: null,
  name: '',
  price: 0,
  stock: 0,
  content: '',
  imgUrl: ''
})

// --- 方法定义 ---

// 1. 加载列表
const loadTeaList = async () => {
  loading.value = true
  try {
    const res = await axios.get('/tea/list')
    if (res.data.code === '200') {
      tableData.value = res.data.data
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (error) {
    ElMessage.error('连接后端失败')
  } finally {
    loading.value = false
  }
}

// 2. 打开弹窗
const openDialog = (row = null) => {
  if (row) {
    // 编辑模式：复制数据
    Object.assign(form, row)
  } else {
    // 新增模式：重置数据
    form.id = null
    form.name = ''
    form.price = 0
    form.stock = 100
    form.content = ''
    form.imgUrl = ''
  }
  dialogVisible.value = true
}

// 3. 保存数据
const saveTea = async () => {
  if (!form.name) return ElMessage.warning('请输入名称')

  submitting.value = true
  try {
    const isEdit = !!form.id
    const url = isEdit ? '/tea/update' : '/tea/add'
    const method = isEdit ? axios.put : axios.post

    const res = await method(url, form)

    if (res.data.code === '200') {
      ElMessage.success(isEdit ? '修改成功' : '新增成功')
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

// 4. 删除数据
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要下架该茶叶吗？', '警告', {
    confirmButtonText: '确定下架',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await axios.delete(`/tea/delete/${id}`)
      if (res.data.code === '200') {
        ElMessage.success('下架成功')
        loadTeaList()
      } else {
        ElMessage.error(res.data.msg)
      }
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}
// 上传成功后的回调
const handleAvatarSuccess = (response, uploadFile) => {
  if (response.code === '200') {
    // 把后端返回的 URL (http://localhost:8080/images/xxx.jpg) 填入表单
    form.imgUrl = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('上传失败：' + response.msg)
  }
}

// 上传前的校验（限制只能传 JPG/PNG 且不超过 2MB）
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('图片必须是 JPG 或 PNG 格式!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 初始化加载
onMounted(() => {
  loadTeaList()
})
</script>

<style scoped>
/* 稍微美化一下样式 */
.title { font-size: 16px; font-weight: bold; color: #333; }
.img-error { display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; background: #f5f7fa; color: #909399; }
/* 图片上传组件样式 */
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  line-height: 120px; /* 垂直居中 */
  border: 1px dashed #d9d9d9; /* 边框 */
  border-radius: 6px;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover; /* 保持比例填充 */
}
</style>