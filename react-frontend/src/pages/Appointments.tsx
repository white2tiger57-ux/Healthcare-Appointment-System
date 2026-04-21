import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import { Appointment } from '../types';
import { useAuth } from '../context/AuthContext';

const Appointments: React.FC = () => {
  const { user } = useAuth();
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [filter, setFilter] = useState('');
  const [loading, setLoading] = useState(true);

  const fetchAppointments = async () => {
    setLoading(true);
    try {
      const params = filter ? `?filter=${filter}` : '';
      const { data } = await api.get(`/appointments${params}`);
      if (data.success) setAppointments(data.appointments);
    } catch (err) { console.error(err); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchAppointments(); }, [filter]);

  const cancelAppointment = async (id: number) => {
    if (!window.confirm('Cancel this appointment?')) return;
    try {
      await api.put(`/appointments/${id}/cancel`);
      fetchAppointments();
    } catch (err) { console.error(err); }
  };

  return (
    <div>
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div><h1>Appointments</h1><p>Manage your appointments</p></div>
        <Link to="/book-appointment" className="btn btn-primary">+ Book New</Link>
      </div>
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem' }}>
        {['', 'upcoming', 'past'].map(f => (
          <button key={f} className={`btn ${filter === f ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setFilter(f)}>{f || 'All'}</button>
        ))}
      </div>
      {loading ? <div className="loading">Loading...</div> :
        appointments.length === 0 ? <div className="card" style={{ textAlign: 'center', padding: '3rem' }}><p style={{ color: '#a8b2d1' }}>No appointments found</p></div> :
          <div className="table-container"><table><thead><tr><th>Doctor/Patient</th><th>Date</th><th>Time</th><th>Type</th><th>Status</th><th>Actions</th></tr></thead>
            <tbody>{appointments.map(a => (
              <tr key={a.id}><td style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <img src={user?.userType === 'doctor' ? '/assets/patient-icon.png' : '/assets/doctor-icon.png'} alt="" style={{ height: 24, width: 24, objectFit: 'contain', borderRadius: '50%' }} />
                {user?.userType === 'doctor' ? a.patientName : a.doctorName}
              </td><td>{a.appointmentDate}</td><td>{a.appointmentTime}</td>
                <td>{a.serviceType || '-'}</td><td><span className={`badge badge-${a.status.toLowerCase()}`}>{a.status}</span></td>
                <td>{a.status === 'Scheduled' && <button className="btn btn-danger" style={{ padding: '4px 10px', fontSize: '0.8rem' }} onClick={() => cancelAppointment(a.id)}>Cancel</button>}</td></tr>
            ))}</tbody></table></div>}
    </div>
  );
};

export default Appointments;
