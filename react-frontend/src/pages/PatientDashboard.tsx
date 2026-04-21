import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import { 
  Calendar, 
  FileText, 
  Bell, 
  Activity, 
  HeartPulse, 
  Thermometer, 
  Clock, 
  Pill, 
  MessageCircle, 
  Star, 
  ArrowRight,
  AlertCircle
} from 'lucide-react';

interface DashboardData {
  upcomingAppointments: any[];
  recentRecords: any[];
  latestHealthMetric: any | null;
  unreadNotifications: number;
  totalAppointments: number;
  totalRecords: number;
}

const DashboardSkeleton = () => (
  <div className="animate-fade-in">
    <div className="page-header">
      <div className="skeleton skeleton-title" style={{ width: '300px' }}></div>
      <div className="skeleton skeleton-text" style={{ width: '400px' }}></div>
    </div>
    <div className="grid-4" style={{ marginBottom: '2.5rem' }}>
      {[...Array(4)].map((_, i) => (
        <div key={i} className="card stat-card">
          <div className="skeleton skeleton-title" style={{ width: '60px', height: '2.5rem' }}></div>
          <div className="skeleton skeleton-text" style={{ width: '100px' }}></div>
        </div>
      ))}
    </div>
    <div className="broken-grid">
      <div className="card">
        <div className="skeleton skeleton-title"></div>
        <div className="skeleton skeleton-text"></div>
        <div className="skeleton skeleton-text"></div>
      </div>
      <div className="card">
        <div className="skeleton skeleton-title"></div>
        <div className="skeleton skeleton-text"></div>
      </div>
    </div>
  </div>
);

const PatientDashboard: React.FC = () => {
  const [data, setData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const { data: res } = await api.get('/dashboard/patient');
        if (res.success) {
          setData(res.dashboard);
          return;
        }
      } catch {
        try {
          const [apptRes, recordsRes, metricsRes, notifRes] = await Promise.allSettled([
            api.get('/appointments?filter=upcoming'),
            api.get('/medical-records'),
            api.get('/health-metrics/latest'),
            api.get('/notifications?unread=true'),
          ]);

          setData({
            upcomingAppointments:
              apptRes.status === 'fulfilled' ? (apptRes.value.data.appointments || []) : [],
            recentRecords:
              recordsRes.status === 'fulfilled' ? (recordsRes.value.data.records || []).slice(0, 5) : [],
            latestHealthMetric:
              metricsRes.status === 'fulfilled' ? (metricsRes.value.data.metrics || null) : null,
            unreadNotifications:
              notifRes.status === 'fulfilled' ? (notifRes.value.data.notifications?.length || 0) : 0,
            totalAppointments: 0,
            totalRecords: 0,
          });
        } catch (fallbackErr) {
          setError('Failed to load your dashboard data. Please try again later.');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, []);

  if (loading) {
    return <DashboardSkeleton />;
  }

  if (error || !data) {
    return (
      <div className="alert alert-error animate-fade-in">
        <AlertCircle size={20} />
        {error || 'Unable to load your dashboard. We are working on it.'}
      </div>
    );
  }

  return (
    <div className="animate-fade-in">
      {/* Header */}
      <div className="page-header">
        <h1>Patient Dashboard</h1>
        <p>Welcome back. Here is a summary of your health and upcoming care.</p>
      </div>

      {/* Stats Row */}
      <div className="grid-4" style={{ marginBottom: '2.5rem' }}>
        <div className="card stat-card animate-slide-up" style={{ animationDelay: '0.1s' }}>
          <div className="stat-value">{data.totalAppointments || data.upcomingAppointments.length}</div>
          <div className="stat-label" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--color-primary)' }}>
            <Calendar size={16} /> Appointments
          </div>
        </div>
        <div className="card stat-card animate-slide-up" style={{ animationDelay: '0.2s' }}>
          <div className="stat-value">{data.upcomingAppointments.length}</div>
          <div className="stat-label" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--color-primary)' }}>
            <Clock size={16} /> Upcoming
          </div>
        </div>
        <div className="card stat-card animate-slide-up" style={{ animationDelay: '0.3s' }}>
          <div className="stat-value">{data.totalRecords || data.recentRecords.length}</div>
          <div className="stat-label" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--color-primary)' }}>
            <FileText size={16} /> Medical Records
          </div>
        </div>
        <div className="card stat-card animate-slide-up" style={{ animationDelay: '0.4s' }}>
          <div className="stat-value">{data.unreadNotifications}</div>
          <div className="stat-label" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--color-primary)' }}>
            <Bell size={16} /> Notifications
          </div>
        </div>
      </div>

      {/* Main Content Grid */}
      <div className="broken-grid animate-slide-up" style={{ animationDelay: '0.5s' }}>
        {/* Left Column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          
          {/* Upcoming Appointments Card */}
          <div className="card">
            <h3 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Calendar size={20} color="var(--color-primary)" /> Upcoming Appointments
            </h3>
            {data.upcomingAppointments.length === 0 ? (
              <p style={{ color: 'var(--color-text-secondary)', fontStyle: 'italic' }}>
                You have no upcoming appointments scheduled.
              </p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {data.upcomingAppointments.slice(0, 5).map((a: any, index: number) => (
                  <div
                    key={a.id || a.Appointment_ID || index}
                    style={{
                      padding: '1rem',
                      borderRadius: 'var(--radius-card)',
                      backgroundColor: 'var(--color-background)',
                      border: '1px solid var(--color-border)',
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center'
                    }}
                  >
                    <div>
                      <div style={{ fontWeight: 600, color: 'var(--color-text-primary)' }}>
                        {a.doctorName || a.Doctor_Name || 'Doctor'}
                      </div>
                      <div style={{ fontSize: '0.9rem', color: 'var(--color-text-secondary)', marginTop: '4px' }}>
                        {a.appointmentDate || a.Appointment_Date} at {a.appointmentTime || a.Appointment_Time}
                        {(a.serviceType || a.Service_Type) && ` · ${a.serviceType || a.Service_Type}`}
                      </div>
                    </div>
                    <span className={`badge badge-${(a.status || a.Status || 'scheduled').toLowerCase()}`}>
                      {a.status || a.Status}
                    </span>
                  </div>
                ))}
              </div>
            )}
            <Link
              to="/appointments"
              className="btn btn-secondary"
              style={{ marginTop: '1.5rem' }}
            >
              View all appointments <ArrowRight size={16} />
            </Link>
          </div>

          {/* Recent Medical Records */}
          <div className="card">
            <h3 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <FileText size={20} color="var(--color-primary)" /> Recent Medical Records
            </h3>
            {data.recentRecords.length === 0 ? (
              <p style={{ color: 'var(--color-text-secondary)', fontStyle: 'italic' }}>
                No recent medical records found.
              </p>
            ) : (
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Type</th>
                      <th>Description</th>
                      <th>Date</th>
                      <th>Doctor</th>
                    </tr>
                  </thead>
                  <tbody>
                    {data.recentRecords.map((r: any, index: number) => (
                      <tr key={r.record_id || r.Record_ID || index}>
                        <td>
                          <span className="badge badge-scheduled" style={{ backgroundColor: 'var(--color-background)', color: 'var(--color-text-primary)', border: '1px solid var(--color-border)' }}>
                            {r.record_type || r.Record_Type || 'Record'}
                          </span>
                        </td>
                        <td>{r.description || r.Description || '—'}</td>
                        <td>{r.record_date || r.Record_Date || '—'}</td>
                        <td>{r.doctor_name || r.Doctor_Name || '—'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
            <Link
              to="/medical-records"
              className="btn btn-secondary"
              style={{ marginTop: '1.5rem' }}
            >
              View medical history <ArrowRight size={16} />
            </Link>
          </div>

        </div>

        {/* Right Column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          
          {/* Health Metrics Card */}
          <div className="card">
            <h3 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Activity size={20} color="var(--color-primary)" /> Recent Vitals
            </h3>
            {data.latestHealthMetric ? (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                  <div style={{ padding: '0.75rem', backgroundColor: '#fee2e2', borderRadius: '50%', color: '#dc2626' }}>
                    <HeartPulse size={24} />
                  </div>
                  <div>
                    <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.85rem', fontWeight: 500 }}>Heart Rate</div>
                    <div style={{ fontSize: '1.25rem', fontWeight: 600, color: 'var(--color-text-primary)' }}>
                      {data.latestHealthMetric.heartRate || data.latestHealthMetric.heart_rate || '—'} <span style={{ fontSize: '0.9rem', color: 'var(--color-text-secondary)', fontWeight: 400 }}>BPM</span>
                    </div>
                  </div>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                  <div style={{ padding: '0.75rem', backgroundColor: '#e0f2fe', borderRadius: '50%', color: '#0284c7' }}>
                    <Activity size={24} />
                  </div>
                  <div>
                    <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.85rem', fontWeight: 500 }}>Blood Pressure</div>
                    <div style={{ fontSize: '1.25rem', fontWeight: 600, color: 'var(--color-text-primary)' }}>
                      {data.latestHealthMetric.systolic || '—'}/{data.latestHealthMetric.diastolic || '—'} <span style={{ fontSize: '0.9rem', color: 'var(--color-text-secondary)', fontWeight: 400 }}>mmHg</span>
                    </div>
                  </div>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                  <div style={{ padding: '0.75rem', backgroundColor: '#fef3c7', borderRadius: '50%', color: '#d97706' }}>
                    <Thermometer size={24} />
                  </div>
                  <div>
                    <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.85rem', fontWeight: 500 }}>Temperature</div>
                    <div style={{ fontSize: '1.25rem', fontWeight: 600, color: 'var(--color-text-primary)' }}>
                      {data.latestHealthMetric.temperature || '—'} <span style={{ fontSize: '0.9rem', color: 'var(--color-text-secondary)', fontWeight: 400 }}>°F</span>
                    </div>
                  </div>
                </div>

                <div style={{ paddingTop: '1rem', borderTop: '1px solid var(--color-border)', fontSize: '0.85rem', color: 'var(--color-text-secondary)' }}>
                  Recorded on {data.latestHealthMetric.createdAt || data.latestHealthMetric.created_at
                      ? new Date(data.latestHealthMetric.createdAt || data.latestHealthMetric.created_at).toLocaleDateString()
                      : '—'}
                </div>
              </div>
            ) : (
              <p style={{ color: 'var(--color-text-secondary)', fontStyle: 'italic' }}>
                No recent vitals recorded.
              </p>
            )}
            <Link
              to="/health-metrics"
              className="btn btn-secondary"
              style={{ marginTop: '1.5rem', width: '100%' }}
            >
              Log new vitals <ArrowRight size={16} />
            </Link>
          </div>

          {/* Quick Actions */}
          <div className="card">
            <h3 style={{ marginBottom: '1.5rem' }}>Quick Actions</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              <Link to="/book-appointment" className="btn btn-primary" style={{ justifyContent: 'flex-start' }}>
                <Calendar size={18} /> Book Appointment
              </Link>
              <Link to="/prescriptions" className="btn btn-secondary" style={{ justifyContent: 'flex-start' }}>
                <Pill size={18} /> My Prescriptions
              </Link>
              <Link to="/messages" className="btn btn-secondary" style={{ justifyContent: 'flex-start' }}>
                <MessageCircle size={18} /> Message Doctor
              </Link>
              <Link to="/feedback" className="btn btn-secondary" style={{ justifyContent: 'flex-start' }}>
                <Star size={18} /> Give Feedback
              </Link>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};

export default PatientDashboard;
