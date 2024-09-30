const express = require('express');
const cors = require('cors');
const app = express();

app.use(cors({
    origin: 'http://localhost:9200', // Địa chỉ của ứng dụng Vue.js
    methods: ['GET', 'POST', 'PUT', 'DELETE'],
    allowedHeaders: ['Content-Type', 'Authorization']
}));

app.use(express.json());

app.get('/api/demo', (req, res) => {
    res.json([{ id: 1, name: 'Lion' }, { id: 2, name: 'Tiger' }]);
});

const PORT = process.env.PORT || 8081;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
