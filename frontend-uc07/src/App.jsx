import { useState } from 'react'
import { addEstimate, getLatest, getHistory } from './api'

export default function App() {
  const [storyId, setStoryId] = useState('')
  const [points, setPoints] = useState('')
  const [by, setBy] = useState('')
  const [latest, setLatest] = useState(null)
  const [history, setHistory] = useState([])
  const [msg, setMsg] = useState('')

  const submit = async (e) => {
    e.preventDefault()
    setMsg('')
    try {
      await addEstimate(storyId, { points: Number(points), estimatedBy: by })
      setMsg('Estimate saved ✅')
      await refresh()
    } catch (e) {
      setMsg('Failed to save estimate ❌')
    }
  }

  const refresh = async () => {
    setMsg('')
    try {
      const [l, h] = await Promise.all([getLatest(storyId), getHistory(storyId)])
      setLatest(l.data ?? null)
      setHistory(Array.isArray(h.data) ? h.data : [])
    } catch (e) {
      setMsg('Failed to fetch data ❌')
    }
  }

  return (
    <div style={{ maxWidth: 720, margin: '32px auto', fontFamily: 'system-ui' }}>
      <h1>UC-07: Estimate Story</h1>
      <form onSubmit={submit} style={{ display: 'grid', gap: 12 }}>
        <label>Story ID
          <input value={storyId} onChange={e=>setStoryId(e.target.value)} placeholder="e.g. 42" />
        </label>
        <label>Points
          <input value={points} onChange={e=>setPoints(e.target.value)} placeholder="1,2,3,5,8,13..." />
        </label>
        <label>Estimated By
          <input value={by} onChange={e=>setBy(e.target.value)} placeholder="your name" />
        </label>
        <div style={{ display: 'flex', gap: 8 }}>
          <button type="submit">Add Estimate</button>
          <button type="button" onClick={refresh}>Refresh</button>
        </div>
      </form>
      {msg && <p>{msg}</p>}

      <section style={{ marginTop: 24 }}>
        <h2>Latest</h2>
        {latest ? (
          <pre>{JSON.stringify(latest, null, 2)}</pre>
        ) : <p>No estimate yet.</p>}
      </section>

      <section style={{ marginTop: 24 }}>
        <h2>History</h2>
        {history.length ? (
          <ul>
            {history.map(e => (
              <li key={e.id}>
                <strong>{e.points}</strong> pts by <em>{e.estimatedBy}</em> at {e.estimatedAt ?? '(created on save)'}
              </li>
            ))}
          </ul>
        ) : <p>No history.</p>}
      </section>
    </div>
  )
}
