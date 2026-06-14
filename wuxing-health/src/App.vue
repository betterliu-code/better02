<script setup>
import { computed, onMounted, ref } from 'vue'

const STORAGE_KEY = 'wuxing-health-state'
const DAY_MS = 24 * 60 * 60 * 1000

const sleepTime = ref(24)
const stress = ref(45)
const hadSnack = ref(false)
const hairCount = ref(100)
const earlySleepStreak = ref(0)
const lastSubmitDate = ref('')
const visibleSigns = ref([])
const diagnosis = ref(null)
const isManifesting = ref(false)
const fallingHairs = ref([])
const elixirFlash = ref(false)

const sleepLabel = computed(() => {
  if (sleepTime.value < 24) return `晚上 ${sleepTime.value}:00`
  if (sleepTime.value === 24) return '凌晨 0:00'
  return `凌晨 ${sleepTime.value - 24}:00`
})

const stressLabel = computed(() => {
  if (stress.value < 25) return '心如止水'
  if (stress.value < 55) return '略有波澜'
  if (stress.value < 80) return '眉头一紧'
  return '快要崩溃'
})

const hairStage = computed(() => {
  if (hairCount.value < 35) return 'sea'
  if (hairCount.value < 60) return 'thin'
  return 'full'
})

const hairStatus = computed(() => {
  if (hairCount.value < 35) return '地中海预警'
  if (hairCount.value < 60) return '发量稀疏'
  return '发量尚佳'
})

const reading = computed(() => {
  if (!lastSubmitDate.value) {
    return {
      title: '今日未起卦',
      lines: ['先填昨夜小账，脸会自己交代。'],
      tone: 'quiet',
    }
  }

  if (visibleSigns.value.length === 0) {
    return {
      title: '脸色暂且太平',
      lines: ['今晚继续早睡，别让毛囊临时开会。'],
      tone: 'good',
    }
  }

  const lineMap = {
    forehead: '额头冒火：昨晚的夜，被心火记账了。',
    leftCheek: '左脸发木：压力堆太久，肝气有点堵。',
    mouth: '嘴周有土：夜宵这笔，脾胃没签收。',
    hair: '发量扣款：毛囊今日扣了绩效。',
  }

  return {
    title: '今日卦象已出',
    lines: visibleSigns.value.map((id) => lineMap[id]).filter(Boolean),
    tone: 'warn',
  }
})

const dateKey = () => new Date().toISOString().slice(0, 10)

const daysBetween = (a, b) => {
  if (!a || !b) return 99
  return Math.round((new Date(b) - new Date(a)) / DAY_MS)
}

const saveState = () => {
  localStorage.setItem(
    STORAGE_KEY,
    JSON.stringify({
      hairCount: hairCount.value,
      earlySleepStreak: earlySleepStreak.value,
      lastSubmitDate: lastSubmitDate.value,
      visibleSigns: visibleSigns.value,
    }),
  )
}

const restoreState = () => {
  const raw = localStorage.getItem(STORAGE_KEY)
  if (!raw) return

  try {
    const saved = JSON.parse(raw)
    hairCount.value = Number.isFinite(saved.hairCount) ? saved.hairCount : 100
    earlySleepStreak.value = Number.isFinite(saved.earlySleepStreak) ? saved.earlySleepStreak : 0
    lastSubmitDate.value = saved.lastSubmitDate || ''
    visibleSigns.value = Array.isArray(saved.visibleSigns) ? saved.visibleSigns : []
  } catch {
    localStorage.removeItem(STORAGE_KEY)
  }
}

const signMap = {
  forehead: {
    id: 'forehead',
    label: '额头痘',
    title: '心火上炎',
    element: '火',
    organ: '心',
    text:
      '五行属火，心火旺盛！是不是昨晚跑代码报错、肝文献到凌晨？离火上炎，全发在额头上了。建议今天罚喝一杯菊花枸杞茶，或者闭目养神十分钟。',
  },
  leftCheek: {
    id: 'leftCheek',
    label: '左脸痘',
    title: '肝气郁结',
    element: '木',
    organ: '肝',
    text:
      '五行属木，肝气郁结！科研或学习压力再大也要深呼吸。木气不舒，气血不畅。放下奶茶，今晚去操场跑两圈散散肝火吧。',
  },
  mouth: {
    id: 'mouth',
    label: '嘴周痘',
    title: '脾胃抗议',
    element: '土',
    organ: '脾',
    text:
      '五行属土，脾胃抗议！重油重辣和深夜外卖让脾胃罢工了。土不生金，今天请乖乖吃点清淡的。',
  },
  hair: {
    id: 'hair',
    label: '发量',
    title: '肾水告急',
    element: '水',
    organ: '肾',
    text:
      '中医云“肾之华在发”。五行属水，熬夜伤肾水，水不涵木，你的毛囊已经失去滋养了！再不早睡，“聪明绝顶”指日可待！',
  },
}

const openDiagnosis = (id) => {
  diagnosis.value = signMap[id]
}

const playTone = (type) => {
  const AudioContext = window.AudioContext || window.webkitAudioContext
  if (!AudioContext) return

  const context = new AudioContext()
  const oscillator = context.createOscillator()
  const gain = context.createGain()

  oscillator.type = type === 'pop' ? 'triangle' : 'sine'
  oscillator.frequency.setValueAtTime(type === 'pop' ? 680 : 240, context.currentTime)
  oscillator.frequency.exponentialRampToValueAtTime(type === 'pop' ? 980 : 120, context.currentTime + 0.18)
  gain.gain.setValueAtTime(0.001, context.currentTime)
  gain.gain.exponentialRampToValueAtTime(type === 'pop' ? 0.18 : 0.12, context.currentTime + 0.03)
  gain.gain.exponentialRampToValueAtTime(0.001, context.currentTime + 0.22)

  oscillator.connect(gain)
  gain.connect(context.destination)
  oscillator.start()
  oscillator.stop(context.currentTime + 0.24)
}

const submitStatus = () => {
  const today = dateKey()
  const lateNight = sleepTime.value >= 25
  const highStress = stress.value >= 80
  const earlySleep = sleepTime.value <= 23
  const nextDay = daysBetween(lastSubmitDate.value, today) === 1
  const firstToday = lastSubmitDate.value !== today
  const nextSigns = []

  if (lateNight) nextSigns.push('forehead')
  if (highStress) nextSigns.push('leftCheek')
  if (hadSnack.value) nextSigns.push('mouth')

  if (firstToday) {
    if (earlySleep) {
      earlySleepStreak.value = nextDay ? earlySleepStreak.value + 1 : 1
    } else {
      earlySleepStreak.value = 0
    }

    if (lateNight || highStress) {
      hairCount.value = Math.max(0, hairCount.value - (lateNight && highStress ? 8 : 5))
      fallingHairs.value = [
        { id: Date.now(), left: 42, delay: 0 },
        { id: Date.now() + 1, left: 56, delay: 0.12 },
      ]
      nextSigns.push('hair')
      setTimeout(() => playTone('slide'), 220)
    }

    if (earlySleepStreak.value >= 2) {
      hairCount.value = Math.min(100, hairCount.value + 10)
      earlySleepStreak.value = 0
      elixirFlash.value = true
      nextSigns.length = 0
      setTimeout(() => {
        elixirFlash.value = false
      }, 1300)
    }

    lastSubmitDate.value = today
  }

  isManifesting.value = true
  visibleSigns.value = [...new Set(nextSigns)]
  playTone('pop')
  saveState()

  setTimeout(() => {
    isManifesting.value = false
  }, 550)
}

const resetPractice = () => {
  hairCount.value = 100
  earlySleepStreak.value = 0
  lastSubmitDate.value = ''
  visibleSigns.value = []
  fallingHairs.value = []
  diagnosis.value = null
  localStorage.removeItem(STORAGE_KEY)
}

onMounted(restoreState)
</script>

<template>
  <main class="app-shell">
    <section class="stage-panel" aria-label="五行养生局">
      <div class="title-row">
        <div>
          <p class="eyebrow">今日面诊 · 仅供自嘲</p>
          <h1>五行养生局</h1>
        </div>
        <div class="hair-meter" aria-label="当前发量">
          <span>发量余数 · {{ hairStatus }}</span>
          <strong>{{ hairCount }}</strong>
        </div>
      </div>

      <div class="canvas-wrap">
        <div class="watercolor-canvas" :class="{ manifesting: isManifesting }">
          <div class="aura aura-fire"></div>
          <div class="aura aura-wood"></div>
          <button class="hair hit-area" type="button" @click="openDiagnosis('hair')" :aria-label="signMap.hair.label">
            <span :class="['hair-shape', hairStage]"></span>
          </button>
          <div class="face">
            <button
              v-if="visibleSigns.includes('forehead')"
              class="pimple forehead"
              type="button"
              @click="openDiagnosis('forehead')"
              aria-label="点击查看额头痘诊断"
            ></button>
            <button
              v-if="visibleSigns.includes('leftCheek')"
              class="pimple left-cheek"
              type="button"
              @click="openDiagnosis('leftCheek')"
              aria-label="点击查看左脸痘诊断"
            ></button>
            <button
              v-if="visibleSigns.includes('mouth')"
              class="pimple mouth"
              type="button"
              @click="openDiagnosis('mouth')"
              aria-label="点击查看嘴周痘诊断"
            ></button>
            <span class="brow left"></span>
            <span class="brow right"></span>
            <span class="eye left"></span>
            <span class="eye right"></span>
            <span class="nose"></span>
            <span class="smile"></span>
            <span class="blush left"></span>
            <span class="blush right"></span>
          </div>
          <div class="neck"></div>
          <div class="robe"></div>
          <span
            v-for="hair in fallingHairs"
            :key="hair.id"
            class="falling-hair"
            :style="{ left: `${hair.left}%`, animationDelay: `${hair.delay}s` }"
          ></span>
          <div v-if="elixirFlash" class="elixir">赛博生发液 +10</div>
        </div>
      </div>
    </section>

    <aside class="control-panel" aria-label="状态输入">
      <div class="panel-head">
        <h2>昨夜小账</h2>
        <p>填完看脸色，不做医学诊断。</p>
      </div>

      <label class="field">
        <span>入睡时间</span>
        <strong>{{ sleepLabel }}</strong>
        <input v-model.number="sleepTime" type="range" min="22" max="27" step="1" />
      </label>

      <label class="field">
        <span>压力刻度</span>
        <strong>{{ stressLabel }}</strong>
        <input v-model.number="stress" type="range" min="0" max="100" step="1" />
      </label>

      <label class="snack-toggle">
        <input v-model="hadSnack" type="checkbox" />
        <span>昨晚吃了重油重辣夜宵</span>
      </label>

      <button class="submit-btn" type="button" @click="submitStatus">起一卦</button>
      <button class="ghost-btn" type="button" @click="resetPractice">洗脸重来</button>

      <section :class="['reading-card', reading.tone]" aria-label="今日卦象">
        <p>{{ reading.title }}</p>
        <ul>
          <li v-for="line in reading.lines" :key="line">{{ line }}</li>
        </ul>
      </section>

      <div class="rule-strip">
        <span><b>心火</b> 凌晨后睡，额头先替你发言</span>
        <span><b>肝木</b> 压力过线，左脸开始记仇</span>
        <span><b>肾水</b> 连续早睡，发量偷偷回一点</span>
      </div>
    </aside>

    <dialog :open="Boolean(diagnosis)" class="diagnosis-card" @click.self="diagnosis = null">
      <article v-if="diagnosis">
        <div class="card-top">
          <span>{{ diagnosis.element }}</span>
          <button type="button" @click="diagnosis = null" aria-label="关闭诊断">×</button>
        </div>
        <p class="card-kicker">{{ diagnosis.organ }} · {{ diagnosis.title }}</p>
        <h2>{{ diagnosis.label }}</h2>
        <p>{{ diagnosis.text }}</p>
      </article>
    </dialog>
  </main>
</template>
