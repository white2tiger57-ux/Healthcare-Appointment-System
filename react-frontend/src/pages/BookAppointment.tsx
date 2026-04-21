import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Calendar as CalendarIcon, Clock, Stethoscope, Building, FileText } from 'lucide-react';
import api from '../services/api';
import { Department, Doctor, TimeSlot } from '../types';

const BookAppointment: React.FC = () => {
  const [departments, setDepartments] = useState<Department[]>([]);
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [slots, setSlots] = useState<TimeSlot[]>([]);
  const [deptId, setDeptId] = useState('');
  const [doctorId, setDoctorId] = useState('');
  const [date, setDate] = useState('');
  const [time, setTime] = useState('');
  const [serviceType, setServiceType] = useState('Consultation');
  const [notes, setNotes] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => { api.get('/departments').then(r => setDepartments(r.data.departments)); }, []);
  useEffect(() => { if (deptId) api.get(`/doctors?department_id=${deptId}`).then(r => setDoctors(r.data.doctors)); }, [deptId]);
  useEffect(() => {
    if (doctorId && date) {
      api.get(`/doctors/${doctorId}/availability?date=${date}`).then(r => setSlots(r.data.availableSlots || []));
    }
  }, [doctorId, date]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault(); setError(''); setLoading(true);
    try {
      await api.post('/appointments', { doctorId: parseInt(doctorId), date, time, serviceType, notes });
      navigate('/appointments');
    } catch (err: any) { setError(err.response?.data?.error || 'Booking failed'); }
    finally { setLoading(false); }
  };

  return (
    <div className="animate-fade-in">
      <div className="page-header" style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
        <CalendarIcon size={32} color="var(--color-primary)" />
        <div>
          <h1 style={{ margin: 0 }}>Book Appointment</h1>
          <p style={{ margin: 0 }}>Select a doctor and available time slot</p>
        </div>
      </div>
      <div className="card" style={{ maxWidth: 650, margin: '0 auto' }}>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="grid-2">
            <div className="form-group">
              <label style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Building size={16} /> Department
              </label>
              <select value={deptId} onChange={e => { setDeptId(e.target.value); setDoctorId(''); }} required>
                <option value="">Select department</option>
                {departments.map(d => <option key={d.Department_ID} value={d.Department_ID}>{d.Name}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Stethoscope size={16} /> Doctor
              </label>
              <select value={doctorId} onChange={e => setDoctorId(e.target.value)} required disabled={!deptId}>
                <option value="">Select doctor</option>
                {doctors.map(d => <option key={d.Doctor_ID} value={d.Doctor_ID}>{d.Name} — {d.Specialization}</option>)}
              </select>
            </div>
          </div>
          
          <div className="grid-2">
            <div className="form-group">
              <label style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <CalendarIcon size={16} /> Date
              </label>
              <input type="date" value={date} onChange={e => setDate(e.target.value)} min={new Date().toISOString().split('T')[0]} required />
            </div>
            <div className="form-group">
              <label style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Stethoscope size={16} /> Service Type
              </label>
              <select value={serviceType} onChange={e => setServiceType(e.target.value)}>
                <option>Consultation</option><option>Follow-up</option><option>Check-up</option><option>Emergency</option>
              </select>
            </div>
          </div>

          {slots.length > 0 && (
            <div className="form-group animate-slide-up">
              <label style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Clock size={16} /> Available Slots
              </label>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px', marginTop: '0.5rem' }}>
                {slots.map(s => (
                  <button key={s.time} type="button" disabled={!s.available}
                    className={`btn ${time === s.time ? 'btn-primary' : 'btn-secondary'}`}
                    style={{ padding: '8px 14px', fontSize: '0.85rem', opacity: s.available ? 1 : 0.4 }}
                    onClick={() => setTime(s.time)}>{s.display}</button>
                ))}
              </div>
            </div>
          )}

          <div className="form-group">
            <label style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <FileText size={16} /> Notes (optional)
            </label>
            <textarea rows={3} value={notes} onChange={e => setNotes(e.target.value)} placeholder="Any specific symptoms or reasons for the visit?" />
          </div>

          <button type="submit" className="btn btn-primary" disabled={loading || !time} style={{ width: '100%', padding: '14px', fontSize: '1.05rem', marginTop: '1rem' }}>
            {loading ? 'Processing...' : 'Confirm Booking'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default BookAppointment;
