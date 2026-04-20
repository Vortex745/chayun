<template>
  <div class="page-container">

    <div class="action-bar">
      <div class="bar-left">
        <span class="header-icon">🖼️</span>
        <span class="page-title">轮播图设置</span>
      </div>
      <div class="bar-right">
        <el-button :icon="Refresh" circle class="icon-btn" title="刷新列表" @click="loadData"/>
        <el-button :icon="Plus" class="add-btn" type="primary" @click="openDialog(null)">新增轮播图</el-button>
      </div>
    </div>

    <div class="table-container">
      <el-table
          v-loading="loading"
          :data="tableData"
          :header-cell-style="{ background: '#f8fdfa', color: '#666', fontWeight: '600', height: '50px' }"
          :row-style="{ height: '80px' }"
          highlight-current-row
          style="width: 100%"
      >
        <el-table-column align="center" label="排序" prop="sort" width="80">
          <template #default="{ row }">
            <el-tag class="sort-tag" effect="plain" round type="info">{{ row.sort }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column align="center" label="轮播内容" width="240">
          <template #default="{ row }">
            <div class="media-wrapper">
              <video
                  v-if="isVideo(row.imgUrl)"
                  :src="row.imgUrl"
                  class="row-media"
                  muted
                  autoplay
                  loop
                  playsinline
              ></video>
              <el-image
                  v-else
                  :preview-src-list="[row.imgUrl]"
                  :src="row.imgUrl"
                  class="row-media"
                  fit="cover"
                  preview-teleported
              >
                <template #error>
                  <div class="img-error"><el-icon><Picture/></el-icon></div>
                </template>
              </el-image>
              <div v-if="isVideo(row.imgUrl)" class="video-badge">视频</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="广告文案" min-width="200" prop="linkText">
          <template #default="{ row }">
            <span class="ad-text">{{ row.linkText || '--' }}</span>
          </template>
        </el-table-column>

        <el-table-column align="center" label="状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.isShow === 1" effect="light" round type="success">
              <span class="dot green"></span>展示中
            </el-tag>
            <el-tag v-else effect="light" round type="info">
              <span class="dot gray"></span>已隐藏
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column align="center" label="创建时间" prop="createTime" style="color: #999;" width="180"/>

        <el-table-column align="center" fixed="right" label="操作" width="180">
          <template #default="{ row }">
            <el-button :icon="Edit" link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button :icon="Delete" link type="danger" @click="handleDelete(row.id)">删除</el-button>
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
        width="520px"
    >
      <template #header>
        <div class="dialog-header">
          <span class="dialog-title">{{ form.id ? '编辑轮播图' : '新增轮播图' }}</span>
          <span class="dialog-subtitle">Manage Carousel</span>
        </div>
      </template>

      <el-form :model="form" class="dialog-form" label-width="80px" size="large">
        <el-form-item label="广告文案">
          <el-input
              v-model="form.linkText"
              class="custom-input"
              placeholder="例如：新茶上市，尝鲜季..."
          />
        </el-form-item>

        <el-form-item label="排序权重">
          <el-input-number
              v-model="form.sort"
              :min="1"
              controls-position="right"
              style="width: 100%;"
          />
          <div class="form-tip">数字越小越靠前显示</div>
        </el-form-item>

        <el-form-item label="展示状态">
          <div class="switch-wrapper">
            <el-switch
                v-model="form.isShow"
                :active-value="1"
                :inactive-value="0"
                active-color="#55ab62"
            />
            <span class="switch-text">{{ form.isShow ? '当前展示中' : '当前已隐藏' }}</span>
          </div>
        </el-form-item>

        <el-form-item label="上传内容" required>
          <el-upload
              :action="uploadUrl"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
              :show-file-list="false"
              class="avatar-uploader"
              name="file"
              accept="image/*,video/*"
          >
            <template v-if="form.imgUrl">
              <video
                  v-if="isVideo(form.imgUrl)"
                  :src="form.imgUrl"
                  class="avatar"
                  autoplay
                  muted
                  loop
                  style="object-fit: contain; background: black;"
              ></video>
              <img v-else :src="form.imgUrl" class="avatar"/>
              <div class="re-upload-mask">
                <el-icon><Plus/></el-icon> 替换
              </div>
            </template>

            <div v-else class="upload-placeholder">
              <el-icon class="upload-icon">
                <Plus/>
              </el-icon>
              <div class="upload-text">点击上传图片或视频</div>
              <div style="font-size: 12px; color: #999; margin-top: 5px;">支持 MP4/JPG/PNG (Max 100MB)</div>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button class="btn-cancel" @click="dialogVisible = false">取 消</el-button>
          <el-button :loading="saving" class="btn-save" type="primary" @click="saveData">确定保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, getCurrentInstance, onMounted, reactive, ref} from 'vue'
import {Delete, Edit, Picture, Plus, Refresh} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'

const {proxy} = getCurrentInstance()
const axios = proxy.$http

const tableData = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)

const form = reactive({
  id: null, imgUrl: '', linkText: '', sort: 1, isShow: 1
})

const baseURL = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const uploadUrl = computed(() => `${baseURL}/common/upload`)

// 【新增】判断是否为视频
const isVideo = (url) => {
  if (!url) return false
  const lower = url.toLowerCase()
  return lower.endsWith('.mp4') || lower.endsWith('.webm') || lower.endsWith('.ogg') || lower.endsWith('.mov')
}

// 【新增】上传前校验
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isVideo = file.type.startsWith('video/')
  const isLt100M = file.size / 1024 / 1024 < 100

  if (!isImage && !isVideo) {
    ElMessage.error('只能上传图片或视频文件!')
    return false
  }
  if (!isLt100M) {
    ElMessage.error('文件大小不能超过 100MB!')
    return false
  }
  return true
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await axios.get('/carousel/list')
    if (res.data.code === '200') tableData.value = res.data.data
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  if (row) Object.assign(form, row)
  else {
    form.id = null;
    form.imgUrl = '';
    form.linkText = '';
    form.sort = 1;
    form.isShow = 1;
  }
  dialogVisible.value = true
}

const handleUploadSuccess = (res) => {
  if (res.code === '200') {
    form.imgUrl = res.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res.msg)
  }
}

const saveData = async () => {
  if (!form.imgUrl) return ElMessage.warning('请上传轮播内容')
  saving.value = true
  try {
    const url = form.id ? '/carousel/update' : '/carousel/add' // 兼容你可能有的不同接口名
    // 如果你的后端统一用 save，请改回 /carousel/save
    // 这里假设沿用你之前代码里的逻辑，或者统一成 save
    // 注意：检查你的后端是区分 add/update 还是统一 save
    // 根据你之前的 CarouselManage 代码，你用的是 /carousel/save
    await axios.post('/carousel/save', form)

    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除吗？', '提示', {type: 'warning', confirmButtonText: '确定删除'})
      .then(async () => {
        await axios.delete(`/carousel/delete/${id}`)
        ElMessage.success('已删除')
        loadData()
      }).catch(() => {
  })
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container {
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

.bar-left { display: flex; align-items: center; gap: 10px; }
.header-icon { font-size: 20px; }
.page-title { font-size: 18px; font-weight: 800; color: #2c3e50; }

.icon-btn { color: #666; margin-right: 10px; }
.icon-btn:hover { color: #55ab62; background: #eef7f2; }

.add-btn {
  background: linear-gradient(135deg, #55ab62, #429e50);
  border: none;
  border-radius: 20px;
  padding: 10px 24px;
  font-weight: bold;
  box-shadow: 0 4px 12px rgba(85, 171, 98, 0.3);
}
.add-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 15px rgba(85, 171, 98, 0.4); }

.table-container {
  background: white;
  border-radius: 16px;
  padding: 5px;
  flex: 1;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  overflow: hidden;
}

/* 媒体预览容器 */
.media-wrapper {
  position: relative;
  width: 180px;
  height: 60px;
  margin: 0 auto;
}

.row-media {
  width: 100%;
  height: 100%;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  object-fit: cover;
  display: block;
}

.video-badge {
  position: absolute;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.6);
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-top-left-radius: 4px;
  border-bottom-right-radius: 6px;
}

.img-error {
  background: #f5f7fa;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
}

.ad-text { font-weight: 500; color: #333; }
.sort-tag { font-family: 'Arial'; font-weight: bold; }

.dot { display: inline-block; width: 6px; height: 6px; border-radius: 50%; margin-right: 6px; margin-bottom: 2px; }
.dot.green { background: #55ab62; }
.dot.gray { background: #909399; }

/* 弹窗样式 */
.dialog-header { display: flex; align-items: baseline; gap: 10px; padding-bottom: 20px; border-bottom: 1px solid #f0f0f0; }
.dialog-title { font-size: 20px; font-weight: 800; color: #333; }
.dialog-subtitle { font-size: 13px; color: #999; font-weight: 500; text-transform: uppercase; letter-spacing: 1px; }
.dialog-form { padding: 10px 10px 0; }

.custom-input :deep(.el-input__wrapper) { background-color: #f8fafc; border-radius: 8px; box-shadow: none; border: 1px solid #eef2f6; transition: all 0.3s; }
.custom-input :deep(.el-input__wrapper.is-focus) { background-color: white; border-color: #55ab62; box-shadow: 0 0 0 3px rgba(85, 171, 98, 0.1); }

.form-tip { font-size: 12px; color: #999; margin-top: 6px; line-height: 1.4; }
.switch-wrapper { display: flex; align-items: center; gap: 10px; }
.switch-text { font-size: 13px; color: #666; }

/* 上传组件优化 */
.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #dcdfe6;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: .3s;
  background: #fafafa;
  width: 100%;
  height: 180px; /* 增高以适应预览 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader :deep(.el-upload:hover) { border-color: #55ab62; background: #f0f9eb; }

.upload-placeholder { width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #8c939d; transition: all 0.3s; }
.upload-icon { font-size: 32px; margin-bottom: 8px; }
.upload-text { font-size: 14px; font-weight: 500; }
.avatar-uploader :deep(.el-upload:hover) .upload-placeholder { color: #55ab62; }

.avatar { width: 100%; height: 100%; display: block; object-fit: cover; }

/* 重新上传遮罩 */
.re-upload-mask {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0,0,0,0.5); color: white;
  display: flex; align-items: center; justify-content: center;
  opacity: 0; transition: opacity 0.3s; gap: 5px; font-weight: bold;
}
.avatar-uploader :deep(.el-upload:hover) .re-upload-mask { opacity: 1; }

.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; padding-top: 10px; }
.btn-cancel { border-radius: 20px; padding: 10px 24px; }
.btn-cancel:hover { background: #f4f4f5; color: #333; border-color: #dcdfe6; }
.btn-save { background: linear-gradient(135deg, #55ab62, #429e50); border: none; border-radius: 20px; padding: 10px 28px; font-weight: bold; box-shadow: 0 4px 12px rgba(85, 171, 98, 0.25); }
.btn-save:hover { transform: translateY(-1px); box-shadow: 0 6px 15px rgba(85, 171, 98, 0.35); }
</style>

<style>
.custom-dialog { border-radius: 16px !important; overflow: hidden; }
.custom-dialog .el-dialog__header { margin-right: 0; padding: 20px 25px 0; }
.custom-dialog .el-dialog__body { padding: 25px 25px 10px; }
.custom-dialog .el-dialog__footer { padding: 15px 25px 25px; border-top: 1px solid #f9f9f9; background: #fff; }
</style>