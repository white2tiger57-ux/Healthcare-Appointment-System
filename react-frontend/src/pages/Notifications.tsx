import React, { useState, useEffect } from 'react';
import api from '../services/api';

const Notifications: React.FC = () => {
  const [notifications, setNotifications] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  const fetch = () => { api.get('/notifications').then(r => setNotifications(r.data.notifications)).catch(console.error).finally(() => setLoading(false)); };
  useEffect(() => { fetch(); }, []);

  const markRead = async (id: number) => { await api.put(`/notifications/${id}/read`); fetch(); };
  const deleteOne = async (id: number) => { await api.delete(`/notifications/${id}`); fetch(); };
  const deleteAll = async () => { if (window.confirm('Delete all?')) { await api.delete('/notifications'); fetch(); } };

  if (loading) return <div className="loading">Loading...</div>;

  return (
    <div>
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between' }}>
        <div><h1>Notifications</h1></div>
        {notifications.length > 0 && <button className="btn btn-danger" onClick={deleteAll}>Clear All</button>}
      </div>
      {notifications.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <img
            src="/assets/notification-page.png"
            alt="No notifications"
            style={{
              maxWidth: 280,
              width: '100%',
              height: 'auto',
              marginBottom: '1.5rem',
              opacity: 0.85,
              borderRadius: 'var(--radius-card)',
            }}
          />
          <p style={{ color: 'var(--color-text-secondary)', fontSize: '1.05rem', fontStyle: 'italic' }}>
            You're all caught up — no notifications right now.
          </p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
          {notifications.map((n: any) => (
            <div className="card" key={n.id} style={{ opacity: n.is_read ? 0.6 : 1, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div><strong>{n.title}</strong><p style={{ color: 'var(--color-text-secondary)', fontSize: '0.85rem', marginTop: '4px' }}>{n.message}</p>
                <span style={{ fontSize: '0.75rem', color: '#666' }}>{new Date(n.created_at).toLocaleString()}</span></div>
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                {!n.is_read && <button className="btn btn-secondary" style={{ padding: '4px 10px', fontSize: '0.8rem' }} onClick={() => markRead(n.id)}>✓</button>}
                <button className="btn btn-danger" style={{ padding: '4px 10px', fontSize: '0.8rem' }} onClick={() => deleteOne(n.id)}>✕</button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Notifications;
