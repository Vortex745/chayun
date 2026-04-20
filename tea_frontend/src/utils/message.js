import { ElMessage } from 'element-plus'

export const DEFAULT_MESSAGE_DURATION = 2000
const MESSAGE_PATCH_FLAG = '__chayunDefaultDurationPatched__'
const MESSAGE_METHODS = ['success', 'warning', 'info', 'error', 'primary']

export function normalizeMessageOptions(options, fallbackType) {
  if (typeof options === 'string') {
    return {
      message: options,
      type: fallbackType,
      duration: DEFAULT_MESSAGE_DURATION,
      showClose: false,
    }
  }

  return {
    duration: DEFAULT_MESSAGE_DURATION,
    showClose: false,
    type: fallbackType,
    ...(options || {}),
  }
}

export function patchMessageDefaults() {
  if (ElMessage[MESSAGE_PATCH_FLAG]) return

  for (const method of MESSAGE_METHODS) {
    const original = ElMessage[method].bind(ElMessage)
    ElMessage[method] = (options) => original(normalizeMessageOptions(options, method))
  }

  ElMessage[MESSAGE_PATCH_FLAG] = true
}

export function resolveRequestMessage(error, fallback = '操作失败，请稍后重试') {
  return error?.response?.data?.msg || error?.message || fallback
}

export async function runWithStatusMessage({
  pendingMessage,
  successMessage,
  errorMessage,
  task,
}) {
  const pendingToast = pendingMessage
    ? ElMessage.info({
        message: pendingMessage,
        duration: 0,
      })
    : null

  try {
    const result = await task()
    pendingToast?.close()
    if (successMessage) {
      ElMessage.success(successMessage)
    }
    return result
  } catch (error) {
    pendingToast?.close()
    ElMessage.error(resolveRequestMessage(error, errorMessage))
    throw error
  }
}
