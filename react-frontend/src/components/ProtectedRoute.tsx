import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface Props {
  children: React.ReactNode;
  role?: 'patient' | 'doctor' | 'admin';
}

const ProtectedRoute: React.FC<Props> = ({ children, role }) => {
  const { isAuthenticated, user, loading } = useAuth();

  if (loading) return <div className="loading">Loading...</div>;
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (role && user?.userType !== role) return <Navigate to="/" replace />;

  return <>{children}</>;
};

export default ProtectedRoute;
