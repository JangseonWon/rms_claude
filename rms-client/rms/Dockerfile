FROM node:20

WORKDIR /app

COPY src /app/src
COPY public /app/public
COPY package.json /app/package.json
COPY tsconfig.json /app/tsconfig.json
COPY next.config.mjs /app/next.config.mjs
COPY .eslintrc.json /app/.eslintrc.json

ARG AUTH_SECRET
ARG BASE_URL

ENV AUTH_SECRET=$AUTH_SECRET
ENV BASE_URL=$BASE_URL

RUN npm install && npm run build

EXPOSE 3000

CMD ["npm", "start"]