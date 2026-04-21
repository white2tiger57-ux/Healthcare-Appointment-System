import React, { useState, useEffect } from 'react';
import { FileText, Upload, Download, Trash2, Calendar, User, User as UserIcon } from 'lucide-react';
import api from '../services/api';
import { MedicalRecord } from '../types';
import { useAuth } from '../context/AuthContext';

const MedicalRecords: React.FC = () => {
  const { isDoctor, user } = useAuth();
  const [records, setRecords] = useState<MedicalRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [showUpload, setShowUpload] = useState(false);
  const [file, setFile] = useState<File | null>(null);
  const [recordType, setRecordType] = useState('Lab Report');
  const [date, setDate] = useState('');
  const [description, setDescription] = useState('');
  
  // For doctors only
  const [patients, setPatients] = useState<{id: number, name: string}[]>([]);
  const [selectedPatientId, setSelectedPatientId] = useState('');
  const [loadingPatients, setLoadingPatients] = useState(false);

  const fetchRecords = () => {
    api.get('/medical-records')
      .then(r => setRecords(r.data.records))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchRecords();
    if (isDoctor && user?.relatedId) {
      setLoadingPatients(true);
      api.get(`/doctors/${user.relatedId}/patients`)
        .then(r => setPatients(r.data))
        .catch(console.error)
        .finally(() => setLoadingPatients(false));
    }
  }, [isDoctor, user?.relatedId]);

  const handleUpload = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) return;
    if (isDoctor && !selectedPatientId) {
      alert('Please select a patient first');
      return;
    }
    
    const formData = new FormData();
    formData.append('file', file);
    formData.append('recordType', recordType);
    formData.append('date', date);
    formData.append('description', description);
    if (isDoctor) {
      formData.append('patientId', selectedPatientId);
    }
    
    try {
      await api.post('/medical-records', formData, { headers: { 'Content-Type': 'multipart/form-data' } });
      setShowUpload(false); 
      setFile(null); 
      setDescription(''); 
      fetchRecords();
    } catch (err) { 
      console.error(err); 
      alert('Failed to upload record. Please try again.');
    }
  };

  const deleteRecord = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this record?')) return;
    try {
      await api.delete(`/medical-records/${id}`);
      fetchRecords();
    } catch (err) {
      console.error(err);
      alert('Failed to delete record');
    }
  };

  const downloadRecord = (id: number) => {
    window.open(`${api.defaults.baseURL}/medical-records/download/${id}`, '_blank');
  };

  return (
    <div className="animate-fade-in">
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <FileText size={32} color="var(--color-primary)" />
          <div>
            <h1 style={{ margin: 0 }}>Medical Records</h1>
            <p style={{ margin: 0 }}>Upload and manage your medical documents</p>
          </div>
        </div>
        <button className="btn btn-primary" onClick={() => setShowUpload(!showUpload)}>
          <Upload size={18} />
          {showUpload ? 'Cancel Upload' : 'Upload Record'}
        </button>
      </div>

      {showUpload && (
        <div className="card animate-slide-up" style={{ marginBottom: '2rem', maxWidth: 600 }}>
          <h3 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--color-primary)' }}>
            <Upload size={20} />
            Upload New Record
          </h3>
          <form onSubmit={handleUpload}>
            {isDoctor && (
              <div className="form-group">
                <label style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <UserIcon size={16} /> Select Patient
                </label>
                {loadingPatients ? (
                  <div className="skeleton skeleton-text" style={{ height: '42px', borderRadius: '6px' }}></div>
                ) : (
                  <select value={selectedPatientId} onChange={e => setSelectedPatientId(e.target.value)} required>
                    <option value="">Select a patient</option>
                    {patients.map(p => (
                      <option key={p.id} value={p.id}>{p.name}</option>
                    ))}
                  </select>
                )}
              </div>
            )}
            
            <div className="grid-2">
              <div className="form-group">
                <label>Record Type</label>
                <select value={recordType} onChange={e => setRecordType(e.target.value)}>
                  <option>Lab Report</option><option>Prescription</option><option>X-Ray</option><option>MRI</option><option>Other</option>
                </select>
              </div>
              <div className="form-group">
                <label>Date</label>
                <input type="date" value={date} onChange={e => setDate(e.target.value)} required />
              </div>
            </div>
            
            <div className="form-group">
              <label>Description</label>
              <textarea rows={2} value={description} onChange={e => setDescription(e.target.value)} required placeholder="Brief description of the document" />
            </div>
            <div className="form-group">
              <label>Document File</label>
              <input type="file" onChange={e => setFile(e.target.files?.[0] || null)} required accept=".pdf,.jpg,.jpeg,.png" style={{ border: '1px dashed var(--color-border)', padding: '1rem' }} />
            </div>
            
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }}>
              <Upload size={18} /> Upload Document
            </button>
          </form>
        </div>
      )}

      {loading ? (
        <div className="grid-2">
          {[1, 2, 3, 4].map(i => (
            <div key={i} className="card skeleton-card"></div>
          ))}
        </div>
      ) : records.length === 0 ? (
        <div className="card animate-fade-in" style={{ textAlign: 'center', padding: '4rem 2rem' }}>
          <FileText size={48} color="var(--color-border)" style={{ marginBottom: '1rem' }} />
          <h3 style={{ color: 'var(--color-text-secondary)', marginBottom: '0.5rem' }}>No medical records found</h3>
          <p style={{ color: '#a8b2d1' }}>Any uploaded documents will appear here.</p>
        </div>
      ) : (
        <div className="grid-2">
          {records.map(r => (
            <div className="card" key={r.record_id}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                <div>
                  <span className="badge badge-scheduled" style={{ marginBottom: '0.5rem', display: 'inline-block' }}>{r.record_type}</span>
                  <h4 style={{ margin: '0 0 0.75rem 0', color: 'var(--color-primary)' }}>{r.description}</h4>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
                    <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: '4px' }}>
                      <Calendar size={14} /> {r.record_date}
                    </p>
                    {r.doctor_name && (
                      <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: '4px' }}>
                        <User size={14} /> Dr. {r.doctor_name}
                      </p>
                    )}
                  </div>
                </div>
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <button className="btn btn-secondary" style={{ padding: '8px', borderRadius: '50%' }} onClick={() => downloadRecord(r.record_id)} title="Download">
                    <Download size={16} />
                  </button>
                  <button className="btn btn-secondary" style={{ padding: '8px', borderRadius: '50%', color: '#dc2626', borderColor: '#fee2e2' }} onClick={() => deleteRecord(r.record_id)} title="Delete">
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MedicalRecords;
