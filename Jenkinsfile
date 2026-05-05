pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        DOCKERHUB_USERNAME    = 'nagendra99'
        IMAGE_NAME            = 'babu-app'
        IMAGE_TAG             = "1.0.${BUILD_NUMBER}"
        GITHUB_USERNAME       = 'nagendar47-hash'
        GITHUB_REPO           = 'babu-app'
    }

    stages {

        stage('Checkout') {
            steps {
                // Public repo — no credentials needed
                git branch: 'main',
                    url: "https://github.com/nagendar47-hash/babu-app.git"
            }
        }

        stage('Build JAR') {
            steps {
                sh '''
                    export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-arm64
                    export MAVEN_HOME=/opt/apache-maven-3.9.13
                    export PATH=$MAVEN_HOME/bin:$JAVA_HOME/bin:$PATH
                    mvn clean package -DskipTests -B
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh """
                    docker build -t ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG} .
                    docker tag ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG} \
                               ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest
                """
            }
        }

        stage('Docker Push') {
            steps {
                sh """
                    echo ${DOCKERHUB_CREDENTIALS_PSW} | \
                    docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin
                    docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}
                    docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest
                """
            }
        }

        stage('Update Image Tag in GitHub') {
            steps {
                // Token needed — WRITE operation to public repo
                withCredentials([string(
                    credentialsId: 'github-token',
                    variable: 'GIT_TOKEN'
                )]) {
                    sh """
                        git config user.email "jenkins@example.com"
                        git config user.name "Jenkins"

                        sed -i 's|image: ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:.*|image: ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}|g' k8s/deployment.yaml

                        git add k8s/deployment.yaml
                        git diff --staged --quiet || git commit -m "ci: update image tag to ${IMAGE_TAG} [skip ci]"
                        git commit -m "ci: update image tag to ${IMAGE_TAG} [skip ci]"
                        git push https://${GITHUB_USERNAME}:${GIT_TOKEN}@github.com/${GITHUB_USERNAME}/${GITHUB_REPO}.git HEAD:main
                    """
                }
            }
        }

        stage('Cleanup') {
            steps {
                sh """
                    docker rmi ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG} || true
                    docker rmi ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest || true
                """
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline succeeded! ArgoCD will deploy the new image.'
        }
        failure {
            echo '❌ Pipeline failed! Check the logs.'
        }
    }
}
