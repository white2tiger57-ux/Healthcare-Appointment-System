import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';

interface Prescription {
  id: number;
  patientId: number;
  patientName?: string;
  doctorId: number;
  doctorName?: string;
  diagnosis: string;
  medication: string;
  dosage: string;
  instructions: string;
  prescriptionDate: string;
  filePath?: string;
  status: string;
  createdAt: string;
}

const Prescriptions: React.FC = () => {
  const { user } = useAuth();
  const [prescriptions, setPrescriptions] = useState<Prescription[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchPrescriptions = async () => {
      try {
        const { data } = await api.get('/prescriptions');
        if (data.success) {
          setPrescriptions(data.prescriptions || data.data || []);
        } else {
          setPrescriptions([]);
        }
      } catch (err: any) {
        // If endpoint doesn't exist yet, show empty state gracefully
        if (err.response?.status === 404) {
          setPrescriptions([]);
        } else {
          setError(err.response?.data?.error || 'Failed to load prescriptions');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchPrescriptions();
  }, []);

  const downloadPrescription = (id: number) => {
    const token = localStorage.getItem('token');
    const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api';
    // Open download in a new tab with auth header via a temporary link
    window.open(`${baseUrl}/prescriptions/download/${id}?token=${token}`, '_blank');
  };

  if (loading) {
    return <div className="loading">Loading prescriptions...</div>;
  }

  const isDoctor = user?.userType === 'doctor';

  return (
    <div>
      {/* Page Header */}
      <div className="page-header">
        <h1>💊 Prescriptions</h1>
        <p>
          {isDoctor
            ? 'Manage prescriptions for your patients'
            : 'View your prescriptions and medication details'}
        </p>
      </div>

      {/* Error Alert */}
      {error && <div className="alert alert-error">{error}</div>}

      {/* Empty State */}
      {prescriptions.length === 0 && !error ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>💊</div>
          <h3 style={{ marginBottom: '0.5rem', color: '#e0e0e0' }}>No Prescriptions Yet</h3>
          <p style={{ color: '#a8b2d1', maxWidth: '400px', margin: '0 auto' }}>
            {isDoctor
              ? 'You haven\'t created any prescriptions yet. Prescriptions can be added from a patient\'s appointment.'
              : 'You don\'t have any prescriptions. Your doctor will add prescriptions after your appointment.'}
          </p>
        </div>
      ) : (
        /* Prescriptions List */
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {prescriptions.map((p, index) => (
            <div className="card" key={p.id || index}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                {/* Left: Prescription Details */}
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.75rem' }}>
                    <h3 style={{ margin: 0 }}>
                      {p.medication || p.diagnosis || `Prescription #${p.id || index + 1}`}
                    </h3>
                    <span
                      className={`badge ${
                        p.status === 'Active' || p.status === 'active'
                          ? 'badge-scheduled'
                          : p.status === 'Completed' || p.status === 'completed'
                          ? 'badge-completed'
                          : 'badge-cancelled'
                      }`}
                    >
                      {p.status || 'Active'}
                    </span>
                  </div>

                  <div className="grid-2" style={{ gap: '0.75rem' }}>
                    {p.diagnosis && (
                      <div>
                        <span style={{ color: '#a8b2d1', fontSize: '0.8rem' }}>Diagnosis</span>
                        <div style={{ fontSize: '0.95rem' }}>{p.diagnosis}</div>
                      </div>
                    )}
                    {p.medication && (
                      <div>
                        <span style={{ color: '#a8b2d1', fontSize: '0.8rem' }}>Medication</span>
                        <div style={{ fontSize: '0.95rem' }}>{p.medication}</div>
                      </div>
                    )}
                    {p.dosage && (
                      <div>
                        <span style={{ color: '#a8b2d1', fontSize: '0.8rem' }}>Dosage</span>
                        <div style={{ fontSize: '0.95rem' }}>{p.dosage}</div>
                      </div>
                    )}
                    {p.instructions && (
                      <div>
                        <span style={{ color: '#a8b2d1', fontSize: '0.8rem' }}>Instructions</span>
                        <div style={{ fontSize: '0.95rem' }}>{p.instructions}</div>
                      </div>
                    )}
                  </div>

                  <div style={{ marginTop: '0.75rem', display: 'flex', gap: '1.5rem', fontSize: '0.85rem', color: '#a8b2d1' }}>
                    {(p.doctorName || isDoctor) && (
                      <span>
                        👨‍⚕️ {isDoctor ? `Patient: ${p.patientName || 'N/A'}` : `Dr. ${p.doctorName}`}
                      </span>
                    )}
                    <span>
                      📅{' '}
                      {p.prescriptionDate || p.createdAt
                        ? new Date(p.prescriptionDate || p.createdAt).toLocaleDateString()
                        : 'N/A'}
                    </span>
                  </div>
                </div>

                {/* Right: Actions */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', marginLeft: '1rem' }}>
                  {p.filePath && (
                    <button
                      className="btn btn-primary"
                      style={{ padding: '6px 14px', fontSize: '0.85rem' }}
                      onClick={() => downloadPrescription(p.id)}
                    >
                      ⬇ Download PDF
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Prescriptions;
