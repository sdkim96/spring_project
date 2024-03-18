import { useState } from 'react';
const ProfileUpdateModal = ({ show, onClose, onSubmit }) => {
    const [preview, setPreview] = useState(null);

    const handleFileChange = (event) => {
        const file = event.target.files[0];
        if (file) {
            setPreview(URL.createObjectURL(file));
        } else {
            setPreview(null);
        }
    };

    if (!show) {
        return null;
    }

    return (
        <div className="modal-backdrop">
            <div className="modal-content">
                <form onSubmit={onSubmit}>
                    <label htmlFor="name">Name:</label>
                    <input type="text" id="name" name="name" required />
                    <label htmlFor="photo">Photo:</label>
                    <input type="file" id="photo" name="photo" onChange={handleFileChange} />
                    {preview && <img src={preview} alt="Preview" className="image-preview" />}
                    <div>
                        <button type="submit">Update</button>
                        <button type="button" onClick={onClose}>Cancel</button>
                    </div>
                </form>
            </div>
        </div>
    );
};


export default ProfileUpdateModal;