import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(email, password);
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.error || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', minHeight: '85vh', gap: '2rem', alignItems: 'center', justifyContent: 'center', flexWrap: 'wrap' }}>
      {/* Hero Section */}
      <div className="login-hero animate-fade-in" style={{
        flex: '1 1 400px',
        maxWidth: 520,
        position: 'relative',
        borderRadius: 'var(--radius-card)',
        overflow: 'hidden',
        minHeight: 420,
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'flex-end',
      }}>
        <img
          src="/assets/hero-image.avif"
          alt="Healthcare"
          style={{
            position: 'absolute',
            inset: 0,
            width: '100%',
            height: '100%',
            objectFit: 'cover',
          }}
        />
        <div style={{
          position: 'relative',
          zIndex: 1,
          background: 'linear-gradient(to top, rgba(15, 76, 92, 0.92) 0%, rgba(15, 76, 92, 0.5) 60%, transparent 100%)',
          padding: '2.5rem 2rem 2rem',
        }}>
          <img src="/assets/logo.png" alt="Logo" style={{ height: 48, marginBottom: '1rem', filter: 'brightness(10)' }} />
          <h2 style={{ color: '#fff', fontFamily: 'var(--font-heading)', fontSize: '1.8rem', marginBottom: '0.5rem' }}>
            Your Health, Our Priority
          </h2>
          <p style={{ color: 'rgba(255,255,255,0.85)', fontSize: '0.95rem', lineHeight: 1.6 }}>
            Book appointments, track your health metrics, and connect with top healthcare professionals — all in one place.
          </p>
        </div>
      </div>

      {/* Login Form */}
      <div className="card animate-slide-up" style={{ flex: '1 1 360px', maxWidth: 420, padding: '2.5rem' }}>
        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <img src="/assets/hospital-logo.png" alt="Hospital" style={{ height: 48, marginBottom: '0.75rem' }} />
          <h1 style={{ fontSize: '1.6rem', marginBottom: '0.5rem', color: 'var(--color-primary)' }}>Welcome Back</h1>
          <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.9rem' }}>
            Sign in to your healthcare account
          </p>
        </div>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email</label>
            <input type="email" value={email} onChange={e => setEmail(e.target.value)} required placeholder="you@example.com" />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)} required placeholder="••••••••" />
          </div>
          <button type="submit" className="btn btn-primary" disabled={loading}
            style={{ width: '100%', padding: '12px', marginTop: '0.5rem' }}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
        <p style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.85rem', color: 'var(--color-text-secondary)' }}>
          Don't have an account? <Link to="/register" style={{ color: 'var(--color-accent)', fontWeight: 600 }}>Register</Link>
        </p>
      </div>
    </div>
  );
};

export default Login;
