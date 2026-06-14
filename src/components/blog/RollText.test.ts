import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import RollText from './RollText.vue'

describe('RollText', () => {
  it('renders container and accepts custom phrases', () => {
    const wrapper = mount(RollText, {
      props: {
        phrases: ['DollMeowOnly'],
      },
    })
    expect(wrapper.find('.roll-text').exists()).toBe(true)
  })
})
