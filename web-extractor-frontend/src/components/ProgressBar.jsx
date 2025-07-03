
const ProgressBar = ({ progress }) => {
  return (
    <div className="progress-container">
      <div className="progress-bar" style={{ width: `${progress}%` }}>
        <div className="progress-text">{progress}%</div>
      </div>
    </div>
  );
};

export default ProgressBar;