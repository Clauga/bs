# Build stage
#FROM node:18 as build-stage
#
#WORKDIR /app
#
#COPY package*.json ./
#
#RUN npm install
#
#COPY . .
#
#RUN npm run build --prod
#
#FROM nginx:alpine
#
#COPY nginx.conf /etc/nginx/nginx.conf
#
#COPY --from=build-stage /app/dist/booksphere-ui /usr/share/nginx/html
#
#EXPOSE 80

# Stage 0, "build-stage", based on Node.js, to build and compile the frontend
FROM node:18 as build-stage

WORKDIR /app

COPY package*.json /app/

RUN npm install

COPY ./ /app/

RUN npm run build --prod

# Stage 1, based on Nginx, to have only the compiled app, ready for production with Nginx
FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf

COPY --from=build-stage /app/dist/ /usr/share/nginx/html/

EXPOSE 80
