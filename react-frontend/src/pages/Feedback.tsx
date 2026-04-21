import React, { useState } from 'react';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';

const Feedback: React.FC = () => {
  const { user } = useAuth();
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');
  const [feedbackType, setFeedbackType] = useState('general');
  const [isAnonymous, setIsAnonymous] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault(); setError(''); setSuccess('');
    try {
      await api.post('/feedback', { patientId: user?.relatedId, rating, comment, feedbackType, isAnonymous });
      setSuccess('Feedback submitted successfully!'); setComment(''); setRating(5);
    } catch (err: any) { setError(err.response?.data?.error || 'Failed to submit feedback'); }
  };

  return (
    <div>
      <div className="page-header"><h1>Feedback</h1><p>Share your experience with us</p></div>
      <div className="card" style={{ maxWidth: 500 }}>
        {success && <div className="alert alert-success">{success}</div>}
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group"><label>Rating</label>
            <div style={{ display: 'flex', gap: '8px' }}>
              {[1,2,3,4,5].map(i => (
                <button key={i} type="button" onClick={() => setRating(i)}
                  style={{ fontSize: '1.5rem', background: 'none', border: 'none', cursor: 'pointer', opacity: i <= rating ? 1 : 0.3 }}>⭐</button>
              ))}
            </div>
          </div>
          <div className="form-group"><label>Type</label>
            <select value={feedbackType} onChange={e => setFeedbackType(e.target.value)}>
              <option value="general">General</option><option value="service">Service</option><option value="doctor">Doctor</option><option value="facility">Facility</option>
            </select></div>
          <div className="form-group"><label>Comment</label><textarea rows={4} value={comment} onChange={e => setComment(e.target.value)} required /></div>
          <div className="form-group"><label style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <input type="checkbox" checked={isAnonymous} onChange={e => setIsAnonymous(e.target.checked)} /> Submit anonymously
          </label></div>
          <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>Submit Feedback</button>
        </form>
      </div>
    </div>
  );
};

export default Feedback;
