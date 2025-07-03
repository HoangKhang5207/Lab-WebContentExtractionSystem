import { 
  FaFileImage, 
  FaFileVideo, 
  FaFileAudio, 
  FaFileAlt,
  FaEye,
  FaExternalLinkAlt
} from 'react-icons/fa';

const FileItem = ({ file, onPreview, formatFileSize }) => {
  const getIcon = () => {
    switch(file.type) {
      case 'IMAGE': return <FaFileImage className="icon" />;
      case 'VIDEO': return <FaFileVideo className="icon" />;
      case 'SOUND': return <FaFileAudio className="icon" />;
      default: return <FaFileAlt className="icon" />;
    }
  };

  const getTypeName = () => {
    switch(file.type) {
      case 'IMAGE': return 'Image';
      case 'VIDEO': return 'Video';
      case 'SOUND': return 'Audio';
      case 'TEXT': return 'Text';
      default: return 'File';
    }
  };

  const getTypeClass = () => {
    return file.type.toLowerCase();
  };

  const handlePreview = (e) => {
    e.preventDefault();
    onPreview(file);
  };

  const handleOpen = (e) => {
    e.preventDefault();
    window.open(`file://${file.savedPath}`, '_blank');
  };

  return (
    <div className={`file-card ${getTypeClass()}`}>
      <div className="file-header">
        {getIcon()}
        <span className="file-type">{getTypeName()}</span>
        <span className="file-size">{formatFileSize(file.fileSize)}</span>
      </div>
      
      <div className="file-body">
        <div className="file-name" title={file.filename}>
          {file.filename}
        </div>
        
        <div className="file-actions">
          {file.type === 'IMAGE' || file.type === 'TEXT' ? (
            <button 
              className="btn-preview"
              onClick={handlePreview}
              title="Preview"
            >
              <FaEye />
            </button>
          ) : null}
          
          <button 
            className="btn-open"
            onClick={handleOpen}
            title="Open File"
          >
            <FaExternalLinkAlt />
          </button>
        </div>
      </div>
      
      <div className="file-footer">
        <a 
          href={file.originalUrl} 
          target="_blank" 
          rel="noopener noreferrer"
          className="original-url"
          title="Original URL"
        >
          Source
        </a>
      </div>
    </div>
  );
};

export default FileItem;