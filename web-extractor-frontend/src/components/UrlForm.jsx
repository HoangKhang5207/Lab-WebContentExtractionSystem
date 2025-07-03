import { useState, useEffect } from 'react';
import { FaDownload, FaSpinner } from 'react-icons/fa';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { checkBackend } from '../services/api';

const UrlForm = ({ onExtract, isProcessing }) => {
  const [url, setUrl] = useState('');
  const [backendConnected, setBackendConnected] = useState(false);
  const [isChecking, setIsChecking] = useState(true);

  useEffect(() => {
    // Kiểm tra kết nối backend khi component mount
    const checkConnection = async () => {
      try {
        const connected = await checkBackend();
        setBackendConnected(connected);
        if (!connected) {
          toast.error('Backend service is not available. Please start the backend server.');
        }
      } catch (error) {
        console.error('Connection check failed:', error);
      } finally {
        setIsChecking(false);
      }
    };
    
    checkConnection();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Kiểm tra URL hợp lệ
    if (!isValidUrl(url)) {
      toast.error('Please enter a valid URL starting with http:// or https://');
      return;
    }
    
    // Kiểm tra backend đã sẵn sàng chưa
    if (!backendConnected) {
      toast.error('Backend service is not available');
      return;
    }
    
    // Gọi hàm trích xuất
    await onExtract(url);
  };

  const isValidUrl = (url) => {
    try {
      new URL(url);
      return true;
    } catch (error) {
      return false;
    }
  };

  return (
    <div className="url-form-container">
      <ToastContainer position="top-right" autoClose={5000} />
      
      <div className={`connection-status ${backendConnected ? 'connected' : 'disconnected'}`}>
        {isChecking ? (
          <span>Checking backend connection...</span>
        ) : (
          <span>
            {backendConnected 
              ? 'Backend connected' 
              : 'Backend disconnected'}
          </span>
        )}
      </div>
      
      <form onSubmit={handleSubmit} className="url-form">
        <div className="input-group">
          <input
            type="text"
            value={url}
            onChange={(e) => setUrl(e.target.value)}
            placeholder="https://example.com"
            required
            disabled={isProcessing}
          />
          <button 
            type="submit" 
            disabled={isProcessing || !backendConnected}
            className="btn-extract"
          >
            {isProcessing ? (
              <>
                <FaSpinner className="spinner" /> Processing...
              </>
            ) : (
              <>
                <FaDownload /> Extract Content
              </>
            )}
          </button>
        </div>
      </form>
      
      <div className="examples">
        <p>Try these examples:</p>
        <div className="example-links">
          <button onClick={() => setUrl('https://en.wikipedia.org/wiki/Web_scraping')}>
            Wikipedia
          </button>
          <button onClick={() => setUrl('https://www.bbc.com/news')}>
            BBC News
          </button>
          <button onClick={() => setUrl('https://unsplash.com/')}>
            Unsplash
          </button>
        </div>
      </div>
    </div>
  );
};

export default UrlForm;