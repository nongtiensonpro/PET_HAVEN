# Sử dụng hình ảnh Nginx chính thức
FROM nginx:alpine

# Sao chép tệp cấu hình Nginx vào hình ảnh
COPY nginx.conf /etc/nginx/nginx.conf

# Sao chép các tệp frontend đã build vào thư mục Nginx phục vụ
COPY /dist /usr/share/nginx/html

# Tạo thư mục cho các tệp log (tuỳ chọn)
RUN mkdir -p /var/log/nginx

# Mở cổng 80 để phục vụ ứng dụng
EXPOSE 80

# Khởi động Nginx
CMD ["nginx", "-g", "daemon off;"]
