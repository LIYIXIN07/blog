<template>
  <canvas ref="canvasRef" class="particle-canvas" />
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'

const props = withDefaults(
  defineProps<{ parallaxX?: number; parallaxY?: number }>(),
  { parallaxX: 0, parallaxY: 0 }
)

const canvasRef = ref<HTMLCanvasElement | null>(null)

interface Particle {
  x: number
  y: number
  vx: number
  vy: number
  radius: number
}

let ctx: CanvasRenderingContext2D | null = null
let particles: Particle[] = []
let mouse = { x: -9999, y: -9999 }
let animationId = 0
let width = 0
let height = 0

const PARTICLE_COUNT = 68
const CONNECT_DIST = 140
const MOUSE_DIST = 160

function initParticles() {
  particles = Array.from({ length: PARTICLE_COUNT }, () => ({
    x: Math.random() * width,
    y: Math.random() * height,
    vx: (Math.random() - 0.5) * 0.35,
    vy: (Math.random() - 0.5) * 0.35,
    radius: Math.random() * 1.6 + 1,
  }))
}

function resize() {
  const canvas = canvasRef.value
  if (!canvas) return
  width = canvas.offsetWidth
  height = canvas.offsetHeight
  canvas.width = width * devicePixelRatio
  canvas.height = height * devicePixelRatio
  ctx = canvas.getContext('2d')
  if (ctx) {
    ctx.scale(devicePixelRatio, devicePixelRatio)
  }
  if (!particles.length) initParticles()
}

function draw() {
  if (!ctx) return
  const offsetX = props.parallaxX * 12
  const offsetY = props.parallaxY * 12

  ctx.clearRect(0, 0, width, height)

  for (const p of particles) {
    p.x += p.vx
    p.y += p.vy
    if (p.x < 0 || p.x > width) p.vx *= -1
    if (p.y < 0 || p.y > height) p.vy *= -1

    ctx.beginPath()
    ctx.arc(p.x + offsetX * 0.3, p.y + offsetY * 0.3, p.radius, 0, Math.PI * 2)
    ctx.fillStyle = 'rgba(186, 210, 255, 0.75)'
    ctx.fill()
  }

  for (let i = 0; i < particles.length; i++) {
    for (let j = i + 1; j < particles.length; j++) {
      const a = particles[i]
      const b = particles[j]
      const dx = a.x - b.x
      const dy = a.y - b.y
      const dist = Math.hypot(dx, dy)
      if (dist < CONNECT_DIST) {
        ctx.beginPath()
        ctx.moveTo(a.x + offsetX * 0.3, a.y + offsetY * 0.3)
        ctx.lineTo(b.x + offsetX * 0.3, b.y + offsetY * 0.3)
        ctx.strokeStyle = `rgba(147, 197, 253, ${0.22 * (1 - dist / CONNECT_DIST)})`
        ctx.lineWidth = 0.8
        ctx.stroke()
      }
    }
  }

  for (const p of particles) {
    const dx = p.x - mouse.x
    const dy = p.y - mouse.y
    const dist = Math.hypot(dx, dy)
    if (dist < MOUSE_DIST) {
      ctx.beginPath()
      ctx.moveTo(p.x + offsetX * 0.3, p.y + offsetY * 0.3)
      ctx.lineTo(mouse.x, mouse.y)
      ctx.strokeStyle = `rgba(199, 210, 254, ${0.45 * (1 - dist / MOUSE_DIST)})`
      ctx.lineWidth = 1
      ctx.stroke()
    }
  }

  animationId = requestAnimationFrame(draw)
}

function onMouseMove(e: MouseEvent) {
  const canvas = canvasRef.value
  if (!canvas) return
  const rect = canvas.getBoundingClientRect()
  mouse.x = e.clientX - rect.left
  mouse.y = e.clientY - rect.top
}

function onMouseLeave() {
  mouse.x = -9999
  mouse.y = -9999
}

onMounted(() => {
  resize()
  window.addEventListener('resize', resize)
  const canvas = canvasRef.value
  canvas?.addEventListener('mousemove', onMouseMove)
  canvas?.addEventListener('mouseleave', onMouseLeave)
  animationId = requestAnimationFrame(draw)
})

onUnmounted(() => {
  cancelAnimationFrame(animationId)
  window.removeEventListener('resize', resize)
  const canvas = canvasRef.value
  canvas?.removeEventListener('mousemove', onMouseMove)
  canvas?.removeEventListener('mouseleave', onMouseLeave)
})
</script>

<style scoped>
.particle-canvas {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  pointer-events: auto;
}
</style>
