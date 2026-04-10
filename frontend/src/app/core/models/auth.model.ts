export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface OtpRequest {
  otp: string
  email: string
}

export interface JwtAuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType?: string;
}
