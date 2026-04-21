import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { DoctorDashboardData } from '../types';

const DoctorDashboard: React.FC = () => {
  const [data, setData] = useState<DoctorDashboardData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/dashboard/doctor').then(res => { if (res.data.success) setData(res.data.dashboard); })
      .catch(console.error).finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading">Loading...</div>;
  if (!data) return <div className="alert alert-error">Failed to load dashboard</div>;

  return (
    <div>
      <div className="page-header"><h1>Doctor Dashboard</h1><p>Your practice overview for today</p></div>
      <div className="grid-4" style={{ marginBottom: '2rem' }}>
        <div className="card stat-card"><div className="stat-value">{data.todayAppointments.length}</div><div className="stat-label">Today</div></div>
        <div className="card stat-card"><div className="stat-value">{data.scheduledAppointments}</div><div className="stat-label">Scheduled</div></div>
        <div className="card stat-card"><div className="stat-value">{data.completedAppointments}</div><div className="stat-label">Completed</div></div>
        <div className="card stat-card"><div className="stat-value">{data.totalAppointments}</div><div className="stat-label">Total</div></div>
      </div>
      <div className="card">
        <h3 style={{ marginBottom: '1rem' }}>📅 Today's Appointments</h3>
        {data.todayAppointments.length === 0 ? <p style={{ color: '#a8b2d1' }}>No appointments today</p> :
          <div className="table-container"><table><thead><tr><th>Patient</th><th>Time</th><th>Type</th><th>Status</th></tr></thead>
          <tbody>{data.todayAppointments.map((a: any, i) => (
            <tr key={i}><td>{a.patientName || a.Patient_Name}</td><td>{a.appointmentTime || a.Appointment_Time}</td>
            <td>{a.serviceType || a.Service_Type || '-'}</td><td><span className={`badge badge-${(a.status || a.Status || '').toLowerCase()}`}>{a.status || a.Status}</span></td></tr>
          ))}</tbody></table></div>}
      </div>
    </div>
  );
};

export default DoctorDashboard;
