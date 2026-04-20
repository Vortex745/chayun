import test from 'node:test'
import assert from 'node:assert/strict'

import { DEFAULT_MESSAGE_DURATION, normalizeMessageOptions } from '../src/utils/message.js'

test('normalizeMessageOptions uses 2s as the default duration for string input', () => {
  assert.deepEqual(normalizeMessageOptions('加入成功', 'success'), {
    message: '加入成功',
    type: 'success',
    duration: DEFAULT_MESSAGE_DURATION,
    showClose: false,
  })
})

test('normalizeMessageOptions keeps explicit duration overrides', () => {
  const result = normalizeMessageOptions(
    {
      message: '处理中',
      duration: 0,
    },
    'info'
  )

  assert.equal(result.message, '处理中')
  assert.equal(result.type, 'info')
  assert.equal(result.duration, 0)
  assert.equal(result.showClose, false)
})
