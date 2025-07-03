import { useState } from 'react';
import FileItem from './FileItem';
import { FaFilter, FaSortAmountDown, FaSortAmountUp } from 'react-icons/fa';

const ResultView = ({ result, isProcessing }) => {
  const [filterType, setFilterType] = useState('ALL');
  const [sortOrder, setSortOrder] = useState('size-desc');
  const [previewUrl, setPreviewUrl] = useState(null);
  const [previewType, setPreviewType] = useState(null);

  if (!result || isProcessing) return null;
  
  if (!result.success) {
    return (
      <div className="result-container error">
        <h3>Extraction Failed</h3>
        <p>{result.message}</p>
      </div>
    );
  }

  // Áp dụng bộ lọc
  const filteredResources = result.resources.filter(resource => {
    if (filterType === 'ALL') return true;
    return resource.type === filterType;
  });

  // Áp dụng sắp xếp
  const sortedResources = [...filteredResources].sort((a, b) => {
    if (sortOrder === 'size-desc') return b.fileSize - a.fileSize;
    if (sortOrder === 'size-asc') return a.fileSize - b.fileSize;
    if (sortOrder === 'type') return a.type.localeCompare(b.type);
    return 0;
  });

  const handlePreview = (resource) => {
    setPreviewUrl(resource.savedPath);
    setPreviewType(resource.type);
  };

  const closePreview = () => {
    setPreviewUrl(null);
    setPreviewType(null);
  };

  const formatFileSize = (bytes) => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / 1048576).toFixed(1) + ' MB';
  };

  const getTypeCount = (type) => {
    return result.resources.filter(r => r.type === type).length;
  };

  return (
    <div className="result-container">
      <div className="result-header">
        <h3>Extracted {result.totalFiles} files</h3>
        
        <div className="controls">
          <div className="filter-control">
            <FaFilter className="icon" />
            <select 
              value={filterType} 
              onChange={(e) => setFilterType(e.target.value)}
            >
              <option value="ALL">All Types ({result.totalFiles})</option>
              <option value="IMAGE">Images ({getTypeCount('IMAGE')})</option>
              <option value="VIDEO">Videos ({getTypeCount('VIDEO')})</option>
              <option value="SOUND">Audio ({getTypeCount('SOUND')})</option>
              <option value="TEXT">Text ({getTypeCount('TEXT')})</option>
            </select>
          </div>
          
          <div className="sort-control">
            {sortOrder.includes('asc') 
              ? <FaSortAmountUp className="icon" /> 
              : <FaSortAmountDown className="icon" />}
            <select 
              value={sortOrder} 
              onChange={(e) => setSortOrder(e.target.value)}
            >
              <option value="size-desc">Size (Large first)</option>
              <option value="size-asc">Size (Small first)</option>
              <option value="type">Type</option>
            </select>
          </div>
        </div>
      </div>

      {sortedResources.length === 0 ? (
        <div className="no-results">
          No files found for the selected filter
        </div>
      ) : (
        <div className="file-grid">
          {sortedResources.map((file) => (
            <FileItem 
              key={file.filename} 
              file={file} 
              onPreview={handlePreview}
              formatFileSize={formatFileSize}
            />
          ))}
        </div>
      )}

      {previewUrl && (
        <div className="preview-modal">
          <div className="preview-content">
            <button className="close-btn" onClick={closePreview}>×</button>
            
            {previewType === 'IMAGE' ? (
              <img src={`file://${previewUrl}`} alt="Preview" />
            ) : previewType === 'TEXT' ? (
              <iframe 
                src={`file://${previewUrl}`} 
                title="Text Preview"
                className="text-preview"
              />
            ) : (
              <div className="unsupported-preview">
                <p>Preview not available for this file type</p>
                <p>File path: {previewUrl}</p>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default ResultView;