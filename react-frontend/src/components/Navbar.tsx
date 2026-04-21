import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

const Navbar: React.FC = () => {
  const { isAuthenticated, user, logout, isDoctor } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!isAuthenticated) return null;

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/">🏥 HealthCare</Link>
      </div>
      <div className="navbar-links">
        <Link to={isDoctor ? '/doctor/dashboard' : '/dashboard'}>Dashboard</Link>
        <Link to="/appointments">Appointments</Link>
        <Link to="/medical-records">Records</Link>
        <Link to="/notifications">Notifications</Link>
        <Link to="/messages">Messages</Link>
        <Link to="/profile">Profile</Link>
      </div>
      <div className="navbar-user">
        <span className="user-type-badge">{user?.userType}</span>
        <button onClick={handleLogout} className="btn-logout">Logout</button>
      </div>
    </nav>
  );
};

export default Navbar;
