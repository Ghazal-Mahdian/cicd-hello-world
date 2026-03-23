pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'your-dockerhub-username/cicd-hello-world:latest'
        SONAR_HOST = 'http://sonarqube:9000'
        DOCKER_USERNAME = credentials('ghazalcsudh')
        DOCKER_PASSWORD = credentials('g2H;EB%+ujAdtj9')
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
                        -Dsonar.login=squ_c57711dc4917130a410c415bb93f432d9456348b
                "
            '''
        }
    }

        stage('Build Docker Image') {
        steps {
            sh '''
                docker build -t ghazalcsudh/cicd-hello-world:latest .
            '''
        }
    }
    
    stage('Push to Docker Hub') {
        steps {
            sh '''
                echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                docker push ghazalcsudh/cicd-hello-world:latest
            '''
        }
    }
    
    stage('Deploy to Kubernetes') {
        steps {
            sh '''
                kubectl apply -f deployment.yaml
                kubectl rollout status deployment/java-app
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

