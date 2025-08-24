# Clone le Projet
    bash`git clone https://github.com/ErenZone01/LostAndFound.git`

# Lancer Kafka 
- lancer kafka a la racine du projet avec les droits d'administrateur
`docker compose up -d`
- verfiez si kafka et zookeeper sont lancés avec les droits d'administrateur
`docker ps`
- si un des deux n'est pas activé alors eteignez les et recommencez le relancer kafka
`docker compose down`

# Lancer les microservices
- dans chaque répertoire lancer le microservice a sa racine danse l'ordre, exemple :
    -eureka
    -api
    -auth
    -user
    -post
    -match
    -notification
    -foundx

    bash`cd eureka && mvn spring-boot:run`

# lancer le front
- aller à la racine de foundx et installer les dependences:
    bash
        `cd foundx && npm install`
- Lancer le projet :
    bash
    `ng serve`
