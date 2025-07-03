import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL + '/api/extract';

// Hàm gửi yêu cầu trích xuất
export const extractContent = async (url) => {
  try {
    const response = await axios.post(API_URL, { url });
    return response.data;
  } catch (error) {
    console.error('Extraction failed:', error);
    return {
      success: false,
      message: error.response?.data?.message || 'Server error'
    };
  }
};

// Hàm kiểm tra kết nối backend
export const checkBackend = async () => {
  try {
    const response = await axios.get(API_URL);
    return response.status === 200;
  } catch (error) {
    return false;
  }
};