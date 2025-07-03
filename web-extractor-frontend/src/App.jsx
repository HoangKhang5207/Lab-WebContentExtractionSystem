import { useState } from 'react';
import UrlForm from './components/UrlForm';
import ResultView from './components/ResultView';
import ProgressBar from './components/ProgressBar';
import { extractContent } from './services/api';
import './assets/styles/main.css';
import './assets/styles/components.css';

function App() {
  const [result, setResult] = useState(null);
  const [isProcessing, setIsProcessing] = useState(false);
  const [progress, setProgress] = useState(0);
  
  const handleExtract = async (url) => {
    try {
      // Reset state
      setResult(null);
      setIsProcessing(true);
      setProgress(0);
      
      // Mô phỏng tiến trình (trong thực tế có thể dùng WebSocket hoặc polling)
      const interval = setInterval(() => {
        setProgress(prev => {
          const newProgress = prev + 5;
          return newProgress > 95 ? 95 : newProgress;
        });
      }, 300);
      
      // Gọi API backend
      const extractionResult = await extractContent(url);
      
      // Cập nhật kết quả
      setResult(extractionResult);
      setProgress(100);
      
      // Dừng mô phỏng tiến trình
      clearInterval(interval);
      
      // Tự động reset tiến trình sau 2s
      setTimeout(() => {
        setProgress(0);
      }, 2000);
    } catch (error) {
      console.error('Extraction error:', error);
      setResult({
        success: false,
        message: 'An unexpected error occurred'
      });
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <div className="app-container">
      <header className="app-header">
        <h1>Web Content Extractor</h1>
        <p>Extract images, videos, audio and text from any website</p>
      </header>
      
      <main className="app-main">
        <UrlForm onExtract={handleExtract} isProcessing={isProcessing} />
        
        {isProcessing && (
          <div className="processing-container">
            <ProgressBar progress={progress} />
            <p>Extracting content from website...</p>
          </div>
        )}
        
        <ResultView result={result} isProcessing={isProcessing} />
      </main>
      
      <footer className="app-footer">
        <p>© {new Date().getFullYear()} Web Content Extractor | Spring Boot + ReactJS</p>
      </footer>
    </div>
  );
}

export default App;