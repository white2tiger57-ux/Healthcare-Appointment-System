import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { HealthMetric } from '../types';

const HealthMetrics: React.FC = () => {
  const [metrics, setMetrics] = useState<HealthMetric[]>([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ heart_rate: '', systolic: '', diastolic: '', temperature: '', notes: '' });
  const [error, setError] = useState('');

  const fetchMetrics = () => { api.get('/health-metrics').then(r => setMetrics(r.data.metrics || [])); };
  useEffect(() => { fetchMetrics(); }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault(); setError('');
    try {
      await api.post('/health-metrics', {
        heart_rate: parseInt(form.heart_rate), systolic: parseInt(form.systolic),
        diastolic: parseInt(form.diastolic), temperature: parseFloat(form.temperature), notes: form.notes
      });
      setShowForm(false); setForm({ heart_rate: '', systolic: '', diastolic: '', temperature: '', notes: '' }); fetchMetrics();
    } catch (err: any) { setError(err.response?.data?.error || 'Failed to save'); }
  };

  return (
    <div>
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between' }}>
        <div><h1>Health Metrics</h1><p>Track your vitals over time</p></div>
        <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}>+ Add Reading</button>
      </div>
      {showForm && (
        <div className="card" style={{ maxWidth: 500, marginBottom: '1.5rem' }}>
          {error && <div className="alert alert-error">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="grid-2">
              <div className="form-group"><label>Heart Rate (BPM)</label><input type="number" value={form.heart_rate} onChange={e => setForm({...form, heart_rate: e.target.value})} required /></div>
              <div className="form-group"><label>Temperature (°F)</label><input type="number" step="0.1" value={form.temperature} onChange={e => setForm({...form, temperature: e.target.value})} required /></div>
              <div className="form-group"><label>Systolic</label><input type="number" value={form.systolic} onChange={e => setForm({...form, systolic: e.target.value})} required /></div>
              <div className="form-group"><label>Diastolic</label><input type="number" value={form.diastolic} onChange={e => setForm({...form, diastolic: e.target.value})} required /></div>
            </div>
            <div className="form-group"><label>Notes</label><textarea value={form.notes} onChange={e => setForm({...form, notes: e.target.value})} /></div>
            <button type="submit" className="btn btn-primary">Save</button>
          </form>
        </div>
      )}
      {metrics.length === 0 ? <div className="card" style={{ textAlign: 'center', padding: '3rem' }}><p style={{ color: '#a8b2d1' }}>No metrics recorded</p></div> :
        <div className="table-container"><table><thead><tr><th>Date</th><th>Heart Rate</th><th>BP</th><th>Temp</th><th>Notes</th></tr></thead>
          <tbody>{metrics.map((m: any) => (
            <tr key={m.id}><td>{new Date(m.createdAt || m.created_at).toLocaleDateString()}</td><td>{m.heartRate || m.heart_rate} BPM</td>
            <td>{m.systolic}/{m.diastolic}</td><td>{m.temperature}°F</td><td>{m.notes || '-'}</td></tr>
          ))}</tbody></table></div>}
    </div>
  );
};

export default HealthMetrics;
