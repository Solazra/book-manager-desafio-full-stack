FROM node:20-alpine AS builder
WORKDIR /app

ARG BUILD_CONFIGURATION=docker
ARG API_URL=

COPY package*.json ./
RUN npm ci
COPY . .

RUN if [ -n "$API_URL" ]; then \
      sed -i "s|apiUrl:.*|apiUrl: '${API_URL}',|" src/environments/environment.prod.ts; \
    fi

RUN npm run build -- --configuration ${BUILD_CONFIGURATION}

FROM nginx:alpine

ARG NGINX_CONF=nginx.conf

COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html
COPY ${NGINX_CONF} /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
