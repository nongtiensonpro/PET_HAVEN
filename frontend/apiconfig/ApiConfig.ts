const API_BASE_URL = 'http://localhost:8080/api';
//khai báo các đường dấn dịch vụ theo yêu cầu
const API_ENDPOINTS = {
    dichVu: {
        getAll:`${API_BASE_URL}/dich-vu/all`
    }
};

export default { API_BASE_URL, API_ENDPOINTS };