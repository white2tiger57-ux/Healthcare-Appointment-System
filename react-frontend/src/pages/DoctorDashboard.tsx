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
    <div className="animate-fade-in">
      {/* Header with doctor icon */}
      <div className="page-header" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <img src="/assets/doctor-icon.png" alt="Doctor" style={{ height: 52, width: 52, objectFit: 'contain' }} />
        <div>
          <h1 style={{ margin: 0 }}>Doctor Dashboard</h1>
          <p style={{ margin: 0 }}>Your practice overview for today</p>
        </div>
      </div>

      {/* Stats Row */}
      <div className="grid-4" style={{ marginBottom: '2rem' }}>
        <div className="card stat-card animate-slide-up" style={{ animationDelay: '0.1s' }}>
          <div className="stat-value">{data.todayAppointments.length}</div>
          <div className="stat-label">Today</div>
        </div>
        <div className="card stat-card animate-slide-up" style={{ animationDelay: '0.2s' }}>
          <div className="stat-value">{data.scheduledAppointments}</div>
          <div className="stat-label">Scheduled</div>
        </div>
        <div className="card stat-card animate-slide-up" style={{ animationDelay: '0.3s' }}>
          <div className="stat-value">{data.completedAppointments}</div>
          <div className="stat-label">Completed</div>
        </div>
        <div className="card stat-card animate-slide-up" style={{ animationDelay: '0.4s' }}>
          <div className="stat-value">{data.totalAppointments}</div>
          <div className="stat-label">Total</div>
        </div>
      </div>

      {/* Main content grid */}
      <div className="broken-grid animate-slide-up" style={{ animationDelay: '0.5s' }}>
        {/* Today's appointments */}
        <div className="card">
          <h3 style={{ marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <img src="/assets/hospital-logo.png" alt="" style={{ height: 22, width: 22, objectFit: 'contain' }} />
            Today's Appointments
          </h3>
          {data.todayAppointments.length === 0 ? (
            <p style={{ color: 'var(--color-text-secondary)', fontStyle: 'italic' }}>No appointments today</p>
          ) : (
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Patient</th><th>Time</th><th>Type</th><th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {data.todayAppointments.map((a: any, i) => (
                    <tr key={i}>
                      <td style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <img src="/assets/patient-icon.png" alt="" style={{ height: 24, width: 24, objectFit: 'contain', borderRadius: '50%' }} />
                        {a.patientName || a.Patient_Name}
                      </td>
                      <td>{a.appointmentTime || a.Appointment_Time}</td>
                      <td>{a.serviceType || a.Service_Type || '-'}</td>
                      <td>
                        <span className={`badge badge-${(a.status || a.Status || '').toLowerCase()}`}>
                          {a.status || a.Status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Sidebar decoration */}
        <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
          <img
            src="/assets/sidebar-image.png"
            alt="Healthcare illustration"
            style={{
              width: '100%',
              height: 'auto',
              maxHeight: 300,
              objectFit: 'cover',
              borderRadius: 'var(--radius-card)',
            }}
          />
          <div style={{ padding: '1.25rem' }}>
            <h4 style={{ color: 'var(--color-primary)', marginBottom: '0.5rem' }}>Quick Tip</h4>
            <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.9rem', lineHeight: 1.5 }}>
              Review your patient records before each appointment for a more personalized consultation.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DoctorDashboard;
