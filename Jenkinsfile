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

   stage('Security Scan (Trivy)') {
            agent {
                docker {
                    image 'ghcr.io/aquasecurity/trivy:latest'
                    args "-v /var/run/docker.sock:/var/run/docker.sock --entrypoint=''"
                }
            }
            steps {
                script {
                    sh "trivy image --severity HIGH,CRITICAL ghazalcsudh/cicd-hello-world:latest"
                }
            }
        }
    
    stage('Push to Docker Hub') {
        steps {
            sh '''
                echo "g2H;EB%+ujAdtj9" | docker login -u "ghazalcsudh" --password-stdin
                docker push ghazalcsudh/cicd-hello-world:latest
            '''
        }
    }
    
   stage('Deploy with Helm') {
        agent {
            docker {
                image 'alpine/helm:latest'
                args "-v /var/run/docker.sock:/var/run/docker.sock --entrypoint=''" 
            }
        }
        steps {
            withCredentials([file(credentialsId: 'kubeconfig-secret', variable: 'KUBECONFIG_FILE')]) {
                sh '''
                    export KUBECONFIG=${KUBECONFIG_FILE}
                    helm upgrade --install my-release ./helm/hello-world \
                        --set image.tag=latest \
                        --kube-insecure-skip-tls-verify
                '''
            }
        }
    }
    
    stage('Check Pods') {
        steps {
            withCredentials([file(credentialsId: 'kubeconfig-secret', variable: 'KUBECONFIG_FILE')]) {
                sh 'export KUBECONFIG=${KUBECONFIG_FILE} && kubectl get pods --insecure-skip-tls-verify'
            }
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

