import React, { useState, useEffect } from 'react';
import { MessageSquare, Plus, X, Send } from 'lucide-react';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';

const Messages: React.FC = () => {
  const { user, isPatient } = useAuth();
  const [conversations, setConversations] = useState<any[]>([]);
  const [messages, setMessages] = useState<any[]>([]);
  const [activeConv, setActiveConv] = useState<number | null>(null);
  const [newMsg, setNewMsg] = useState('');
  
  // New Message Modal state
  const [showModal, setShowModal] = useState(false);
  const [availableDoctors, setAvailableDoctors] = useState<{id: number, name: string}[]>([]);
  const [selectedDoctorId, setSelectedDoctorId] = useState('');
  const [initialMsg, setInitialMsg] = useState('');
  const [loadingDoctors, setLoadingDoctors] = useState(false);

  const fetchConversations = () => {
    api.get('/messages/conversations').then(r => setConversations(r.data.conversations));
  };

  useEffect(() => { fetchConversations(); }, []);

  const openConversation = async (id: number) => {
    setActiveConv(id);
    const { data } = await api.get(`/messages/${id}`);
    setMessages(data.messages);
  };

  const sendMessage = async () => {
    if (!newMsg.trim() || !activeConv) return;
    const conv = conversations.find(c => c.conversation_id === activeConv);
    await api.post('/messages', { receiverId: conv?.other_user_id, content: newMsg });
    setNewMsg('');
    openConversation(activeConv);
    fetchConversations();
  };

  const handleStartNewConversation = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedDoctorId || !initialMsg.trim()) return;
    try {
      const { data } = await api.post('/messages', { receiverId: parseInt(selectedDoctorId), content: initialMsg });
      setShowModal(false);
      setInitialMsg('');
      setSelectedDoctorId('');
      fetchConversations();
      openConversation(data.conversationId);
    } catch (err) {
      console.error(err);
      alert('Failed to send message');
    }
  };

  const openNewMessageModal = () => {
    setShowModal(true);
    setLoadingDoctors(true);
    api.get('/messages/doctors-for-patient')
      .then(r => setAvailableDoctors(r.data))
      .catch(console.error)
      .finally(() => setLoadingDoctors(false));
  };

  return (
    <div className="animate-fade-in">
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <MessageSquare size={32} color="var(--color-primary)" />
          <div>
            <h1 style={{ margin: 0 }}>Messages</h1>
            <p style={{ margin: 0 }}>Chat with your {isPatient ? 'doctors' : 'patients'}</p>
          </div>
        </div>
        {isPatient && (
          <button className="btn btn-primary" onClick={openNewMessageModal}>
            <Plus size={18} /> New Message
          </button>
        )}
      </div>

      {showModal && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 }}>
          <div className="card animate-slide-up" style={{ width: '100%', maxWidth: 500, padding: '2rem', position: 'relative' }}>
            <button onClick={() => setShowModal(false)} style={{ position: 'absolute', top: '1rem', right: '1rem', background: 'none', border: 'none', cursor: 'pointer', color: 'var(--color-text-secondary)' }}>
              <X size={24} />
            </button>
            <h2 style={{ marginBottom: '1.5rem', color: 'var(--color-primary)' }}>Start New Conversation</h2>
            <form onSubmit={handleStartNewConversation}>
              <div className="form-group">
                <label>Select Doctor</label>
                {loadingDoctors ? (
                  <div className="skeleton skeleton-text" style={{ height: '42px', borderRadius: '6px' }}></div>
                ) : availableDoctors.length === 0 ? (
                  <div className="alert alert-error">You must have an appointment with a doctor before messaging them.</div>
                ) : (
                  <select value={selectedDoctorId} onChange={e => setSelectedDoctorId(e.target.value)} required>
                    <option value="">Select a doctor...</option>
                    {availableDoctors.map(d => (
                      <option key={d.id} value={d.id}>Dr. {d.name}</option>
                    ))}
                  </select>
                )}
              </div>
              <div className="form-group">
                <label>Message</label>
                <textarea rows={4} value={initialMsg} onChange={e => setInitialMsg(e.target.value)} required placeholder="Type your message here..." />
              </div>
              <button type="submit" className="btn btn-primary" disabled={loadingDoctors || availableDoctors.length === 0} style={{ width: '100%', marginTop: '1rem' }}>
                <Send size={18} /> Send Message
              </button>
            </form>
          </div>
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: '300px 1fr', gap: '1.5rem', minHeight: '60vh' }}>
        <div className="card" style={{ padding: '0', overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
          <div style={{ padding: '1rem', borderBottom: '1px solid var(--color-border)', background: '#f8fafc', fontWeight: 600 }}>
            Recent Conversations
          </div>
          <div style={{ overflow: 'auto', flex: 1 }}>
            {conversations.map(c => (
              <div key={c.conversation_id} onClick={() => openConversation(c.conversation_id)}
                style={{ padding: '1.25rem 1rem', cursor: 'pointer', borderBottom: '1px solid var(--color-border)',
                  background: activeConv === c.conversation_id ? '#f1f5f9' : 'transparent',
                  borderLeft: activeConv === c.conversation_id ? '4px solid var(--color-primary)' : '4px solid transparent',
                  transition: 'background 0.2s' }}>
                <div style={{ fontWeight: 600, color: 'var(--color-text-primary)', marginBottom: '4px' }}>
                  {c.other_user_type === 'doctor' ? 'Dr. ' : ''}{c.other_user_name}
                </div>
                <div style={{ fontSize: '0.8rem', color: 'var(--color-text-secondary)', textTransform: 'capitalize' }}>
                  {c.other_user_type}
                </div>
              </div>
            ))}
            {conversations.length === 0 && <p style={{ padding: '2rem 1rem', color: 'var(--color-text-secondary)', textAlign: 'center' }}>No conversations yet</p>}
          </div>
        </div>
        
        <div className="card" style={{ display: 'flex', flexDirection: 'column', padding: 0, overflow: 'hidden' }}>
          {!activeConv ? (
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', color: 'var(--color-text-secondary)', padding: '2rem' }}>
              <MessageSquare size={48} color="var(--color-border)" style={{ marginBottom: '1rem' }} />
              <p>Select a conversation from the sidebar to start messaging</p>
            </div>
          ) : (
            <>
              <div style={{ padding: '1rem', borderBottom: '1px solid var(--color-border)', background: '#f8fafc', fontWeight: 600 }}>
                {conversations.find(c => c.conversation_id === activeConv)?.other_user_type === 'doctor' ? 'Dr. ' : ''}
                {conversations.find(c => c.conversation_id === activeConv)?.other_user_name}
              </div>
              <div style={{ flex: 1, overflow: 'auto', padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1rem', background: '#fafafa' }}>
                {messages.map((m: any) => {
                  const isMine = m.senderId === user?.id || m.sender_id === user?.id;
                  return (
                    <div key={m.id || m.message_id} style={{ display: 'flex', justifyContent: isMine ? 'flex-end' : 'flex-start' }}>
                      <div style={{ 
                        maxWidth: '70%', 
                        padding: '10px 14px', 
                        borderRadius: isMine ? '12px 12px 0 12px' : '12px 12px 12px 0',
                        background: isMine ? 'var(--color-primary)' : 'white',
                        color: isMine ? 'white' : 'var(--color-text-primary)',
                        boxShadow: '0 1px 2px rgba(0,0,0,0.05)',
                        border: isMine ? 'none' : '1px solid var(--color-border)'
                      }}>
                        <div style={{ fontSize: '0.95rem', lineHeight: '1.4' }}>{m.content}</div>
                        <div style={{ fontSize: '0.7rem', color: isMine ? 'rgba(255,255,255,0.7)' : 'var(--color-text-secondary)', marginTop: '6px', textAlign: isMine ? 'right' : 'left' }}>
                          {new Date(m.createdAt || m.created_at).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
              <div style={{ padding: '1rem', borderTop: '1px solid var(--color-border)', background: 'white' }}>
                <div style={{ display: 'flex', gap: '0.75rem' }}>
                  <input 
                    style={{ flex: 1, padding: '12px 16px', background: '#f8fafc', border: '1px solid var(--color-border)', borderRadius: '24px', outline: 'none' }}
                    value={newMsg} onChange={e => setNewMsg(e.target.value)} placeholder="Type a message..."
                    onKeyDown={e => e.key === 'Enter' && sendMessage()} 
                  />
                  <button className="btn btn-primary" onClick={sendMessage} style={{ borderRadius: '24px', padding: '0 20px' }}>
                    <Send size={18} />
                  </button>
                </div>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default Messages;
