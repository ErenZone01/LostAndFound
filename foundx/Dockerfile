FROM node:20

WORKDIR /app

# Copier seulement les fichiers package.json et package-lock.json pour installer les dépendances
COPY package*.json ./
RUN npm install

# Copier le reste du code
COPY . .

# Construire l'application
RUN npm run build

# Exposer le port que Angular utilise
EXPOSE 4200

# Lancer le serveur Angular
CMD ["npm", "start", "--", "--host", "0.0.0.0"]
