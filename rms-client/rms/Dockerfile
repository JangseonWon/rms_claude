FROM node:20

WORKDIR /app

COPY package.json /app/package.json
COPY tsconfig.json /app/tsconfig.json
COPY next.config.mjs /app/next.config.mjs
COPY .eslintrc.json /app/.eslintrc.json
COPY .env /app/.env

RUN npm install

COPY src /app/src
COPY public /app/public

RUN npm run build && \
    npm cache clean --force && \
    rm -rf /app/src /app/public


EXPOSE 3000

CMD ["npm", "start"]