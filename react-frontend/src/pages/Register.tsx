import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { User, Stethoscope, ArrowLeft } from 'lucide-react';
import api from '../services/api';

interface Department {
  id: number;
  name: string;
}

const Register: React.FC = () => {
  const [role, setRole] = useState<'patient' | 'doctor' | ''>('');
  const [form, setForm] = useState<Record<string, string>>({});
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [departments, setDepartments] = useState<Department[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    if (role === 'doctor') {
      api.get('/departments')
        .then(res => {
          const deptsArray = res.data.departments || [];
          const formatted = deptsArray.map((dept: any) => ({
            id: dept.Department_ID,
            name: dept.Name
          }));
          setDepartments(formatted);
        })
        .catch(err => console.error('Failed to load departments', err));
    }
  }, [role]);

  const update = (key: string, val: string) => setForm(prev => ({ ...prev, [key]: val }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      const endpoint = role === 'patient' ? '/auth/register/patient' : '/auth/register/doctor';
      let body;
      if (role === 'patient') {
        body = {
          name: form.name,
          mobileNumber: form.mobile,
          age: parseInt(form.age),
          gender: form.gender,
          email: form.email,
          password: form.password,
          confirmPassword: form.confirmPassword
        };
      } else {
        body = {
          name: form.name,
          contact: form.mobile,
          email: form.email,
          qualification: form.qualification,
          specialization: form.specialization,
          departmentId: parseInt(form.departmentId),
          location: form.location,
          availability: form.availability || 'Full-time',
          experience: parseInt(form.experience),
          password: form.password,
          confirmPassword: form.confirmPassword
        };
      }
      await api.post(endpoint, body);
      setSuccess('Registration successful! Redirecting to login...');
      setTimeout(() => navigate('/login'), 2000);
    } catch (err: any) {
      setError(err.response?.data?.error || err.response?.data?.details?.join(', ') || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  if (!role) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh' }}>
        <div className="card" style={{ maxWidth: 500, padding: '2.5rem', textAlign: 'center', width: '100%' }}>
          <img src="/assets/hospital-logo.png" alt="Healthcare" style={{ height: 48, marginBottom: '0.5rem' }} />
          <h1 style={{ marginBottom: '1rem', color: 'var(--color-primary)' }}>Create Account</h1>
          <p style={{ color: 'var(--color-text-secondary)', marginBottom: '2rem' }}>Choose your role to get started</p>
          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
            <button className="btn btn-primary" onClick={() => setRole('patient')} style={{ padding: '20px 40px', fontSize: '1rem', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
              <img src="/assets/patient-icon.png" alt="Patient" style={{ height: 44, width: 44, objectFit: 'contain' }} />
              Patient
            </button>
            <button className="btn btn-secondary" onClick={() => setRole('doctor')} style={{ padding: '20px 40px', fontSize: '1rem', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
              <img src="/assets/doctor-icon.png" alt="Doctor" style={{ height: 44, width: 44, objectFit: 'contain' }} />
              Doctor
            </button>
          </div>
          <p style={{ marginTop: '2rem', fontSize: '0.95rem', color: 'var(--color-text-secondary)' }}>
            Already have an account? <Link to="/login" style={{ color: 'var(--color-accent)', fontWeight: 600 }}>Sign In</Link>
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="animate-fade-in" style={{ display: 'flex', justifyContent: 'center', padding: '2rem 0' }}>
      <div className="card" style={{ width: '100%', maxWidth: 550, padding: '2.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.5rem' }}>
          <button onClick={() => setRole('')} style={{ background: 'none', border: 'none', color: 'var(--color-text-secondary)', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '4px' }}>
            <ArrowLeft size={18} />
            Back
          </button>
          <h1 style={{ margin: 0, color: 'var(--color-primary)' }}>Register as {role === 'patient' ? 'Patient' : 'Doctor'}</h1>
        </div>
        
        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="grid-2">
            <div className="form-group"><label>Full Name</label><input required onChange={e => update('name', e.target.value)} /></div>
            <div className="form-group"><label>Mobile</label><input required onChange={e => update('mobile', e.target.value)} /></div>
          </div>
          
          {role === 'patient' && (
            <div className="grid-2">
              <div className="form-group"><label>Age</label><input type="number" required onChange={e => update('age', e.target.value)} /></div>
              <div className="form-group"><label>Gender</label>
                <select required onChange={e => update('gender', e.target.value)}>
                  <option value="">Select</option>
                  <option>Male</option>
                  <option>Female</option>
                  <option>Other</option>
                </select>
              </div>
            </div>
          )}
          
          {role === 'doctor' && (
            <>
              <div className="grid-2">
                <div className="form-group"><label>Qualification</label><input required onChange={e => update('qualification', e.target.value)} /></div>
                <div className="form-group"><label>Specialization</label><input required onChange={e => update('specialization', e.target.value)} /></div>
              </div>
              <div className="grid-2">
                <div className="form-group">
                  <label>Department</label>
                  <select required onChange={e => update('departmentId', e.target.value)}>
                    <option value="">Select Department</option>
                    {departments.map(dept => (
                      <option key={dept.id} value={dept.id}>{dept.name}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group"><label>Experience (years)</label><input type="number" required onChange={e => update('experience', e.target.value)} /></div>
              </div>
              <div className="grid-2">
                <div className="form-group"><label>Location</label><input required onChange={e => update('location', e.target.value)} /></div>
                <div className="form-group">
                  <label>Availability</label>
                  <select required defaultValue="Full-time" onChange={e => update('availability', e.target.value)}>
                    <option value="Full-time">Full-time</option>
                    <option value="Part-time">Part-time</option>
                  </select>
                </div>
              </div>
            </>
          )}
          
          <div className="form-group"><label>Email</label><input type="email" required onChange={e => update('email', e.target.value)} /></div>
          <div className="grid-2">
            <div className="form-group"><label>Password</label><input type="password" required minLength={8} onChange={e => update('password', e.target.value)} /></div>
            <div className="form-group"><label>Confirm Password</label><input type="password" required onChange={e => update('confirmPassword', e.target.value)} /></div>
          </div>
          
          <button type="submit" className="btn btn-primary" disabled={loading} style={{ width: '100%', padding: '12px', marginTop: '1rem' }}>
            {loading ? 'Registering...' : 'Create Account'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default Register;