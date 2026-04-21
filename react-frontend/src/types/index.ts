export interface User {
  id: number;
  email: string;
  userType: 'patient' | 'doctor' | 'admin';
  relatedId: number;
  token: string;
}

export interface AuthResponse {
  success: boolean;
  token: string;
  userType: string;
  userId: number;
  relatedId: number;
  expiresIn: number;
}

export interface Appointment {
  id: number;
  patientId: number;
  patientName: string;
  doctorId: number;
  doctorName: string;
  specialization: string;
  departmentName: string;
  appointmentDate: string;
  appointmentTime: string;
  serviceType: string;
  notes: string;
  status: string;
  createdAt: string;
}

export interface Doctor {
  Doctor_ID: number;
  Name: string;
  Specialization: string;
  Qualification: string;
  Experience: number;
  Contact: string;
  Department_Name?: string;
}

export interface Department {
  Department_ID: number;
  Name: string;
}

export interface TimeSlot {
  time: string;
  display: string;
  available: boolean;
}

export interface MedicalRecord {
  record_id: number;
  record_type: string;
  description: string;
  file_path: string;
  record_date: string;
  created_at: string;
  doctor_name: string | null;
}

export interface HealthMetric {
  id: number;
  heartRate: number;
  systolic: number;
  diastolic: number;
  temperature: number;
  notes: string;
  createdAt: string;
}

export interface Notification {
  id: number;
  title: string;
  message: string;
  category: string;
  is_read: boolean;
  is_urgent: boolean;
  created_at: string;
  action_url: string;
}

export interface ConversationSummary {
  conversation_id: number;
  other_user_id: number;
  other_user_name: string;
  other_user_type: string;
  last_message_at: string;
}

export interface Message {
  id: number;
  senderId: number;
  receiverId: number;
  content: string;
  isRead: boolean;
  createdAt: string;
}

export interface PatientDashboardData {
  upcomingAppointments: Appointment[];
  recentRecords: MedicalRecord[];
  latestHealthMetric: HealthMetric | null;
  unreadNotifications: number;
  totalAppointments: number;
  totalRecords: number;
}

export interface DoctorDashboardData {
  todayAppointments: Appointment[];
  totalAppointments: number;
  completedAppointments: number;
  scheduledAppointments: number;
  unreadNotifications: number;
}
