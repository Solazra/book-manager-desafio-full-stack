# Railway / produção: SPA estático, API via URL pública do backend (CORS)
FROM node:20-alpine AS builder
WORKDIR /app

ARG API_URL=

COPY package*.json ./
RUN npm ci
COPY . .

RUN if [ -n "$API_URL" ]; then \
      sed -i "s|apiUrl:.*|apiUrl: '${API_URL}',|" src/environments/environment.prod.ts; \
    fi

RUN npm run build -- --configuration production

FROM nginx:alpine
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.spa.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
