pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'your-dockerhub-username/cicd-hello-world:latest'
        SONAR_HOST = 'http://sonarqube:9000'
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Ghazal-Mahdian/cicd-hello-world.git'
            }
        }

        stage('Build (Java 17)') {
            steps {
                sh '''
                    docker exec java17-builder bash -c "
                        rm -rf /app &&
                        mkdir /app
                    "
                    docker cp . java17-builder:/app
                    docker exec java17-builder bash -c "
                        cd /app &&
                        ./mvnw clean package -DskipTests || mvn clean package -DskipTests
                    "
                '''
            }
        }

        stage('Run Unit Tests (Java 11)') {
            steps {
                sh '''
                    docker exec java11-tester bash -c "
                        rm -rf /app &&
                        mkdir /app
                    "
                    docker cp . java11-tester:/app
                    docker exec java11-tester bash -c "
                        cd /app &&
                        ./mvnw test || mvn test
                    "
                '''
            }
        }

       stage('Code Analysis (Java 8)') {
        steps {
            sh '''
                docker exec java11-tester bash -c "
                    rm -rf /app &&
                    mkdir /app
                "
                docker cp . java11-tester:/app
                docker exec java11-tester bash -c "
                    cd /app &&
                    mvn sonar:sonar \
                        -Dsonar.host.url=http://sonarqube:9000 \
                        -Dsonar.login=admin \
                        -Dsonar.password=admin
                "
            '''
        }
    }

    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed — check the logs above.'
        }
    }
}

