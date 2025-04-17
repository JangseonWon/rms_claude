FROM node:20

WORKDIR /app

COPY src /app/src
COPY public /app/public
COPY package.json /app/package.json
COPY tsconfig.json /app/tsconfig.json
COPY next.config.mjs /app/next.config.mjs
COPY .eslintrc.json /app/.eslintrc.json
COPY .env /app/.env
COPY .env_dev /app/.env_dev

ARG BRANCH=default
RUN echo "🔍 BRANCH value is: $BRANCH" && \
    if [ "$BRANCH" = "feature" ]; then \
      echo "✅ Using .env_dev for feature branch" && \
      cp /app/.env_dev /app/.env; \
    else \
      true; \
    fi

RUN npm install && npm run build

EXPOSE 3000

CMD ["npm", "start"]