FROM node:20

WORKDIR /app

COPY src /app/src
COPY public /app/public
COPY next.config.mjs /app/next.config.mjs
COPY .eslintrc.json /app/.eslintrc.json
COPY .env /app/.env
COPY package.json /app/package.json
COPY tsconfig.json /app/tsconfig.json
RUN npm install && npm run build

EXPOSE 3000

CMD ["npm", "start"]