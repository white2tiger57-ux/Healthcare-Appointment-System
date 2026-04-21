import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import PatientDashboard from './pages/PatientDashboard';
import DoctorDashboard from './pages/DoctorDashboard';
import Appointments from './pages/Appointments';
import BookAppointment from './pages/BookAppointment';
import MedicalRecords from './pages/MedicalRecords';
import Notifications from './pages/Notifications';
import Messages from './pages/Messages';
import Profile from './pages/Profile';
import Feedback from './pages/Feedback';
import HealthMetrics from './pages/HealthMetrics';

function App() {
  const { isAuthenticated, isDoctor } = useAuth();

  return (
    <div className="app">
      <Navbar />
      <main className="main-content">
        <Routes>
          {/* Public */}
          <Route path="/login" element={isAuthenticated ? <Navigate to="/" /> : <Login />} />
          <Route path="/register" element={<Register />} />

          {/* Home redirect */}
          <Route path="/" element={
            isAuthenticated
              ? <Navigate to={isDoctor ? '/doctor/dashboard' : '/dashboard'} />
              : <Navigate to="/login" />
          } />

          {/* Patient */}
          <Route path="/dashboard" element={<ProtectedRoute role="patient"><PatientDashboard /></ProtectedRoute>} />

          {/* Doctor */}
          <Route path="/doctor/dashboard" element={<ProtectedRoute role="doctor"><DoctorDashboard /></ProtectedRoute>} />

          {/* Shared (authenticated) */}
          <Route path="/appointments" element={<ProtectedRoute><Appointments /></ProtectedRoute>} />
          <Route path="/book-appointment" element={<ProtectedRoute><BookAppointment /></ProtectedRoute>} />
          <Route path="/medical-records" element={<ProtectedRoute><MedicalRecords /></ProtectedRoute>} />
          <Route path="/health-metrics" element={<ProtectedRoute><HealthMetrics /></ProtectedRoute>} />
          <Route path="/notifications" element={<ProtectedRoute><Notifications /></ProtectedRoute>} />
          <Route path="/messages" element={<ProtectedRoute><Messages /></ProtectedRoute>} />
          <Route path="/feedback" element={<ProtectedRoute><Feedback /></ProtectedRoute>} />
          <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
        </Routes>
      </main>
    </div>
  );
}

export default App;
