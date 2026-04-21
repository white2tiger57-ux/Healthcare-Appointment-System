import React, { useState, useEffect } from 'react';
import api from '../services/api';

const Profile: React.FC = () => {
  const [profile, setProfile] = useState<any>(null);
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState<any>({});

  useEffect(() => {
    api.get('/profile').then(r => { setProfile(r.data.profile); setForm(r.data.profile); });
  }, []);

  const handleSave = async () => {
    await api.put('/profile', { name: form.Name, mobile_number: form.Mobile_number || form.Contact, age: form.Age, gender: form.Gender, address: form.Address });
    setEditing(false);
    api.get('/profile').then(r => setProfile(r.data.profile));
  };

  const handlePhotoUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const fd = new FormData(); fd.append('photo', file);
    await api.post('/profile/photo', fd, { headers: { 'Content-Type': 'multipart/form-data' } });
    api.get('/profile').then(r => setProfile(r.data.profile));
  };

  if (!profile) return <div className="loading">Loading...</div>;

  return (
    <div>
      <div className="page-header" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <img src="/assets/patient-icon.png" alt="Profile" style={{ height: 44, width: 44, objectFit: 'contain' }} />
        <h1 style={{ margin: 0 }}>Profile</h1>
      </div>
      <div className="card" style={{ maxWidth: 600 }}>
        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <div style={{ width: 88, height: 88, borderRadius: '50%', background: 'linear-gradient(135deg, var(--color-primary), var(--color-accent))', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', overflow: 'hidden', border: '3px solid var(--color-border)' }}>
            <img src="/assets/patient-icon.png" alt="Avatar" style={{ width: 56, height: 56, objectFit: 'contain' }} />
          </div>
          <div style={{ marginTop: '0.5rem' }}><label className="btn btn-secondary" style={{ cursor: 'pointer', padding: '4px 12px', fontSize: '0.8rem' }}>
            Change Photo <input type="file" accept="image/*" onChange={handlePhotoUpload} style={{ display: 'none' }} />
          </label></div>
        </div>
        {editing ? (
          <div>
            <div className="form-group"><label>Name</label><input value={form.Name || ''} onChange={e => setForm({ ...form, Name: e.target.value })} /></div>
            <div className="form-group"><label>Phone</label><input value={form.Mobile_number || form.Contact || ''} onChange={e => setForm({ ...form, Mobile_number: e.target.value })} /></div>
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <button className="btn btn-primary" onClick={handleSave}>Save</button>
              <button className="btn btn-secondary" onClick={() => setEditing(false)}>Cancel</button>
            </div>
          </div>
        ) : (
          <div>
            {Object.entries(profile).filter(([k]) => k !== 'Photo').map(([k, v]) => (
              <div key={k} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid rgba(255,255,255,0.03)' }}>
                <span style={{ color: '#a8b2d1', fontSize: '0.85rem' }}>{k.replace(/_/g, ' ')}</span>
                <span>{String(v || '-')}</span>
              </div>
            ))}
            <button className="btn btn-primary" onClick={() => setEditing(true)} style={{ marginTop: '1rem' }}>Edit Profile</button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Profile;
