<template>
  <div ref="containerRef" class="roll-text" aria-live="polite" />
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref, watch } from 'vue'

const props = withDefaults(
  defineProps<{
    phrases?: string[]
    prefix?: string
  }>(),
  {
    phrases: () => [
      'DollMeowOnly ✨',
      '代码与猫，皆不可负',
      'Full Stack · Meow Dev',
      '游于代码之海',
      'Build · Break · Fix · Meow',
    ],
    prefix: '',
  },
)

const containerRef = ref<HTMLElement | null>(null)

const RAINBOW = [
  'rgb(110,64,170)', 'rgb(150,61,179)', 'rgb(191,60,175)', 'rgb(228,65,157)',
  'rgb(254,75,131)', 'rgb(255,94,99)', 'rgb(255,120,71)', 'rgb(251,150,51)',
  'rgb(226,183,47)', 'rgb(198,214,60)', 'rgb(175,240,91)', 'rgb(127,246,88)',
  'rgb(82,246,103)', 'rgb(48,239,130)', 'rgb(29,223,163)', 'rgb(26,199,194)',
  'rgb(35,171,216)', 'rgb(54,140,225)', 'rgb(76,110,219)', 'rgb(96,84,200)',
]

const STEP_DELAY = 75
const STEP_SIZE = 1
const PAUSE_TICKS = 2
const SPARK_COUNT = 5

let timerId = 0
let running = false

function randomColor() {
  return RAINBOW[Math.floor(Math.random() * RAINBOW.length)]
}

function randomChar() {
  return String.fromCharCode(94 * Math.random() + 33)
}

function sparkFragment(count: number) {
  const fragment = document.createDocumentFragment()
  for (let i = 0; i < count; i++) {
    const span = document.createElement('span')
    span.textContent = randomChar()
    span.style.color = randomColor()
    fragment.appendChild(span)
  }
  return fragment
}

function startAnimation() {
  stopAnimation()
  const root = containerRef.value
  if (!root || !props.phrases.length) return

  const phrases = props.phrases.map((p) => String(p))
  const prefix = props.prefix

  const state = {
    text: '',
    prefixP: -SPARK_COUNT,
    phraseI: 0,
    charP: 0,
    direction: 'forward' as 'forward' | 'backward',
    delay: PAUSE_TICKS,
    step: STEP_SIZE,
  }

  running = true

  const tick = () => {
    if (!running || !containerRef.value) return

    const current = phrases[state.phraseI]

    if (state.step > 0) {
      state.step--
    } else {
      state.step = STEP_SIZE

      if (state.prefixP < prefix.length) {
        if (state.prefixP >= 0) state.text += prefix[state.prefixP]
        state.prefixP++
      } else if (state.direction === 'forward') {
        if (state.charP < current.length) {
          state.text += current[state.charP]
          state.charP++
        } else if (state.delay > 0) {
          state.delay--
        } else {
          state.direction = 'backward'
          state.delay = PAUSE_TICKS
        }
      } else if (state.charP > 0) {
        state.text = state.text.slice(0, -1)
        state.charP--
      } else {
        state.phraseI = (state.phraseI + 1) % phrases.length
        state.direction = 'forward'
        state.prefixP = -SPARK_COUNT
      }
    }

    root.textContent = state.text
    const sparkLen =
      state.prefixP < prefix.length
        ? Math.min(SPARK_COUNT, SPARK_COUNT + state.prefixP)
        : Math.min(SPARK_COUNT, current.length - state.charP)
    root.appendChild(sparkFragment(sparkLen))

    timerId = window.setTimeout(tick, STEP_DELAY)
  }

  tick()
}

function stopAnimation() {
  running = false
  if (timerId) {
    clearTimeout(timerId)
    timerId = 0
  }
}

onMounted(startAnimation)

onUnmounted(stopAnimation)

watch(
  () => [props.phrases, props.prefix] as const,
  () => startAnimation(),
  { deep: true },
)
</script>

<style scoped>
.roll-text {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.04em;
  min-height: 22px;
  padding: 0 16px 10px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  color: #555;
}
</style>
